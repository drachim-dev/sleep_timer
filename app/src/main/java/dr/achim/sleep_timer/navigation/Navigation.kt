package dr.achim.sleep_timer.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dr.achim.sleep_timer.common.UiMessageManager
import dr.achim.sleep_timer.data.SettingsRepository
import dr.achim.sleep_timer.model.HueActionSource
import dr.achim.sleep_timer.presentation.home.HomeScreen
import dr.achim.sleep_timer.presentation.hue.HueDiscoveryScreen
import dr.achim.sleep_timer.presentation.hue.RoomSelectionScreen
import dr.achim.sleep_timer.presentation.onboarding.OnboardingScreen
import dr.achim.sleep_timer.presentation.settings.CreditsScreen
import dr.achim.sleep_timer.presentation.settings.FaqScreen
import dr.achim.sleep_timer.presentation.settings.SettingsScreen
import dr.achim.sleep_timer.presentation.timer.TimerScreen
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

@Serializable
object OnboardingKey : NavKey

@Serializable
object HomeKey : NavKey

@Serializable
data class TimerKey(val minutes: Int?) : NavKey

@Serializable
data class SettingsKey(val highlight: String? = null) : NavKey

@Serializable
object CreditsKey : NavKey

@Serializable
object FaqKey : NavKey

@Serializable
data class HueDiscoveryKey(val source: HueActionSource) : NavKey

@Serializable
data class RoomSelectionKey(val source: HueActionSource) : NavKey


@Composable
fun Navigation(
    initialBackStack: List<NavKey> = listOf(HomeKey),
    sharedTransitionScope: SharedTransitionScope? = null
) {
    val backStack = rememberNavBackStack(*initialBackStack.toTypedArray())
    val onBack = dropUnlessResumed { backStack.removeLastOrNull() }
    val settingsRepository = koinInject<SettingsRepository>()
    val uiMessageManager = koinInject<UiMessageManager>()
    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        uiMessageManager.messages.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        sharedTransitionScope = sharedTransitionScope,
        predictivePopTransitionSpec = {
            ContentTransform(
                fadeIn(
                    spring(
                        dampingRatio = 1.0f,
                        stiffness = 1600.0f,
                    )
                ),
                fadeOut(),
            )
        },
        entryProvider = entryProvider {
            entry<OnboardingKey> {
                OnboardingScreen(
                    onComplete = {
                        scope.launch {
                            settingsRepository.setFirstLaunchCompleted()
                            backStack.clear()
                            backStack += HomeKey
                        }
                    }
                )
            }
            entry<HomeKey> {
                HomeScreen(
                    onNavigateToTimer = { minutes ->
                        backStack += TimerKey(minutes)
                    },
                    onNavigateToSettings = dropUnlessResumed {
                        backStack += SettingsKey()
                    },
                    snackbarHostState = snackbarHostState
                )
            }
            entry<TimerKey>(
                metadata = metadata {
                    put(NavDisplay.TransitionKey) {
                        slideInVertically(initialOffsetY = { it }) + fadeIn() togetherWith ExitTransition.KeepUntilTransitionsFinished
                    }
                    put(NavDisplay.PopTransitionKey) {
                        EnterTransition.None togetherWith slideOutVertically(targetOffsetY = { it }) + fadeOut()
                    }
                    put(NavDisplay.PredictivePopTransitionKey) {
                        EnterTransition.None togetherWith slideOutVertically(targetOffsetY = { it }) + fadeOut()
                    }
                }
            ) { key ->
                TimerScreen(
                    onBack = onBack,
                    onNavigateToRoomSelection = { source ->
                        backStack += RoomSelectionKey(source)
                    },
                    onNavigateToSettings = { highlight ->
                        backStack += SettingsKey(highlight)
                    },
                    viewModel = koinViewModel(parameters = { parametersOf(key.minutes) }),
                    snackbarHostState = snackbarHostState
                )
            }
            entry<SettingsKey>(
                metadata = metadata {
                    put(NavDisplay.TransitionKey) {
                        slideInHorizontally(initialOffsetX = { it }) togetherWith slideOutHorizontally(
                            targetOffsetX = { -it })
                    }
                    put(NavDisplay.PopTransitionKey) {
                        slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(
                            targetOffsetX = { it })
                    }
                    put(NavDisplay.PredictivePopTransitionKey) {
                        slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(
                            targetOffsetX = { it })
                    }
                }
            ) { key ->
                SettingsScreen(
                    onBack = onBack,
                    onNavigateToCredits = {
                        backStack += CreditsKey
                    },
                    onNavigateToFaq = {
                        backStack += FaqKey
                    },
                    highlight = key.highlight,
                    snackbarHostState = snackbarHostState
                )
            }
            entry<FaqKey> {
                FaqScreen(onBack = onBack)
            }
            entry<CreditsKey> {
                CreditsScreen(onBack = onBack)
            }
            entry<HueDiscoveryKey> { key ->
                HueDiscoveryScreen(
                    onBack = onBack,
                    viewModel = koinViewModel(parameters = { parametersOf(key.source) })
                )
            }
            entry<RoomSelectionKey> { key ->
                RoomSelectionScreen(
                    onBack = onBack,
                    onNavigateToDiscovery = { source ->
                        backStack += HueDiscoveryKey(source)
                    },
                    viewModel = koinViewModel(parameters = { parametersOf(key.source) })
                )
            }
        }
    )
}
