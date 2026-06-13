package dr.achim.sleep_timer.domain.usecase

import dr.achim.sleep_timer.domain.repository.TimerRepository
import dr.achim.sleep_timer.model.TimerActions
import dr.achim.sleep_timer.model.TimerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class GetTimerStatusUseCase(
    private val manageTimerActionsUseCase: ManageTimerActionsUseCase,
    timerRepository: TimerRepository
) {
    val timerState: StateFlow<TimerState> = timerRepository.timerState
    fun observeTimerActions(): Flow<TimerActions> = manageTimerActionsUseCase.observeTimerActions()
}
