package dr.achim.sleep_timer.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionDefaults
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import dr.achim.sleep_timer.navigation.LocalSharedTransitionScope

@Composable
fun Modifier.safeSharedElement(
    key: Any,
    animatedVisibilityScope: AnimatedVisibilityScope? = if (LocalInspectionMode.current) null else LocalNavAnimatedContentScope.current,
    boundsTransform: BoundsTransform = SharedTransitionDefaults.BoundsTransform,
    placeholderSize: SharedTransitionScope.PlaceholderSize = SharedTransitionScope.PlaceholderSize.ContentSize,
    renderInOverlayDuringTransition: Boolean = true,
    zIndexInOverlay: Float = 0f,
): Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    return if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            sharedElement(
                sharedContentState = rememberSharedContentState(key = key),
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = boundsTransform,
                placeholderSize = placeholderSize,
                renderInOverlayDuringTransition = renderInOverlayDuringTransition,
                zIndexInOverlay = zIndexInOverlay,
            )
        }
    } else this
}

@Composable
fun Modifier.safeSharedBounds(
    key: Any,
    enter: EnterTransition = fadeIn(),
    exit: ExitTransition = fadeOut(),
    animatedVisibilityScope: AnimatedVisibilityScope? = if (LocalInspectionMode.current) null else LocalNavAnimatedContentScope.current,
    boundsTransform: BoundsTransform = SharedTransitionDefaults.BoundsTransform,
    placeholderSize: SharedTransitionScope.PlaceholderSize = SharedTransitionScope.PlaceholderSize.ContentSize,
    resizeMode: SharedTransitionScope.ResizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
    renderInOverlayDuringTransition: Boolean = true,
    zIndexInOverlay: Float = 0f,
): Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current

    return if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            sharedBounds(
                sharedContentState = rememberSharedContentState(key = key),
                animatedVisibilityScope = animatedVisibilityScope,
                enter = enter,
                exit = exit,
                boundsTransform = boundsTransform,
                placeholderSize = placeholderSize,
                resizeMode = resizeMode,
                renderInOverlayDuringTransition = renderInOverlayDuringTransition,
                zIndexInOverlay = zIndexInOverlay,
            )
        }
    } else this
}

enum class SharedElementKey {
    Fab,
    FabTrailing,
    CircularTimer,
    TimerTextBox,
    TimerText,
    ActionButtonGearToCross
}
