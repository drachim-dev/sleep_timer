package dr.achim.sleep_timer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.NavKey
import dr.achim.sleep_timer.data.BillingRepository
import dr.achim.sleep_timer.data.GoogleMobileAdsConsentManager
import dr.achim.sleep_timer.data.SettingsRepository
import dr.achim.sleep_timer.model.ThemeMode
import dr.achim.sleep_timer.navigation.HomeKey
import dr.achim.sleep_timer.navigation.LocalSharedTransitionScope
import dr.achim.sleep_timer.navigation.Navigation
import dr.achim.sleep_timer.navigation.OnboardingKey
import dr.achim.sleep_timer.navigation.PaywallKey
import dr.achim.sleep_timer.navigation.TimerKey
import dr.achim.sleep_timer.service.TimerService
import dr.achim.sleep_timer.ui.theme.AppTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

val LocalIsPro = compositionLocalOf { false }

class MainActivity : ComponentActivity() {
    private val settingsRepository: SettingsRepository by inject()
    private val billingRepository: BillingRepository by inject()
    private val adsConsentManager: GoogleMobileAdsConsentManager by inject()
    private var isAppReady = false
    private var initialBackStack by mutableStateOf<List<NavKey>?>(null)
    private var openPaywallEvent by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { !isAppReady }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            val isProDeferred = async { billingRepository.awaitIsPro() }
            val isFirstLaunchDeferred = async { settingsRepository.isFirstLaunch.firstOrNull() ?: true }

            val isPro = isProDeferred.await()
            val isFirstLaunch = isFirstLaunchDeferred.await()

            initialBackStack = when {
                intent?.action == TimerService.ACTION_OPEN_TIMER -> listOf(HomeKey, TimerKey(null))
                intent?.action == ACTION_UPGRADE_PRO -> listOf(HomeKey, PaywallKey)
                isFirstLaunch -> listOf(OnboardingKey)
                else -> listOf(HomeKey)
            }

            isAppReady = true

            if (!isPro) {
                adsConsentManager.initialize(this@MainActivity)
            }
        }

        setContent {
            val themeMode by settingsRepository.themeMode.collectAsStateWithLifecycle(initialValue = ThemeMode.default)
            val isProState by billingRepository.isPro.collectAsStateWithLifecycle(initialValue = false)

            initialBackStack?.let { backStack ->
                AppTheme(themeMode = themeMode) {
                    SharedTransitionLayout {
                        CompositionLocalProvider(
                            LocalSharedTransitionScope provides this,
                            LocalIsPro provides isProState,
                        ) {
                            Navigation(
                                initialBackStack = backStack,
                                openPaywallEvent = openPaywallEvent,
                                onOpenPaywallHandled = { openPaywallEvent = false },
                                sharedTransitionScope = this
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.action == ACTION_UPGRADE_PRO) {
            openPaywallEvent = true
        }
    }

    companion object {
        const val ACTION_UPGRADE_PRO = "dr.achim.sleep_timer.ACTION_UPGRADE_PRO"
    }
}
