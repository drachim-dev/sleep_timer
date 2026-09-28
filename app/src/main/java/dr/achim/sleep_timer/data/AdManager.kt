package dr.achim.sleep_timer.data

import android.content.Context
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdEventCallback
import dr.achim.sleep_timer.BuildConfig
import dr.achim.sleep_timer.common.findActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AdManager(
    private val settingsRepository: SettingsRepository,
    private val billingRepository: BillingRepository,
    private val consentManager: GoogleMobileAdsConsentManager,
) {

    companion object {
        private const val AD_FREQUENCY = 5
        private const val AD_UNIT_ID = BuildConfig.ADMOB_INTERSTITIAL_UNIT_ID
    }

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var interstitialAd: InterstitialAd? = null
    private var isLoadingAd = false

    private val canShowAds: Boolean
        get() = consentManager.canRequestAds && !billingRepository.isPro.value

    init {
        scope.launch {
            consentManager.state.collect { state ->
                if (state == AdsInitState.Ready) {
                    mayPreload()
                }
            }
        }
    }

    /**
     * Preloads an interstitial when the next timer start should show one.
     *
     * Returns true when this is an ad turn, regardless of whether the ad
     * has actually finished loading.
     */
    suspend fun mayPreload(): Boolean {
        val isAdTurn = isNextAdTurn()

        if (isAdTurn) {
            loadAd()
        }

        return isAdTurn
    }

    /**
     * Shows the preloaded interstitial if this timer start is an ad turn.
     * [onAdClosed] is always invoked exactly once.
     */
    suspend fun showAd(context: Context, onAdClosed: () -> Unit) {
        if (!canShowAds) {
            // Keep the timer-start counter progressing even when ads are disabled.
            interstitialAd = null
            settingsRepository.incrementAndGetTimerStartCount()
            onAdClosed()
            return
        }

        val currentCount = settingsRepository.timerStartCount.firstOrNull() ?: 0
        val nextCount = currentCount + 1
        val isAdTurn = nextCount > 0 && nextCount % AD_FREQUENCY == 0

        if (!isAdTurn) {
            settingsRepository.incrementAndGetTimerStartCount()
            onAdClosed()
            return
        }

        val activity = context.findActivity()
        val ad = interstitialAd

        if (activity == null || ad == null) {
            // Ad turn reached, but ad is not ready. Do not increment counter so
            // the ad turn is preserved for the next timer start once loaded.
            loadAd()
            onAdClosed()
            return
        }

        settingsRepository.incrementAndGetTimerStartCount()

        // An interstitial can only be shown once.
        interstitialAd = null

        ad.adEventCallback = object : InterstitialAdEventCallback {
            override fun onAdDismissedFullScreenContent() {
                onAdClosed()
            }

            override fun onAdFailedToShowFullScreenContent(
                fullScreenContentError: FullScreenContentError,
            ) {
                onAdClosed()
            }
        }

        ad.show(activity)
    }

    private suspend fun isNextAdTurn(): Boolean {
        val count = settingsRepository.timerStartCount.firstOrNull() ?: 0
        return (count + 1) % AD_FREQUENCY == 0
    }

    private fun loadAd() {
        if (!canShowAds || AD_UNIT_ID.isEmpty()) {
            return
        }

        if (interstitialAd != null || isLoadingAd) {
            return
        }

        isLoadingAd = true

        val request = AdRequest.Builder(AD_UNIT_ID).build()

        InterstitialAd.load(request, object : AdLoadCallback<InterstitialAd> {
                override fun onAdLoaded(ad: InterstitialAd) {
                    isLoadingAd = false
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    isLoadingAd = false
                    interstitialAd = null
                }
            }
        )
    }
}