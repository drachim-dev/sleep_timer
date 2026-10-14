package dr.achim.sleep_timer.data

import dr.achim.sleep_timer.domain.repository.TimerRepository
import dr.achim.sleep_timer.model.TimerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TimerRepositoryImpl : TimerRepository {
    private val _timerState = MutableStateFlow<TimerState>(TimerState.Idle())
    override val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private val _lightsOffDelayProgress = MutableStateFlow(0f)
    override val lightsOffDelayProgress: StateFlow<Float> = _lightsOffDelayProgress.asStateFlow()

    override fun setRemainingTime(millis: Long) {
        _timerState.update { current ->
            when (current) {
                is TimerState.Idle -> current.copy(remainingTimeMillis = millis)
                is TimerState.Running -> current.copy(remainingTimeMillis = millis)
                is TimerState.Paused -> current.copy(remainingTimeMillis = millis)
            }
        }
    }

    override fun setRunning(running: Boolean) {
        _timerState.update { current ->
            if (running) {
                TimerState.Running(current.remainingTimeMillis, current.totalTimeMillis)
            } else {
                TimerState.Idle(current.remainingTimeMillis, current.totalTimeMillis)
            }
        }
    }

    override fun setPaused(paused: Boolean) {
        _timerState.update { current ->
            current as? TimerState.Idle
                ?: if (paused) {
                    TimerState.Paused(current.remainingTimeMillis, current.totalTimeMillis)
                } else {
                    TimerState.Running(current.remainingTimeMillis, current.totalTimeMillis)
                }
        }
    }

    override fun setTotalTime(millis: Long) {
        _timerState.update { current ->
            when (current) {
                is TimerState.Idle -> current.copy(totalTimeMillis = millis)
                is TimerState.Running -> current.copy(totalTimeMillis = millis)
                is TimerState.Paused -> current.copy(totalTimeMillis = millis)
            }
        }
    }

    override fun setLightsOffDelayProgress(progress: Float) {
        _lightsOffDelayProgress.update { progress }
    }
}
