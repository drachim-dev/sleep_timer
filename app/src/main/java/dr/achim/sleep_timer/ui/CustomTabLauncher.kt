package dr.achim.sleep_timer.ui

import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

@Composable
fun rememberCustomTabLauncher(): (url: String) -> Unit {
    val context = LocalContext.current

    val colorSchemeParams = CustomTabColorSchemeParams.Builder()
        .setToolbarColor(MaterialTheme.colorScheme.surface.toArgb())
        .setNavigationBarColor(MaterialTheme.colorScheme.surface.toArgb())
        .build()

    return remember(context) {
        { url ->
            val intent = CustomTabsIntent.Builder()
                .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
                .setShowTitle(true)
                .setUrlBarHidingEnabled(false)
                .setDefaultColorSchemeParams(colorSchemeParams)
                .build()

            intent.launchUrl(context, url.toUri())
        }
    }
}