package dr.achim.sleep_timer.data

import android.app.Activity
import android.util.Log
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.ProductType
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.getCustomerInfoWith
import com.revenuecat.purchases.getProductsWith
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import com.revenuecat.purchases.models.StoreProduct
import com.revenuecat.purchases.purchaseWith
import com.revenuecat.purchases.restorePurchasesWith
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

    suspend fun getProducts(productIds: List<String>, type: ProductType = ProductType.INAPP): List<StoreProduct> =
        suspendCancellableCoroutine { cont ->
            Purchases.sharedInstance.getProductsWith(
                productIds = productIds,
                type = type,
                onError = { error ->
                    Log.e(TAG, "Failed to get products: ${error.message}")
                    if (cont.isActive) cont.resume(emptyList())
                }
            ) { storeProducts ->
                if (cont.isActive) cont.resume(storeProducts)
            }
        }

    suspend fun purchase(activity: Activity, product: StoreProduct): Result<Unit> =
        suspendCancellableCoroutine { cont ->
            Purchases.sharedInstance.purchaseWith(
                PurchaseParams.Builder(activity, product).build(),
                onError = { error, userCancelled ->
                    if (userCancelled) {
                        if (cont.isActive) cont.resume(Result.failure(Exception("User cancelled")))
                    } else {
                        Log.e(TAG, "Purchase failed: ${error.message}")
                        if (cont.isActive) cont.resume(Result.failure(Exception(error.message)))
                    }
                },
                onSuccess = { _, info ->
                    updateInfo(info)
                    if (cont.isActive) cont.resume(Result.success(Unit))
                }
            )
        }

    suspend fun restorePurchases(): Result<CustomerInfo> =
        suspendCancellableCoroutine { cont ->
            Purchases.sharedInstance.restorePurchasesWith(
                onError = { error ->
                    Log.e(TAG, "Restore failed: ${error.message}")
                    if (cont.isActive) cont.resume(Result.failure(Exception(error.message)))
                },
                onSuccess = { info ->
                    updateInfo(info)
                    if (cont.isActive) cont.resume(Result.success(info))
                }
            )
        }
}