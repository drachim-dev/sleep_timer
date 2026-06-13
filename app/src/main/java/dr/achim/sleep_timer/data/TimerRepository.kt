package dr.achim.sleep_timer.data

import dr.achim.sleep_timer.domain.repository.TimerRepository
import dr.achim.sleep_timer.model.TimerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TimerRepositoryImpl : TimerRepository {
    private val _timerState = MutableStateFlow<TimerState>(TimerState.Idle())
    override val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    override fun setRemainingTime(millis: Long) {
        val current = _timerState.value
        _timerState.value = when (current) {
            is TimerState.Idle -> current.copy(remainingTimeMillis = millis)
            is TimerState.Running -> current.copy(remainingTimeMillis = millis)
            is TimerState.Paused -> current.copy(remainingTimeMillis = millis)
        }
    }

    override fun setRunning(running: Boolean) {
        val current = _timerState.value
        _timerState.value = if (running) {
            TimerState.Running(current.remainingTimeMillis, current.totalTimeMillis)
        } else {
            TimerState.Idle(current.remainingTimeMillis, current.totalTimeMillis)
        }
    }

    override fun setPaused(paused: Boolean) {
        val current = _timerState.value
        if (current is TimerState.Idle) return

        _timerState.value = if (paused) {
            TimerState.Paused(current.remainingTimeMillis, current.totalTimeMillis)
        } else {
            TimerState.Running(current.remainingTimeMillis, current.totalTimeMillis)
        }
    }

    override fun setTotalTime(millis: Long) {
        val current = _timerState.value
        _timerState.value = when (current) {
            is TimerState.Idle -> current.copy(totalTimeMillis = millis)
            is TimerState.Running -> current.copy(totalTimeMillis = millis)
            is TimerState.Paused -> current.copy(totalTimeMillis = millis)
        }
    }
}
