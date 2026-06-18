package dr.achim.sleep_timer.data

import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.getCustomerInfoWith
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import dr.achim.sleep_timer.model.Entitlement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class BillingRepository {
    private val _customerInfo = MutableStateFlow<CustomerInfo?>(null)
    val customerInfo: StateFlow<CustomerInfo?> = _customerInfo.asStateFlow()

    val isPro = _customerInfo.map { info ->
        info?.entitlements?.get(Entitlement.Pro.id)?.isActive == true
    }

    init {
        loadCustomerInfo()
        Purchases.sharedInstance.updatedCustomerInfoListener = UpdatedCustomerInfoListener { info ->
            _customerInfo.value = info
        }
    }

    private fun loadCustomerInfo() {
        Purchases.sharedInstance.getCustomerInfoWith(
            onError = { /* Log error if needed */ }
        ) { info ->
            _customerInfo.value = info
        }
    }

    fun clear() {
        Purchases.sharedInstance.updatedCustomerInfoListener = null
    }
}
