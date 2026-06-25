package dr.achim.sleep_timer.presentation.settings

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revenuecat.purchases.ProductType
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.getProductsWith
import com.revenuecat.purchases.models.StoreProduct
import com.revenuecat.purchases.purchaseWith
import dr.achim.sleep_timer.common.TAG
import dr.achim.sleep_timer.data.BillingRepository
import dr.achim.sleep_timer.data.TimerController
import dr.achim.sleep_timer.domain.usecase.CheckTimerPermissionsUseCase
import dr.achim.sleep_timer.domain.usecase.GetSettingsUseCase
import dr.achim.sleep_timer.domain.usecase.UpdateSettingsUseCase
import dr.achim.sleep_timer.model.AppSettings
import dr.achim.sleep_timer.model.Entitlement
import dr.achim.sleep_timer.model.Product
import dr.achim.sleep_timer.model.PurchaseEvent
import dr.achim.sleep_timer.model.ThemeMode
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class StoreProductUiModel(
    val product: StoreProduct,
    val isPurchased: Boolean
)

class SettingsViewModel(
    private val timerController: TimerController,
    billingRepository: BillingRepository,
    getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val checkTimerPermissionsUseCase: CheckTimerPermissionsUseCase,
) : ViewModel() {

    private val eventChannel = Channel<PurchaseEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    private val settings = getSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings()
        )

    val themeMode: StateFlow<ThemeMode> = settings
        .map { it.themeMode }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings().themeMode
        )

    val glowEffectEnabled: StateFlow<Boolean> = settings
        .map { it.glowEffectEnabled }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings().glowEffectEnabled
        )

    val glowIntensity: StateFlow<Float> = settings
        .map { it.glowIntensity }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings().glowIntensity
        )

    val extendOnShake: StateFlow<Boolean> = settings
        .map { it.extendOnShake }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings().extendOnShake
        )

    val extendOnShakeMinutes: StateFlow<Int> = settings
        .map { it.extendOnShakeMinutes }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings().extendOnShakeMinutes
        )

    private val _isDeviceAdminEnabled = MutableStateFlow(checkTimerPermissionsUseCase().isDeviceAdminEnabled)
    val isDeviceAdminEnabled: StateFlow<Boolean> = _isDeviceAdminEnabled.asStateFlow()

    private val _hasNotificationAccess = MutableStateFlow(checkTimerPermissionsUseCase().hasNotificationAccess)
    val hasNotificationAccess: StateFlow<Boolean> = _hasNotificationAccess.asStateFlow()

    private val _products = MutableStateFlow<List<StoreProduct>>(emptyList())

    val productUiModels: StateFlow<List<StoreProductUiModel>> = combine(_products, billingRepository.customerInfo) { products, info ->
        products.map { product ->
            val productType = Product.entries.find { it.id == product.id }
            val isConsumable = productType?.isConsumable ?: false

            val isPurchased = when (product.id) {
                Product.RemoveAds.id -> info?.entitlements?.get(Entitlement.Pro.id)?.isActive == true
                else -> info?.allPurchasedProductIds?.contains(product.id) == true && !isConsumable
            }

            StoreProductUiModel(product, isPurchased)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    init {
        loadProducts()
    }

    private fun loadProducts() {
        val productIdentifiers = Product.entries.map { it.id }
        Purchases.sharedInstance.getProductsWith(
            productIds = productIdentifiers,
            type = ProductType.INAPP,
            onError = { error ->
                Log.e(TAG, "Code ${error.code}: ${error.message}")
            }
        ) { storeProducts ->
            _products.value = storeProducts
        }
    }

    fun onAction(action: SettingsUiAction) {
        when (action) {
            is SettingsUiAction.RefreshDeviceAdminStatus -> refreshDeviceAdminStatus()
            is SettingsUiAction.RefreshDndStatus -> refreshDndStatus()
            is SettingsUiAction.DisableDeviceAdmin -> disableDeviceAdmin()
            is SettingsUiAction.DisableNotificationAccess -> disableNotificationAccess()
            is SettingsUiAction.SetThemeMode -> setThemeMode(action.themeMode)
            is SettingsUiAction.SetGlowEffectEnabled -> setGlowEffectEnabled(action.enabled)
            is SettingsUiAction.SetGlowIntensity -> setGlowIntensity(action.intensity)
            is SettingsUiAction.SetExtendOnShake -> setExtendOnShake(action.enabled)
            is SettingsUiAction.SetExtendOnShakeMinutes -> setExtendOnShakeMinutes(action.minutes)
            is SettingsUiAction.PurchaseProduct -> purchaseProduct(action.activity, action.product)
        }
    }

    private fun purchaseProduct(activity: Activity, product: StoreProduct) {
        Purchases.sharedInstance.purchaseWith(
            PurchaseParams.Builder(activity, product).build(),
            onError = { error, _ ->
                Log.e(TAG, "Code ${error.code}: ${error.message}")
                sendEvent(PurchaseEvent.PurchaseAborted)
            },
            onSuccess = { _, _ ->
                sendEvent(PurchaseEvent.PurchaseComplete)
            }
        )
    }

    private fun refreshDeviceAdminStatus(): Boolean {
        val enabled = checkTimerPermissionsUseCase().isDeviceAdminEnabled
        _isDeviceAdminEnabled.value = enabled
        return enabled
    }

    private fun refreshDndStatus(): Boolean {
        val enabled = checkTimerPermissionsUseCase().hasNotificationAccess
        _hasNotificationAccess.value = enabled
        return enabled
    }

    private fun disableDeviceAdmin() {
        timerController.removeActiveAdmin()
        _isDeviceAdminEnabled.value = false
    }

    private fun disableNotificationAccess() {
        _hasNotificationAccess.value = false
    }

    private fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            updateSettingsUseCase.setThemeMode(themeMode)
        }
    }

    private fun setGlowEffectEnabled(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.setGlowEffectEnabled(enabled)
        }
    }

    private fun setGlowIntensity(intensity: Float) {
        viewModelScope.launch {
            updateSettingsUseCase.setGlowIntensity(intensity)
        }
    }

    private fun setExtendOnShake(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.setExtendOnShake(enabled)
        }
    }

    private fun setExtendOnShakeMinutes(minutes: Int) {
        viewModelScope.launch {
            updateSettingsUseCase.setExtendOnShakeMinutes(minutes)
        }
    }

    private fun sendEvent(event: PurchaseEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}
