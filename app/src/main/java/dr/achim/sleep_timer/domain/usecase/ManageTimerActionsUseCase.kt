package dr.achim.sleep_timer.domain.usecase

import dr.achim.sleep_timer.data.SettingsRepository
import dr.achim.sleep_timer.model.EndActions
import dr.achim.sleep_timer.model.StartActions
import dr.achim.sleep_timer.model.TimerActionSource
import dr.achim.sleep_timer.model.TimerActionType
import dr.achim.sleep_timer.model.TimerActions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ManageTimerActionsUseCase(private val settingsRepository: SettingsRepository) {

    fun observeTimerActions(): Flow<TimerActions> {
        val startActionsFlow = combine(
            settingsRepository.startAdjustVolume,
            settingsRepository.startVolumeLevel,
            settingsRepository.startEnableDnd,
            settingsRepository.startHueLights
        ) { adjust, level, dnd, hue ->
            StartActions(
                adjustVolume = adjust,
                volumeLevel = level,
                enableDnd = dnd,
                hueLights = hue
            )
        }

        val endActionsFlow = combine<Any?, EndActions>(
            settingsRepository.endStopMedia,
            settingsRepository.endAdjustVolume,
            settingsRepository.endVolumeLevel,
            settingsRepository.endTurnOffScreen,
            settingsRepository.endTurnOffBluetooth,
            settingsRepository.endHueLights
        ) { flows ->
            EndActions(
                stopMedia = flows[0] as Boolean,
                adjustVolume = flows[1] as Boolean,
                volumeLevel = flows[2] as Int?,
                turnOffScreen = flows[3] as Boolean,
                turnOffBluetooth = flows[4] as Boolean,
                hueLights = flows[5] as Boolean
            )
        }

        return combine(startActionsFlow, endActionsFlow) { start, end ->
            TimerActions(start, end)
        }
    }

    suspend fun updateAction(type: TimerActionType, source: TimerActionSource, enabled: Boolean) {
        when(source) {
            TimerActionSource.START -> {
                when (type) {
                    TimerActionType.ADJUST_VOLUME -> settingsRepository.setStartAdjustVolume(enabled)
                    TimerActionType.HUE_LIGHTS -> settingsRepository.setStartHueLights(enabled)
                    TimerActionType.DND -> settingsRepository.setStartEnableDnd(enabled)
                    TimerActionType.STOP_MEDIA -> {}
                    TimerActionType.TURN_OFF_SCREEN -> {}
                    TimerActionType.TURN_OFF_BLUETOOTH -> {}
                }
            }
            TimerActionSource.END -> {
                when (type) {
                    TimerActionType.ADJUST_VOLUME -> settingsRepository.setEndAdjustVolume(enabled)
                    TimerActionType.HUE_LIGHTS -> settingsRepository.setEndHueLights(enabled)
                    TimerActionType.DND -> {}
                    TimerActionType.STOP_MEDIA -> settingsRepository.setEndStopMedia(enabled)
                    TimerActionType.TURN_OFF_SCREEN -> settingsRepository.setEndTurnOffScreen(enabled)
                    TimerActionType.TURN_OFF_BLUETOOTH -> settingsRepository.setEndTurnOffBluetooth(enabled)
                }
            }
        }
    }

    suspend fun setVolumeLevel(source: TimerActionSource, level: Int?) {
        when (source) {
            TimerActionSource.START -> settingsRepository.setStartVolumeLevel(level)
            TimerActionSource.END -> settingsRepository.setEndVolumeLevel(level)
        }
    }
}
