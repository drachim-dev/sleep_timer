package dr.achim.sleep_timer.data

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dr.achim.sleep_timer.BuildConfig
import kotlinx.coroutines.flow.firstOrNull

class AdManager(
    private val context: Context,
    private val settingsRepository: SettingsRepository,
) {
    companion object {
        private const val INTERSTITIAL_AD_UNIT_ID = BuildConfig.ADMOB_INTERSTITIAL_UNIT_ID
        private const val AD_FREQUENCY = 6
    }

    private var interstitialAd: InterstitialAd? = null

    private fun loadAd() {
        if (INTERSTITIAL_AD_UNIT_ID.isEmpty()) return
        if (interstitialAd != null) return

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
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
        if (isProUser) {
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
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
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