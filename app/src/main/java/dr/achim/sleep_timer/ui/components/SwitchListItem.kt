package dr.achim.sleep_timer.ui.components

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SwitchListItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    leadingContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    highlightedContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    highlighted: Boolean = false,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    val highlightColor = highlightedContainerColor.compositeOver(containerColor)
    val animatableColor = remember(containerColor) { Animatable(containerColor) }

    LaunchedEffect(highlighted, containerColor) {
        if (highlighted) {
            delay(800.milliseconds)
            repeat(3) {
                animatableColor.animateTo(highlightColor, spring(stiffness = Spring.StiffnessLow))
                animatableColor.animateTo(containerColor, spring(stiffness = Spring.StiffnessLow))
            }
            animatableColor.animateTo(highlightColor, spring(stiffness = Spring.StiffnessVeryLow))
        }
    }

    ListItem(
        onClick = { onCheckedChange(!checked) },
        modifier = modifier
            .semantics {
                role = Role.Switch
                toggleableState = ToggleableState(checked)
            },
        interactionSource = interactionSource,
        leadingContent = leadingContent,
        supportingContent = supportingContent,
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = null,
                interactionSource = interactionSource
            )
        },
        colors = ListItemDefaults.colors(containerColor = animatableColor.value),
        content = content
    )
}
