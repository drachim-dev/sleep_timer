package dr.achim.sleep_timer.presentation.settings

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revenuecat.purchases.models.StoreProduct
import dr.achim.sleep_timer.R
import dr.achim.sleep_timer.common.TAG
import dr.achim.sleep_timer.common.UiMessageManager
import dr.achim.sleep_timer.data.BillingRepository
import dr.achim.sleep_timer.data.GoogleMobileAdsConsentManager
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
    val id: String,
    val title: String,
    val description: String,
    val price: String,
    val isPurchased: Boolean
)

class SettingsViewModel(
    private val timerController: TimerController,
    private val googleMobileAdsConsentManager: GoogleMobileAdsConsentManager,
    private val billingRepository: BillingRepository,
    getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val checkTimerPermissionsUseCase: CheckTimerPermissionsUseCase,
    private val uiMessageManager: UiMessageManager,
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

    val lightsOffDelay: StateFlow<Boolean> = settings
        .map { it.lightsOffDelay }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings().lightsOffDelay
        )

    val lightsOffDelaySeconds: StateFlow<Int> = settings
        .map { it.lightsOffDelaySeconds }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings().lightsOffDelaySeconds
        )

    private val _isDeviceAdminEnabled = MutableStateFlow(false)
    val isDeviceAdminEnabled: StateFlow<Boolean> = _isDeviceAdminEnabled.asStateFlow()

    private val _hasNotificationAccess = MutableStateFlow(false)
    val hasNotificationAccess: StateFlow<Boolean> = _hasNotificationAccess.asStateFlow()

    private val _products = MutableStateFlow<List<StoreProduct>>(emptyList())
    private val _purchasedProductIds = MutableStateFlow<Set<String>>(emptySet())

    val productUiModels: StateFlow<List<StoreProductUiModel>> = combine(
        _products,
        _purchasedProductIds
    ) { products, purchasedIds ->
        products.map { product ->
            StoreProductUiModel(
                id = product.id,
                title = product.name,
                description = product.description,
                price = product.price.formatted,
                isPurchased = purchasedIds.contains(product.id)
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val isPrivacyOptionsRequired: Boolean
        get() = googleMobileAdsConsentManager.isPrivacyOptionsRequired

    init {
        loadProducts()
        observePurchases()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            val productIds = listOf(
                Product.Donation.id,
                Product.RemoveAds.id
            )
            _products.value = billingRepository.getProducts(productIds)
        }
    }

    private fun observePurchases() {
        viewModelScope.launch {
            billingRepository.customerInfo.collect { customerInfo ->
                val purchased = mutableSetOf<String>()
                if (customerInfo?.entitlements?.get(Entitlement.Pro.id)?.isActive == true) {
                    purchased.add(Product.RemoveAds.id)
                }
                customerInfo?.nonSubscriptionTransactions?.forEach { transaction ->
                    purchased.add(transaction.productIdentifier)
                }
                _purchasedProductIds.value = purchased
            }
        }
    }

    fun onAction(action: SettingsUiAction) {
        when (action) {
            is SettingsUiAction.SetThemeMode -> setThemeMode(action.themeMode)
            is SettingsUiAction.SetGlowEffectEnabled -> setGlowEffectEnabled(action.enabled)
            is SettingsUiAction.SetGlowIntensity -> setGlowIntensity(action.intensity)
            is SettingsUiAction.SetExtendOnShake -> setExtendOnShake(action.enabled)
            is SettingsUiAction.SetExtendOnShakeMinutes -> setExtendOnShakeMinutes(action.minutes)
            is SettingsUiAction.SetLightsOffDelay -> setLightsOffDelay(action.enabled)
            is SettingsUiAction.SetLightsOffDelaySeconds -> setLightsOffDelaySeconds(action.seconds)
            is SettingsUiAction.PurchaseProduct -> purchase(action.activity, action.productId)
            SettingsUiAction.RestorePurchases -> restorePurchases()
            is SettingsUiAction.ShowPrivacyOptions -> showPrivacyOptions(action.activity)
            SettingsUiAction.RefreshDeviceAdminStatus -> refreshDeviceAdminStatus()
            is SettingsUiAction.RefreshDndStatus -> refreshDndStatus()
            SettingsUiAction.DisableDeviceAdmin -> disableDeviceAdmin()
            SettingsUiAction.DisableNotificationAccess -> disableNotificationAccess()
        }
    }

    private fun showPrivacyOptions(activity: Activity?) {
        if (activity == null) return
        googleMobileAdsConsentManager.showPrivacyOptionsForm(activity) { error ->
            if (error != null) {
                Log.e(TAG, "Failed to show privacy options: ${error.message}")
            }
        }
    }

    fun purchase(activity: Activity?, productId: String) {
        if (activity == null) return
        val product = _products.value.find { it.id == productId } ?: return

        viewModelScope.launch {
            billingRepository.purchase(activity, product)
                .onSuccess {
                    uiMessageManager.emitMessage(R.string.settings_purchase_success)
                    sendEvent(PurchaseEvent.PurchaseComplete)
                }
                .onFailure { error ->
                    if (error.message != "User cancelled") {
                        uiMessageManager.emitMessage(R.string.error_purchase_failure)
                        sendEvent(PurchaseEvent.PurchaseError)
                    }
                }
        }
    }

    private fun restorePurchases() {
        viewModelScope.launch {
            billingRepository.restorePurchases()
                .onSuccess { info ->
                    if (info.entitlements[Entitlement.Pro.id]?.isActive == true) {
                        uiMessageManager.emitMessage(R.string.settings_restore_success)
                        sendEvent(PurchaseEvent.RestoreSuccess)
                    } else {
                        uiMessageManager.emitMessage(R.string.error_restore_failure)
                        sendEvent(PurchaseEvent.RestoreError)
                    }
                }
                .onFailure {
                    uiMessageManager.emitMessage(R.string.error_restore_failure)
                    sendEvent(PurchaseEvent.RestoreError)
                }
        }
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

    private fun setLightsOffDelay(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.setLightsOffDelay(enabled)
        }
    }

    private fun setLightsOffDelaySeconds(seconds: Int) {
        viewModelScope.launch {
            updateSettingsUseCase.setLightsOffDelaySeconds(seconds)
        }
    }

    private fun sendEvent(event: PurchaseEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}
