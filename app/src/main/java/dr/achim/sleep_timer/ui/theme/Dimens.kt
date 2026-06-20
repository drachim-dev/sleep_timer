package dr.achim.sleep_timer.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimens(
    val spacingExtraExtraSmall: Dp = 2.dp,
    val spacingExtraSmall: Dp = 4.dp,
    val spacingSmall: Dp = 8.dp,
    val spacingNormal: Dp = 12.dp,
    val spacingMedium: Dp = 16.dp,
    val spacingLarge: Dp = 24.dp,
    val spacingExtraLarge: Dp = 32.dp,
    val spacingHuge: Dp = 56.dp,

    val largeButtonSize: Dp = 48.dp,
    val pageIndicator: Dp = 8.dp,
    val timeButtonSize: Dp = 64.dp,
    val quickTimeAddIconSize: Dp = 32.dp,
    val actionToggleIconSize: Dp = 32.dp,

    val timerDiameter: Dp = 280.dp,
    val timerStrokeWidthDefault: Dp = 8.dp,
    val timerStrokeWidthInteractive: Dp = 14.dp,
    val timerHandleRadius: Dp = 14.dp,
    val timerSizeExpanded: Dp = 200.dp,
    val timerSizeCollapsed: Dp = 150.dp,

    val quickLaunchItemWidth: Dp = 72.dp,
    val quickLaunchItemHeight: Dp = 80.dp,
    val quickLaunchAppIconSize: Dp = 48.dp,
    val quickLaunchIconSize: Dp = 24.dp,
    val quickLaunchCardWidth: Dp = 96.dp,

    val borderThickness: Dp = 2.dp,
    val dashLength: Dp = 8.dp,
    val dashGap: Dp = 6.dp,
)

val LocalDimens = staticCompositionLocalOf { Dimens() }
