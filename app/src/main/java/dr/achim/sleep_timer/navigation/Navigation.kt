package dr.achim.sleep_timer.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dr.achim.sleep_timer.presentation.home.HomeScreen
import dr.achim.sleep_timer.presentation.settings.CreditsScreen
import dr.achim.sleep_timer.presentation.settings.FaqScreen
import dr.achim.sleep_timer.presentation.settings.SettingsScreen
import dr.achim.sleep_timer.presentation.timer.TimerScreen
import dr.achim.sleep_timer.util.UiMessageManager
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

@Serializable
object HomeKey : NavKey

@Serializable
data class TimerKey(val minutes: Int?) : NavKey

@Serializable
object SettingsKey : NavKey

@Serializable
object CreditsKey : NavKey

@Serializable
object FaqKey : NavKey


@Composable
fun Navigation(initialBackStack: List<NavKey> = listOf(HomeKey)) {
    val backStack = rememberNavBackStack(*initialBackStack.toTypedArray())
    val onBack = dropUnlessResumed { backStack.removeLastOrNull() }
    val uiMessageManager = koinInject<UiMessageManager>()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        uiMessageManager.messages.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    SharedTransitionLayout {
        NavDisplay(
            backStack = backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            sharedTransitionScope = this,
            transitionSpec = {
                slideInHorizontally(initialOffsetX = { it }) togetherWith slideOutHorizontally(
                    targetOffsetX = { -it })
            },
            popTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(
                    targetOffsetX = { it })
            },
            predictivePopTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(
                    targetOffsetX = { it })
            },
            entryProvider = entryProvider {
                entry<HomeKey> {
                    CompositionLocalProvider(
                        LocalSharedTransitionScope provides this@SharedTransitionLayout
                    ) {
                        HomeScreen(
                            onNavigateToTimer = { minutes ->
                                backStack += TimerKey(minutes)
                            },
                            onNavigateToSettings = dropUnlessResumed {
                                backStack += SettingsKey
                            },
                            snackbarHostState = snackbarHostState
                        )
                    }
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
                    CompositionLocalProvider(
                        LocalSharedTransitionScope provides this@SharedTransitionLayout
                    ) {
                        TimerScreen(
                            onBack = onBack,
                            viewModel = koinViewModel(parameters = { parametersOf(key.minutes) }),
                            snackbarHostState = snackbarHostState
                        )
                    }
                }
                entry<SettingsKey> {
                    SettingsScreen(
                        onBack = onBack,
                        onNavigateToCredits = {
                            backStack += CreditsKey
                        },
                        onNavigateToFaq = {
                            backStack += FaqKey
                        },
                        snackbarHostState = snackbarHostState
                    )
                }
                entry<FaqKey> {
                    FaqScreen(onBack = onBack)
                }
                entry<CreditsKey> {
                    CreditsScreen(onBack = onBack)
                }
            }
        )
    }
}