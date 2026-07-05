package dr.achim.sleep_timer.data

import android.util.Log
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.getCustomerInfoWith
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import dr.achim.sleep_timer.common.TAG
import dr.achim.sleep_timer.model.Entitlement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class BillingRepository {
    private val _customerInfo = MutableStateFlow<CustomerInfo?>(null)
    val customerInfo: StateFlow<CustomerInfo?> = _customerInfo.asStateFlow()

    private val _isPro = MutableStateFlow(false)
    val isPro: StateFlow<Boolean> = _isPro.asStateFlow()

    init {
        loadCustomerInfo()
        Purchases.sharedInstance.updatedCustomerInfoListener = UpdatedCustomerInfoListener { info ->
            updateInfo(info)
        }
    }

    private fun loadCustomerInfo() {
        Purchases.sharedInstance.getCustomerInfoWith(
            onError = { error -> Log.e(TAG, "Failed to load customer info: ${error.message}") }
        ) { info ->
            updateInfo(info)
        }
    }

    private fun updateInfo(info: CustomerInfo?) {
        _customerInfo.value = info
        _isPro.value = info?.entitlements?.get(Entitlement.Pro.id)?.isActive == true
    }

    suspend fun awaitCustomerInfo(): CustomerInfo? {
        _customerInfo.value?.let { return it }

        return suspendCancellableCoroutine { cont ->
            Purchases.sharedInstance.getCustomerInfoWith(
                onError = { error ->
                    Log.e(TAG, "Failed to load customer info: ${error.message}")
                    if (cont.isActive) cont.resume(null)
                }
            ) { info ->
                updateInfo(info)
                if (cont.isActive) cont.resume(info)
            }
        }
    }

    suspend fun awaitIsPro(): Boolean =
        awaitCustomerInfo()?.entitlements?.get(Entitlement.Pro.id)?.isActive == true
}