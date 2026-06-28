package dr.achim.sleep_timer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import dr.achim.sleep_timer.data.BillingRepository
import dr.achim.sleep_timer.data.SettingsRepository
import dr.achim.sleep_timer.model.ThemeMode
import dr.achim.sleep_timer.navigation.HomeKey
import dr.achim.sleep_timer.navigation.LocalSharedTransitionScope
import dr.achim.sleep_timer.navigation.Navigation
import dr.achim.sleep_timer.navigation.OnboardingKey
import dr.achim.sleep_timer.navigation.TimerKey
import dr.achim.sleep_timer.service.TimerService
import dr.achim.sleep_timer.ui.theme.AppTheme
import kotlinx.coroutines.flow.firstOrNull
import org.koin.android.ext.android.inject

val LocalIsPro = compositionLocalOf { false }

class MainActivity : ComponentActivity() {
    private val settingsRepository: SettingsRepository by inject()
    private val billingRepository: BillingRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by settingsRepository.themeMode.collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)
            val isPro by billingRepository.isPro.collectAsStateWithLifecycle(initialValue = false)

            val initialBackStack by produceState<List<NavKey>?>(initialValue = null) {
                val isFirstLaunch = settingsRepository.isFirstLaunch.firstOrNull() ?: true
                value = when {
                    intent?.action == TimerService.ACTION_OPEN_TIMER -> listOf(HomeKey, TimerKey(null))
                    isFirstLaunch -> listOf(OnboardingKey)
                    else -> listOf(HomeKey)
                }
            }

            initialBackStack?.let { backStack ->
                AppTheme(themeMode = themeMode) {
                    SharedTransitionLayout {
                        CompositionLocalProvider(
                            LocalSharedTransitionScope provides this,
                            LocalIsPro provides isPro
                        ) {
                            Navigation(
                                initialBackStack = backStack,
                                sharedTransitionScope = this
                            )
                        }
                    }
                }
            }
        }
    }
}