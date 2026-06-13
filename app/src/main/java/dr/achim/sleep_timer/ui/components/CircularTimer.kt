package dr.achim.sleep_timer.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import dr.achim.sleep_timer.ui.theme.AppTheme
import dr.achim.sleep_timer.ui.theme.dimens
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CircularTimer(
    progress: Float,
    timeText: String,
    modifier: Modifier = Modifier,
    interactive: Boolean = false,
    glowEnabled: Boolean = true,
    glowIntensity: Float = 30f,
    glowColor: Color = MaterialTheme.colorScheme.primary,
    showHandle: Boolean = interactive,
    onProgressChange: (Float) -> Unit = {}
) {
    val currentProgress by rememberUpdatedState(progress)
    val currentOnProgressChange by rememberUpdatedState(onProgressChange)

    var isDragging by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    val handleScale by animateFloatAsState(
        targetValue = if (isPressed || isDragging) 1.25f else 1.0f,
        label = "handleScale"
    )

    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val diameter =
            if (maxWidth != Dp.Infinity && maxHeight != Dp.Infinity) {
                minOf(maxWidth, maxHeight)
            } else {
                AppTheme.dimens.timerDiameter
            }

        val strokeWidth: Dp = if (interactive) AppTheme.dimens.timerStrokeWidthInteractive else AppTheme.dimens.timerStrokeWidthDefault
        val strokeWidthPx = with(density) { strokeWidth.toPx() }
        val innerSquareSide = (diameter - strokeWidth) * 0.7071f

        Box(
            modifier = Modifier
                .size(diameter)
                .then(
                    if (interactive) {
                        Modifier.pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val down = awaitFirstDown()
                                    val center = Offset(size.width / 2f, size.height / 2f)
                                    val radius = size.width / 2f
                                    val innerRadius = (size.width - strokeWidthPx) / 2f

                                    val distanceFromCenter = (down.position - center).getDistance()
                                    val isOnTrack =
                                        distanceFromCenter in (innerRadius - strokeWidthPx)..(radius + strokeWidthPx)

                                    if (isOnTrack) {
                                        isPressed = true
                                        updateProgress(
                                            touchPoint = down.position,
                                            center = center,
                                            currentProgress = currentProgress,
                                            onProgressChange = currentOnProgressChange,
                                            keepTurnCount = true
                                        )

                                        drag(down.id) { change ->
                                            isDragging = true
                                            updateProgress(
                                                touchPoint = change.position,
                                                center = center,
                                                currentProgress = currentProgress,
                                                onProgressChange = currentOnProgressChange,
                                                keepTurnCount = false
                                            )
                                            change.consume()
                                        }
                                        isPressed = false
                                        isDragging = false
                                    }
                                }
                            }
                        }
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            val sweepAngle = if (interactive) {
                val displayProgress =
                    if (progress > 0f && progress % 1f == 0f) 1f else progress % 1f
                360f * displayProgress
            } else {
                360f * progress
            }

            CircularTimerTrack(
                sweepAngle = sweepAngle,
                strokeWidth = strokeWidth,
                glowEnabled = glowEnabled,
                glowIntensity = glowIntensity,
                glowColor = glowColor,
                showHandle = showHandle,
                handleScale = handleScale,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .size(innerSquareSide)
                    .padding(AppTheme.dimens.spacingSmall),
                contentAlignment = Alignment.Center
            ) {
                val textStyle = MaterialTheme.typography.displayLarge
                Text(
                    text = timeText,
                    style = textStyle,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    autoSize = TextAutoSize.StepBased(maxFontSize = textStyle.fontSize)
                )
            }
        }
    }
}

@Composable
private fun CircularTimerTrack(
    sweepAngle: Float,
    strokeWidth: Dp,
    modifier: Modifier = Modifier,
    glowEnabled: Boolean = true,
    glowIntensity: Float = 30f,
    glowColor: Color = MaterialTheme.colorScheme.primary,
    showHandle: Boolean = false,
    handleScale: Float = 1f,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val handleRadiusDp = AppTheme.dimens.timerHandleRadius

    Canvas(modifier = modifier) {
        val strokeWidthPx = strokeWidth.toPx()
        val innerRadius = (size.minDimension - strokeWidthPx) / 2
        val center = Offset(size.width / 2, size.height / 2)

        // Background Track
        drawCircle(
            color = surfaceVariant.copy(alpha = 0.3f),
            radius = innerRadius,
            center = center,
            style = Stroke(width = strokeWidthPx)
        )

        if (glowEnabled) {
            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    isAntiAlias = true
                    color = glowColor.toArgb()
                    style = android.graphics.Paint.Style.STROKE
                    this.strokeWidth = strokeWidthPx
                    strokeCap = android.graphics.Paint.Cap.ROUND
                    maskFilter = android.graphics.BlurMaskFilter(
                        glowIntensity.coerceAtLeast(1f),
                        android.graphics.BlurMaskFilter.Blur.OUTER
                    )
                }

                drawArc(
                    strokeWidthPx / 2,
                    strokeWidthPx / 2,
                    size.width - strokeWidthPx / 2,
                    size.height - strokeWidthPx / 2,
                    -90f,
                    sweepAngle,
                    false,
                    paint
                )
            }
        }

        // Main Progress Arc
        drawArc(
            color = primaryColor,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
            size = Size(size.width - strokeWidthPx, size.height - strokeWidthPx),
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        )

        // Drag Handle
        if (showHandle) {
            val handleRadius = handleRadiusDp.toPx() * handleScale
            val angleRad = Math.toRadians((sweepAngle - 90f).toDouble())
            val handleCenter = Offset(
                center.x + innerRadius * cos(angleRad).toFloat(),
                center.y + innerRadius * sin(angleRad).toFloat()
            )

            drawCircle(
                color = primaryColor,
                radius = handleRadius,
                center = handleCenter
            )

            drawCircle(
                color = Color.White,
                radius = handleRadius * 0.9f,
                center = handleCenter
            )
        }
    }
}

private fun updateProgress(
    touchPoint: Offset,
    center: Offset,
    currentProgress: Float,
    onProgressChange: (Float) -> Unit,
    keepTurnCount: Boolean
) {
    val angle = atan2(
        (touchPoint.y - center.y).toDouble(),
        (touchPoint.x - center.x).toDouble()
    )
    var normalizedAngle = Math.toDegrees(angle) + 90
    if (normalizedAngle < 0) normalizedAngle += 360

    val touchProgress = (normalizedAngle / 360f).toFloat()

    val nextProgress = if (keepTurnCount) {
        // Maintain the current integer part (hour/turn)
        val currentInt = kotlin.math.floor(currentProgress.toDouble()).toInt()
        (currentInt + touchProgress)
    } else {
        // Delta logic for dragging (allows crossing turns)
        val currentFraction = currentProgress % 1.0f
        var delta = touchProgress - currentFraction
        if (delta > 0.5f) {
            delta -= 1.0f
        } else if (delta < -0.5f) {
            delta += 1.0f
        }
        currentProgress + delta
    }

    onProgressChange(nextProgress.coerceAtLeast(0f))
}

@Preview(showBackground = true)
@Composable
fun CircularTimerPreview() {
    AppTheme {
        CircularTimer(
            progress = 0.83f,
            timeText = "50",
            interactive = true
        )
    }
}
