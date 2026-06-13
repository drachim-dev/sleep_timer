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
fun Modifier.sharedElementTransition(
    key: Any,
): Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val isPreview = LocalInspectionMode.current
    val animatedVisibilityScope = if (isPreview) null else LocalNavAnimatedContentScope.current

    return if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            this@sharedElementTransition.sharedElement(
                sharedContentState = rememberSharedContentState(key = key),
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    } else this
}

@Composable
fun Modifier.sharedBoundsIfAvailable(
    key: Any,
    enter: EnterTransition = fadeIn(),
    exit: ExitTransition = fadeOut(),
): Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val isPreview = LocalInspectionMode.current
    val animatedVisibilityScope = if (isPreview) null else LocalNavAnimatedContentScope.current

    return if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            this@sharedBoundsIfAvailable.sharedBounds(
                rememberSharedContentState(key = key),
                animatedVisibilityScope = animatedVisibilityScope,
                enter = enter,
                exit = exit
            )
        }
    } else this
}
