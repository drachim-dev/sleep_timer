package dr.achim.sleep_timer.domain.usecase

import dr.achim.sleep_timer.data.TimerController
import dr.achim.sleep_timer.domain.repository.TimerRepository

class ControlTimerUseCase(
    private val timerController: TimerController,
    private val timerRepository: TimerRepository
) {
    fun start(millis: Long) = timerController.startTimer(millis)
    fun stop() = timerController.stopTimer()
    fun pause() = timerController.pauseTimer()
    fun resume() = timerController.resumeTimer()
    fun setRemainingTime(millis: Long) = timerRepository.setRemainingTime(millis)

    fun hasNotificationPermission() = timerController.hasNotificationPermission()
}
