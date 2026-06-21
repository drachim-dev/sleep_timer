package dr.achim.sleep_timer.ui.components

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.ColorMatrixColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.NinePatchDrawable
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb

@Composable
fun rememberDrawablePainter(
    icon: Any?,
    foregroundTint: Color? = null,
    backgroundTint: Color? = null
): Painter {
    val drawable = icon as? Drawable

    return remember(icon, foregroundTint, backgroundTint) {
        val needsTint = foregroundTint != null || backgroundTint != null

        val d = if (needsTint) drawable?.mutate() else drawable

        if (d != null && needsTint) {
            if (d is AdaptiveIconDrawable) {
                backgroundTint?.let { d.background?.applyTint(it) }

                val foreground = d.effectiveForeground

                foregroundTint?.let { tint ->
                    foreground?.tintForeground(tint)
                }
            } else {
                foregroundTint?.let { tint ->
                    d.tintForeground(tint)
                }
            }
        }

        object : Painter() {
            // Return Size.Unspecified only when drawable is genuinely absent;
            // callers should provide a fixed size via Modifier.size() in that case.
            override val intrinsicSize: Size
                get() = d?.let {
                    Size(
                        it.intrinsicWidth.toFloat(),
                        it.intrinsicHeight.toFloat()
                    )
                } ?: Size.Unspecified

            override fun DrawScope.onDraw() {
                d?.let {
                    if (it is AdaptiveIconDrawable) {
                        val background = it.background
                        val foreground = it.effectiveForeground

                        // Adaptive icons are 108×108 dp with a 72×72 dp safe zone.
                        // Scaling by 108/72 = 1.5× maps the safe zone back to the viewport.
                        val scale = 1.5f
                        val offset = size.width * (1f - scale) / 2f

                        if (backgroundTint != null) {
                            drawRect(backgroundTint)
                        } else {
                            background?.let { bg ->
                                bg.setBounds(
                                    0, 0,
                                    (size.width * scale).toInt(),
                                    (size.height * scale).toInt()
                                )
                                drawContext.canvas.nativeCanvas.save()
                                drawContext.canvas.nativeCanvas.translate(offset, offset)
                                bg.draw(drawContext.canvas.nativeCanvas)
                                drawContext.canvas.nativeCanvas.restore()
                            }
                        }

                        foreground?.let { fg ->
                            fg.setBounds(
                                0, 0,
                                (size.width * scale).toInt(),
                                (size.height * scale).toInt()
                            )
                            drawContext.canvas.nativeCanvas.save()
                            drawContext.canvas.nativeCanvas.translate(offset, offset)
                            fg.draw(drawContext.canvas.nativeCanvas)
                            drawContext.canvas.nativeCanvas.restore()
                        }
                    } else {
                        backgroundTint?.let { tint -> drawRect(tint) }
                        it.setBounds(0, 0, size.width.toInt(), size.height.toInt())
                        it.draw(drawContext.canvas.nativeCanvas)
                    }
                }
            }
        }
    }
}

private val AdaptiveIconDrawable.effectiveForeground: Drawable?
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        monochrome ?: foreground
    } else {
        foreground
    }

private fun Drawable.tintForeground(color: Color) {
    if (shouldUseLuminanceTint()) {
        applyMonochromeMask(color)
    } else {
        applyTint(color)
    }
}

private fun Drawable.applyTint(color: Color) {
    colorFilter = BlendModeColorFilter(color.toArgb(), BlendMode.SRC_IN)
}

/**
 * Applies a color-matrix filter that:
 *  - sets the RGB channels to [color]'s RGB values, and
 *  - derives alpha from the *inverse luminance* of the source pixel so that
 *    near-white backgrounds become transparent while dark symbols stay opaque.
 *
 * This only works correctly for fully opaque [color] values. Semi-transparent
 * colors shift the [-0.5, +Infinity] alpha window and will produce unexpected results.
 * Pass an opaque color or pre-multiply alpha before calling this function.
 */
private fun Drawable.applyMonochromeMask(color: Color) {
    val ca = color.alpha
    // Factor 2.0× and −0.5 offset maps [0.25, 0.75] of (Alpha − Luminance) → [0.0, 1.0] alpha,
    // removing near-white backgrounds while keeping dark symbols fully opaque.
    val f = 2.0f
    val off = -0.5f

    val matrix = android.graphics.ColorMatrix(
        floatArrayOf(
            0f, 0f, 0f, 0f, color.red * 255f,
            0f, 0f, 0f, 0f, color.green * 255f,
            0f, 0f, 0f, 0f, color.blue * 255f,
            -0.2126f * f * ca, -0.7152f * f * ca, -0.0722f * f * ca, f * ca, off * 255f * ca
        )
    )
    colorFilter = ColorMatrixColorFilter(matrix)
}

/**
 * Returns true when this drawable is inherently opaque and therefore cannot
 * be used as an alpha mask — a luminance-based matrix tint should be used instead.
 */
private fun Drawable.shouldUseLuminanceTint(): Boolean {
    return opacity == PixelFormat.OPAQUE
            || this is BitmapDrawable
            || this is NinePatchDrawable
            || this is ColorDrawable
}