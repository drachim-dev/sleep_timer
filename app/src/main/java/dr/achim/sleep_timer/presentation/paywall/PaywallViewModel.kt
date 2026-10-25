package dr.achim.sleep_timer.presentation.paywall

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revenuecat.purchases.models.StoreProduct
import dr.achim.sleep_timer.R
import dr.achim.sleep_timer.common.UiMessageManager
import dr.achim.sleep_timer.data.BillingRepository
import dr.achim.sleep_timer.domain.usecase.UpdateSettingsUseCase
import dr.achim.sleep_timer.model.Entitlement
import dr.achim.sleep_timer.model.Product
import dr.achim.sleep_timer.model.PurchaseEvent
import dr.achim.sleep_timer.presentation.settings.StoreProductUiModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PaywallViewModel(
    private val billingRepository: BillingRepository,
    private val uiMessageManager: UiMessageManager,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
) : ViewModel() {

    private val eventChannel = Channel<PurchaseEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    private val _product = MutableStateFlow<StoreProduct?>(null)

    val productUiModel: StateFlow<StoreProductUiModel?> = _product.mapNotNull { product ->
        product?.let {
            StoreProductUiModel(
                id = product.id,
                title = product.name,
                description = product.description,
                price = product.price.formatted,
                isPurchased = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _product.value = billingRepository
                .getProducts(listOf(Product.Pro.id))
                .find { it.id == Product.Pro.id }
        }
    }

    fun markWhatsNewSeen() {
        viewModelScope.launch {
            updateSettingsUseCase.setShowWhatsNewPro(false)
        }
    }

    fun purchase(activity: Activity?, productId: String) {
        val product = _product.value
        if (activity == null || product?.id == productId) return

        viewModelScope.launch {
            val storeProducts = billingRepository.getProducts(listOf(Product.Pro.id))
            val product = storeProducts.find { it.id == Product.Pro.id } ?: return@launch

            billingRepository.purchase(activity, product)
                .onSuccess {
                    updateSettingsUseCase.setShowWhatsNewPro(false)
                    uiMessageManager.emitMessage(R.string.paywall_purchase_success)
                    sendEvent(PurchaseEvent.PurchaseComplete)
                }
                .onFailure { error ->
                    if (error.message != "User canceled") {
                        sendEvent(PurchaseEvent.PurchaseError)
                    }
                }
        }
    }

    fun restorePurchases() {
        viewModelScope.launch {
            billingRepository.restorePurchases()
                .onSuccess { info ->
                    if (info.entitlements[Entitlement.Pro.id]?.isActive == true) {
                        updateSettingsUseCase.setShowWhatsNewPro(false)
                        uiMessageManager.emitMessage(R.string.paywall_restore_success)
                        sendEvent(PurchaseEvent.RestoreSuccess)
                    } else {
                        sendEvent(PurchaseEvent.RestoreError)
                    }
                }
                .onFailure {
                    sendEvent(PurchaseEvent.RestoreError)
                }
        }
    }

    private fun sendEvent(event: PurchaseEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}
