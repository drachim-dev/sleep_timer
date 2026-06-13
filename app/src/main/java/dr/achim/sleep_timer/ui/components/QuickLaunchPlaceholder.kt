package dr.achim.sleep_timer.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import dr.achim.sleep_timer.R
import dr.achim.sleep_timer.ui.dashedBorder
import dr.achim.sleep_timer.ui.theme.AppTheme
import dr.achim.sleep_timer.ui.theme.dimens

@Composable
fun QuickLaunchPlaceholder(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    QuickLaunchItem(
        modifier = modifier,
        onClick = onClick,
        label = "Pin app",
        alpha = 0.3f,
        icon = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .dashedBorder(
                        AppTheme.dimens.borderThickness,
                        MaterialTheme.colorScheme.onBackground,
                        MaterialTheme.shapes.medium,
                        AppTheme.dimens.dashLength,
                        AppTheme.dimens.dashGap
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = "Add",
                    modifier = Modifier.size(AppTheme.dimens.quickLaunchIconSize)
                )
            }
        }
    )
}
