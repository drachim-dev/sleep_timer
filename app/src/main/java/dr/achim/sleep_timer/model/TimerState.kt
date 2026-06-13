package dr.achim.sleep_timer.model

import java.util.Locale

sealed interface TimerState {
    val remainingTimeMillis: Long
    val totalTimeMillis: Long

    val progress: Float
        get() = if (totalTimeMillis > 0) {
            (remainingTimeMillis.toFloat() / totalTimeMillis).coerceIn(0f, 1f)
        } else {
            0f
        }

    val formattedTime: String
        get() {
            val seconds = (remainingTimeMillis / 1000) % 60
            val minutes = (remainingTimeMillis / (1000 * 60)) % 60
            val hours = (remainingTimeMillis / (1000 * 60 * 60))
            return if (hours > 0) {
                String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
            }
        }

    data class Idle(
        override val remainingTimeMillis: Long = 0L,
        override val totalTimeMillis: Long = 0L
    ) : TimerState

    data class Running(
        override val remainingTimeMillis: Long,
        override val totalTimeMillis: Long
    ) : TimerState

    data class Paused(
        override val remainingTimeMillis: Long,
        override val totalTimeMillis: Long
    ) : TimerState
}
