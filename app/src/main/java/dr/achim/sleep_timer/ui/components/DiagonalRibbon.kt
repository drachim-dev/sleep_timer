package dr.achim.sleep_timer.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import dr.achim.sleep_timer.R
import dr.achim.sleep_timer.ui.theme.AppTheme
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun BoxScope.DiagonalRibbon(
    modifier: Modifier = Modifier,
    ribbonContainerColor: Color = MaterialTheme.colorScheme.tertiary,
    ribbonContentColor: Color = MaterialTheme.colorScheme.onTertiary,
    angle: Float = 45f,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalContentColor provides ribbonContentColor) {
        SubcomposeLayout(modifier = modifier.matchParentSize()) { constraints ->
            // Measure the text composable unconstrained so it never wraps
            val textPlaceable = subcompose("text_content", content)
                .first()
                .measure(Constraints())

            val textWidth = textPlaceable.width
            val textHeight = textPlaceable.height

            val ribbonHeight = textHeight * 1f
            val ribbonWidth = constraints.maxWidth * 2f
            val offsetFromCorner = 28.dp.toPx()

            val layoutWidth = constraints.maxWidth
            val layoutHeight = constraints.maxHeight

            layout(layoutWidth, layoutHeight) {
                val placeable = subcompose("ribbon_draw") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .drawWithContent {
                                withTransform({
                                    translate(left = size.width, top = 0f)
                                    rotate(angle, pivot = Offset.Zero)
                                    translate(left = 0f, top = offsetFromCorner)
                                }) {
                                    // Draw background banner
                                    drawRect(
                                        color = ribbonContainerColor,
                                        topLeft = Offset(-ribbonWidth / 2f, -ribbonHeight / 2f),
                                        size = Size(ribbonWidth, ribbonHeight)
                                    )
                                }
                            }
                    )
                }.first().measure(constraints)

                placeable.place(0, 0)

                // Place the actual text composable using the exact same coordinate logic
                // Matrix math simplified: translate to corner -> rotate -> push down -> center text
                val angleRad = Math.toRadians(angle.toDouble())
                val cos = cos(angleRad).toFloat()
                val sin = sin(angleRad).toFloat()

                val localX = 0f

                val rotatedX = localX * cos - offsetFromCorner * sin
                val rotatedY = localX * sin + offsetFromCorner * cos

                val targetX = layoutWidth + rotatedX - (textWidth / 2f)
                val targetY = 0f + rotatedY - (textHeight / 2f)

                textPlaceable.placeWithLayer(
                    x = targetX.roundToInt(),
                    y = targetY.roundToInt()
                ) {
                    rotationZ = angle
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        Box(modifier = Modifier.clipToBounds()) {
            ListItem {
                Text("Option 1")
            }

            DiagonalRibbon { Text(stringResource(R.string.common_pro)) }
        }
    }
}