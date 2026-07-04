package dr.achim.sleep_timer.data

import android.app.Activity
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdEventCallback
import dr.achim.sleep_timer.BuildConfig
import kotlinx.coroutines.flow.firstOrNull

class AdManager(
    private val settingsRepository: SettingsRepository,
    private val consentManager: GoogleMobileAdsConsentManager,
) {
    companion object {
        private const val INTERSTITIAL_AD_UNIT_ID = BuildConfig.ADMOB_INTERSTITIAL_UNIT_ID
        private const val AD_FREQUENCY = 6
    }

    private var interstitialAd: InterstitialAd? = null

    private fun loadAd() {
        if (!consentManager.canRequestAds) return
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

    suspend fun shouldShowAd(isProUser: Boolean): Boolean {
        if (isProUser || !consentManager.canRequestAds) {
            interstitialAd = null
            settingsRepository.incrementAndGetTimerStartCount()
            return false
        }

        val count = settingsRepository.incrementAndGetTimerStartCount()
        return count > 0 && count % AD_FREQUENCY == 0
    }
    
    private suspend fun isNextAdTurn() : Boolean {
        val count = settingsRepository.timerStartCount.firstOrNull() ?: 0
        return (count + 1) % AD_FREQUENCY == 0
    }

    fun showAd(activity: Activity, onAdClosed: () -> Unit) {
        if (!consentManager.canRequestAds) {
            interstitialAd = null
            onAdClosed()
            return
        }

        val ad = interstitialAd
        if (ad != null) {
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