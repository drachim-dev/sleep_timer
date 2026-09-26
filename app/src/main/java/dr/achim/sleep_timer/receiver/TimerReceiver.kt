package dr.achim.sleep_timer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dr.achim.sleep_timer.data.TimerActionExecutor
import dr.achim.sleep_timer.domain.repository.TimerRepository
import dr.achim.sleep_timer.domain.usecase.ManageTimerActionsUseCase
import dr.achim.sleep_timer.service.TimerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TimerReceiver : BroadcastReceiver(), KoinComponent {
    private val timerActionExecutor: TimerActionExecutor by inject()
    private val manageTimerActionsUseCase: ManageTimerActionsUseCase by inject()
    private val timerRepository: TimerRepository by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_TIMER_EXPIRED) {
            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.Default).launch {
                try {
                    timerRepository.setLightsOffDelayProgress(0f)
                    timerRepository.setRunning(false)
                    timerRepository.setRemainingTime(0)

                    val actions = manageTimerActionsUseCase.observeTimerActions().first()
                    timerActionExecutor.applyEndActions(actions.endActions)
                } finally {
                    context.stopService(Intent(context, TimerService::class.java))
                    pendingResult.finish()
                }
            }
        }
    }

    companion object {
        const val ACTION_TIMER_EXPIRED = "dr.achim.sleep_timer.ACTION_TIMER_EXPIRED"
    }
}