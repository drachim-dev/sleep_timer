package dr.achim.sleep_timer.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import dr.achim.sleep_timer.navigation.LocalSharedTransitionScope

@Composable
fun Modifier.safeSharedElement(key: Any): Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = if (LocalInspectionMode.current) null else LocalNavAnimatedContentScope.current

    return if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            sharedElement(
                sharedContentState = rememberSharedContentState(key = key),
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    } else this
}

@Composable
fun Modifier.safeSharedBounds(key: Any,
    enter: EnterTransition = fadeIn(),
    exit: ExitTransition = fadeOut(),
): Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = if (LocalInspectionMode.current) null else LocalNavAnimatedContentScope.current

    return if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            sharedBounds(
                sharedContentState = rememberSharedContentState(key = key),
                animatedVisibilityScope = animatedVisibilityScope,
                enter = enter,
                exit = exit
            )
        }
    } else this
}

enum class SharedElementKey {
    Fab,
    FabTrailing,
    CircularTimer,
    ActionButtonGearToCross
}