package dr.achim.sleep_timer.ui.components

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
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
    return remember(drawable, foregroundTint, backgroundTint) {
        val d = drawable?.mutate()
        if (d != null && (foregroundTint != null || backgroundTint != null)) {
            if (d is AdaptiveIconDrawable) {
                backgroundTint?.let { d.background?.applyTint(it) }
                
                val monochrome = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    d.monochrome
                } else null

                if (monochrome != null) {
                    foregroundTint?.let { monochrome.applyTint(it) }
                } else {
                    // Fallback: use foreground with duotone filter to preserve contrast
                    if (foregroundTint != null && backgroundTint != null) {
                        d.foreground?.applyDuotone(foregroundTint, backgroundTint)
                    } else {
                        foregroundTint?.let { d.foreground?.applyTint(it) }
                    }
                }
            } else {
                foregroundTint?.let { d.applyTint(it) }
            }
        }

        object : Painter() {
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
                        val foreground = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            it.monochrome ?: it.foreground
                        } else {
                            it.foreground
                        }

                        // Adaptive icons are 108x108dp with 72x72dp safe zone.
                        // Scaling by 1.5x (108/72) restores the safe zone to the viewport.
                        val scale = 1.5f
                        val offset = (size.width - size.width * scale) / 2f

                        background?.let { bg ->
                            bg.setBounds(0, 0, (size.width * scale).toInt(), (size.height * scale).toInt())
                            this.drawContext.canvas.nativeCanvas.save()
                            this.drawContext.canvas.nativeCanvas.translate(offset, offset)
                            bg.draw(this.drawContext.canvas.nativeCanvas)
                            this.drawContext.canvas.nativeCanvas.restore()
                        }
                        foreground?.let { fg ->
                            fg.setBounds(0, 0, (size.width * scale).toInt(), (size.height * scale).toInt())
                            this.drawContext.canvas.nativeCanvas.save()
                            this.drawContext.canvas.nativeCanvas.translate(offset, offset)
                            fg.draw(this.drawContext.canvas.nativeCanvas)
                            this.drawContext.canvas.nativeCanvas.restore()
                        }
                    } else {
                        it.setBounds(0, 0, size.width.toInt(), size.height.toInt())
                        it.draw(this.drawContext.canvas.nativeCanvas)
                    }
                }
            }
        }
    }
}

private fun Drawable.applyTint(color: Color) {
    colorFilter = BlendModeColorFilter(color.toArgb(), BlendMode.SRC_IN)
}

private fun Drawable.applyDuotone(hi: Color, lo: Color) {
    val dr = hi.red - lo.red
    val dg = hi.green - lo.green
    val db = hi.blue - lo.blue

    val matrix = android.graphics.ColorMatrix(
        floatArrayOf(
            dr * 0.2126f, dr * 0.7152f, dr * 0.0722f, 0f, lo.red * 255f,
            dg * 0.2126f, dg * 0.7152f, dg * 0.0722f, 0f, lo.green * 255f,
            db * 0.2126f, db * 0.7152f, db * 0.0722f, 0f, lo.blue * 255f,
            0f, 0f, 0f, 1f, 0f
        )
    )
    colorFilter = ColorMatrixColorFilter(matrix)
}


