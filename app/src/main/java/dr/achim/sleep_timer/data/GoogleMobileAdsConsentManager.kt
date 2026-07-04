package dr.achim.sleep_timer.data

import android.app.Activity
import android.content.Context
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import dr.achim.sleep_timer.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * The Google Mobile Ads SDK provides the User Messaging Platform (UMP) SDK to help you manage ads
 * consent. For more information, see [Help users manage their privacy](https://developers.google.com/admob/android/privacy).
 */
class GoogleMobileAdsConsentManager(
    private val context: Context,
    private val billingRepository: BillingRepository
) {
    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(context)

    /** Interface definition for a callback to be invoked when consent gathering is complete. */
    fun interface OnConsentGatheringCompleteListener {
        fun consentGatheringComplete(error: FormError?)
    }

    private val isMobileAdsInitializeCalled = AtomicBoolean(false)

    /**
     * Helper variable to determine if the app can request ads.
     */
    val canRequestAds: Boolean
        get() = consentInformation.canRequestAds()

    /**
     * Helper variable to determine if the privacy options form is required.
     */
    val isPrivacyOptionsRequired: Boolean
        get() =
            consentInformation.privacyOptionsRequirementStatus ==
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    /**
     * Helper method to call the UMP SDK methods to request consent information and load/show a
     * consent form if necessary.
     */
    fun gatherConsent(
        activity: Activity,
        onConsentGatheringCompleteListener: OnConsentGatheringCompleteListener,
    ) {
        val params = ConsentRequestParameters.Builder().build()

        // Requesting an update to consent information should be called on every app launch.
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                loadAndShowConsentFormIfRequired(activity, onConsentGatheringCompleteListener)
            },
            { requestConsentError ->
                onConsentGatheringCompleteListener.consentGatheringComplete(requestConsentError)
            }
        )
    }

    private fun loadAndShowConsentFormIfRequired(
        activity: Activity,
        onConsentGatheringCompleteListener: OnConsentGatheringCompleteListener,
    ) {
        UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
            onConsentGatheringCompleteListener.consentGatheringComplete(formError)
        }
    }

    /**
     * Helper method to call the UMP SDK method to show the privacy options form.
     */
    fun showPrivacyOptionsForm(
        activity: Activity,
        onConsentFormDismissedListener: (error: FormError?) -> Unit
    ) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity, onConsentFormDismissedListener)
    }

    /**
     * Initializes the Google Mobile Ads SDK if the user is not a Pro user and has given consent.
     */
    fun initializeMobileAdsSdk() {
        if (!canRequestAds) {
            return
        }

        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            if (!billingRepository.isPro.first()) {
                // Initialize the Google Mobile Ads SDK.
                val initConfig = InitializationConfig.Builder(BuildConfig.ADMOB_APP_ID).build()
                MobileAds.initialize(context, initConfig)
            } else {
                isMobileAdsInitializeCalled.set(false)
            }
        }
    }
}
