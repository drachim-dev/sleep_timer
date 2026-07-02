package dr.achim.sleep_timer.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import dr.achim.sleep_timer.model.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = PeachPrimary,
    onPrimary = OnPeach,
    secondary = PeachSecondary,
    tertiary = PeachTertiary,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = DarkSurfaceVariant
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFD84315), // Vibrant Deep Orange
    onPrimary = Color.White,
    secondary = Color(0xFFFF8A65),
    tertiary = Color(0xFFFFCCBC),
    background = Color(0xFFFFF8F6),
    surface = Color(0xFFFFF8F6),
    onBackground = Color(0xFF2B2930),
    onSurface = Color(0xFF2B2930),
    surfaceVariant = Color(0xFFF4DED8)
)

@Composable
fun AppTheme(
    themeMode: ThemeMode = ThemeMode.default,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LightDynamic,
        ThemeMode.LightOrange -> false
        ThemeMode.DarkDynamic,
        ThemeMode.DarkOrange -> true
    }

    val dynamicColor = themeMode in listOf(ThemeMode.SYSTEM, ThemeMode.LightDynamic, ThemeMode.DarkDynamic)
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalDimens provides Dimens()) {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            motionScheme = MotionScheme.expressive(),
            typography = Typography,
            content = content
        )
    }
}

object AppTheme

val AppTheme.dimens: Dimens
    @Composable
    @ReadOnlyComposable
    get() = LocalDimens.current
