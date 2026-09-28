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
import kotlinx.coroutines.flow.firstOrNull

class AdManager(
    private val settingsRepository: SettingsRepository,
    private val billingRepository: BillingRepository,
    private val consentManager: GoogleMobileAdsConsentManager,
) {
    companion object {
        private const val INTERSTITIAL_AD_UNIT_ID = BuildConfig.ADMOB_INTERSTITIAL_UNIT_ID
        private const val AD_FREQUENCY = 6
    }

    private var interstitialAd: InterstitialAd? = null

    private val canShowAds: Boolean
        get() = consentManager.canRequestAds && !billingRepository.isPro.value

    private fun loadAd() {
        if (!canShowAds) return
        if (INTERSTITIAL_AD_UNIT_ID.isEmpty()) return
        if (interstitialAd != null) return

        val adRequest = AdRequest.Builder(INTERSTITIAL_AD_UNIT_ID).build()
        InterstitialAd.load(
            adRequest,
            object : AdLoadCallback<InterstitialAd> {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    suspend fun mayPreload(): Boolean {
        val isNextAdTurn = isNextAdTurn()

        if (isNextAdTurn) {
            loadAd()
        }

        return isNextAdTurn
    }

    suspend fun shouldShowAd(): Boolean {
        if (!canShowAds) {
            interstitialAd = null
            settingsRepository.incrementAndGetTimerStartCount()
            return false
        }

        val count = settingsRepository.incrementAndGetTimerStartCount()
        return count > 0 && count % AD_FREQUENCY == 0
    }

    private suspend fun isNextAdTurn(): Boolean {
        val count = settingsRepository.timerStartCount.firstOrNull() ?: 0
        return (count + 1) % AD_FREQUENCY == 0
    }

    fun showAd(context: Context, onAdClosed: () -> Unit) {
        if (!canShowAds) {
            interstitialAd = null
            onAdClosed()
            return
        }

        val activity = context.findActivity()
        val ad = interstitialAd
        if (ad != null && activity != null) {
            ad.adEventCallback = object : InterstitialAdEventCallback {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(fullScreenContentError: FullScreenContentError) {
                    interstitialAd = null
                    onAdClosed()
                }
            }
            ad.show(activity)
        } else {
            loadAd() // Try to be ready for the next turn/app start if we missed this one
            onAdClosed()
        }
    }
}