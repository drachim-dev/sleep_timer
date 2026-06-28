package dr.achim.sleep_timer.domain.repository

import dr.achim.sleep_timer.model.TimerState
import kotlinx.coroutines.flow.StateFlow

interface TimerRepository {
    val timerState: StateFlow<TimerState>
    fun setRemainingTime(millis: Long)
    fun setRunning(running: Boolean)
    fun setPaused(paused: Boolean)
    fun setTotalTime(millis: Long)
}
