package dr.achim.sleep_timer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import dr.achim.sleep_timer.domain.model.QuickLaunchApp
import dr.achim.sleep_timer.ui.theme.AppTheme

@Composable
fun QuickLaunchAppItem(
    app: QuickLaunchApp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null
) {
    QuickLaunchItem(
        modifier = modifier,
        onClick = onClick,
        onLongClick = onLongClick,
        label = app.name,
        icon = {
            Image(
                painter = rememberDrawablePainter(
                    icon = app.icon,
                    foregroundTint = MaterialTheme.colorScheme.primary,
                    backgroundTint = MaterialTheme.colorScheme.surfaceVariant
                ),
                contentDescription = null,
                modifier = Modifier.clip(MaterialTheme.shapes.medium)
            )
        }
    )
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        QuickLaunchAppItem(
            onClick = {},
            app = QuickLaunchApp(
                name = "Spotify",
                packageName = "com.spotify.music",
                icon = null
            )
        )
    }
}
