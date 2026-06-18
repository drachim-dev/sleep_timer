package dr.achim.sleep_timer.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dr.achim.sleep_timer.data.SettingsRepository
import dr.achim.sleep_timer.domain.usecase.ControlTimerUseCase
import dr.achim.sleep_timer.domain.usecase.GetLastSelectedMinutesUseCase
import dr.achim.sleep_timer.domain.usecase.GetQuickTimesUseCase
import dr.achim.sleep_timer.domain.usecase.GetSettingsUseCase
import dr.achim.sleep_timer.domain.usecase.GetTimerStatusUseCase
import dr.achim.sleep_timer.domain.usecase.UpdateLastSelectedMinutesUseCase
import dr.achim.sleep_timer.domain.usecase.UpdateQuickTimeUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    getQuickTimesUseCase: GetQuickTimesUseCase,
    private val updateQuickTimeUseCase: UpdateQuickTimeUseCase,
    getLastSelectedMinutesUseCase: GetLastSelectedMinutesUseCase,
    private val updateLastSelectedMinutesUseCase: UpdateLastSelectedMinutesUseCase,
    getSettingsUseCase: GetSettingsUseCase,
    getTimerStatusUseCase: GetTimerStatusUseCase,
    private val controlTimerUseCase: ControlTimerUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        getSettingsUseCase(),
        getQuickTimesUseCase(),
        getLastSelectedMinutesUseCase(),
        getTimerStatusUseCase.timerState,
        settingsRepository.timerStartCount
    ) { settings, quickTimes, lastMinutes, timerState, startCount ->
        HomeUiState(
            glowEnabled = settings.glowEffectEnabled,
            glowIntensity = settings.glowIntensity,
            quickTimes = quickTimes,
            lastSelectedMinutes = lastMinutes,
            timerState = timerState,
            timerStartCount = startCount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    fun onAction(action: HomeUiAction) {
        when (action) {
            is HomeUiAction.UpdateQuickTime -> updateQuickTime(action.index, action.minutes)
            is HomeUiAction.UpdateLastSelectedMinutes -> updateLastSelectedMinutes(action.minutes)
            is HomeUiAction.StopTimer -> stopTimer()
        }
    }

    private fun updateQuickTime(index: Int, minutes: Int) {
        viewModelScope.launch {
            updateQuickTimeUseCase(index, minutes)
        }
    }

    private fun updateLastSelectedMinutes(minutes: Int) {
        viewModelScope.launch {
            updateLastSelectedMinutesUseCase(minutes)
        }
    }

    private fun stopTimer() {
        controlTimerUseCase.stop()
    }
}
