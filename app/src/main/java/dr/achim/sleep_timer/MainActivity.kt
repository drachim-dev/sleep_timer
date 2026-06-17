package dr.achim.sleep_timer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dr.achim.sleep_timer.data.SettingsRepository
import dr.achim.sleep_timer.model.ThemeMode
import dr.achim.sleep_timer.navigation.HomeKey
import dr.achim.sleep_timer.navigation.LocalSharedTransitionScope
import dr.achim.sleep_timer.navigation.Navigation
import dr.achim.sleep_timer.navigation.TimerKey
import dr.achim.sleep_timer.service.TimerService
import dr.achim.sleep_timer.ui.theme.AppTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val settingsRepository: SettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by settingsRepository.themeMode.collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)

            val initialBackStack = if (intent?.action == TimerService.ACTION_OPEN_TIMER) {
                listOf(HomeKey, TimerKey(null))
            } else {
                listOf(HomeKey)
            }

            AppTheme(themeMode = themeMode) {
                SharedTransitionLayout {
                    CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                        Navigation(
                            initialBackStack = initialBackStack,
                            sharedTransitionScope = this
                        )
                    }
                }
            }
        }
    }
}

