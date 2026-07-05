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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

sealed class AdsInitState {
    object NotStarted : AdsInitState()
    object CheckingPro : AdsInitState()
    object GatheringConsent : AdsInitState()
    object Initializing : AdsInitState()
    object Ready : AdsInitState()
    object SkippedPro : AdsInitState()
    data class Failed(val reason: String) : AdsInitState()
}

class GoogleMobileAdsConsentManager(
    private val context: Context,
    private val billingRepository: BillingRepository
) {
    private val consentInformation = UserMessagingPlatform.getConsentInformation(context)

    private val _state = MutableStateFlow<AdsInitState>(AdsInitState.NotStarted)
    val state: StateFlow<AdsInitState> = _state.asStateFlow()

    /** True only once consent is granted AND the Ads SDK has actually been initialized. */
    val canRequestAds: Boolean
        get() = _state.value == AdsInitState.Ready

    val isPrivacyOptionsRequired: Boolean
        get() = consentInformation.privacyOptionsRequirementStatus ==
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    suspend fun initialize(activity: Activity) {
        if (_state.value == AdsInitState.Ready || _state.value == AdsInitState.SkippedPro) return

        _state.value = AdsInitState.CheckingPro
        if (billingRepository.awaitIsPro()) {
            _state.value = AdsInitState.SkippedPro
            return
        }

        _state.value = AdsInitState.GatheringConsent
        val consentError = gatherConsent(activity)
        if (consentError != null) {
            _state.value = AdsInitState.Failed(consentError.message)
            return
        }

        if (!consentInformation.canRequestAds()) {
            _state.value = AdsInitState.Failed("Consent not granted")
            return
        }

        _state.value = AdsInitState.Initializing
        val initConfig = InitializationConfig.Builder(BuildConfig.ADMOB_APP_ID).build()
        MobileAds.initialize(context, initConfig)
        _state.value = AdsInitState.Ready
    }

    /** Wire to a "Privacy Options" row in Settings — the GDPR consent-revocation link. */
    fun showPrivacyOptionsForm(activity: Activity, onDismissed: (FormError?) -> Unit = {}) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError ->
            onDismissed(formError)
            _state.value = if (consentInformation.canRequestAds()) {
                AdsInitState.Ready
            } else {
                AdsInitState.Failed("Consent revoked")
            }
        }
    }

    private suspend fun gatherConsent(activity: Activity): FormError? {
        val updateError = requestConsentInfoUpdate(activity)
        if (updateError != null) return updateError
        return loadAndShowConsentFormIfRequired(activity)
    }

    private suspend fun requestConsentInfoUpdate(activity: Activity): FormError? =
        suspendCancellableCoroutine { cont ->
            val params = ConsentRequestParameters.Builder().build()
            consentInformation.requestConsentInfoUpdate(
                activity,
                params,
                { cont.resume(null) },
                { error -> cont.resume(error) }
            )
        }

    private suspend fun loadAndShowConsentFormIfRequired(activity: Activity): FormError? =
        suspendCancellableCoroutine { cont ->
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { error ->
                cont.resume(error)
            }
        }
}