package dr.achim.sleep_timer.presentation.paywall

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dr.achim.sleep_timer.R
import dr.achim.sleep_timer.common.UiMessageManager
import dr.achim.sleep_timer.data.BillingRepository
import dr.achim.sleep_timer.model.Entitlement
import dr.achim.sleep_timer.model.Product
import dr.achim.sleep_timer.model.PurchaseEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class PaywallViewModel(
    private val billingRepository: BillingRepository,
    private val uiMessageManager: UiMessageManager,
) : ViewModel() {

    private val eventChannel = Channel<PurchaseEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    private val _price = MutableStateFlow("")
    val price: StateFlow<String> = _price

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            val storeProducts = billingRepository.getProducts(listOf(Product.RemoveAds.id))
            _price.value = storeProducts.find { it.id == Product.RemoveAds.id }?.price?.formatted ?: ""
        }
    }

    fun purchase(activity: Activity?) {
        if (activity == null) return

        viewModelScope.launch {
            val storeProducts = billingRepository.getProducts(listOf(Product.RemoveAds.id))
            val product = storeProducts.find { it.id == Product.RemoveAds.id } ?: return@launch

            billingRepository.purchase(activity, product)
                .onSuccess {
                    uiMessageManager.emitMessage(R.string.settings_purchase_success)
                    sendEvent(PurchaseEvent.PurchaseComplete)
                }
                .onFailure { error ->
                    if (error.message != "User cancelled") {
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
                        uiMessageManager.emitMessage(R.string.settings_restore_success)
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
