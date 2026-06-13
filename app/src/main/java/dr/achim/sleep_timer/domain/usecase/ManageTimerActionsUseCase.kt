package dr.achim.sleep_timer.domain.usecase

import dr.achim.sleep_timer.data.SettingsRepository
import dr.achim.sleep_timer.model.EndActions
import dr.achim.sleep_timer.model.StartActions
import dr.achim.sleep_timer.model.TimerActions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

enum class VolumeType { START, END }

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

        val endActionsFlow = combine(
            settingsRepository.endStopMedia,
            settingsRepository.endAdjustVolume,
            settingsRepository.endVolumeLevel,
            settingsRepository.endTurnOffScreen,
            settingsRepository.endTurnOffBluetooth
        ) { stop, adjust, level, screen, bluetooth ->
            EndActions(
                stopMedia = stop,
                adjustVolume = adjust,
                volumeLevel = level,
                turnOffScreen = screen,
                turnOffBluetooth = bluetooth
            )
        }

        return combine(startActionsFlow, endActionsFlow) { start, end ->
            TimerActions(start, end)
        }
    }

    suspend fun setVolumeLevel(type: VolumeType, level: Int?) {
        when (type) {
            VolumeType.START -> settingsRepository.setStartVolumeLevel(level)
            VolumeType.END -> settingsRepository.setEndVolumeLevel(level)
        }
    }

    suspend fun setStartAdjustVolume(enabled: Boolean) {
        settingsRepository.setStartAdjustVolume(enabled)
    }

    suspend fun setStartEnableDnd(enabled: Boolean) {
        settingsRepository.setStartEnableDnd(enabled)
    }

    suspend fun setStartHueLights(enabled: Boolean) {
        settingsRepository.setStartHueLights(enabled)
    }

    suspend fun setEndStopMedia(enabled: Boolean) {
        settingsRepository.setEndStopMedia(enabled)
    }

    suspend fun setEndAdjustVolume(enabled: Boolean) {
        settingsRepository.setEndAdjustVolume(enabled)
    }

    suspend fun setEndTurnOffScreen(enabled: Boolean) {
        settingsRepository.setEndTurnOffScreen(enabled)
    }

    suspend fun setEndTurnOffBluetooth(enabled: Boolean) {
        settingsRepository.setEndTurnOffBluetooth(enabled)
    }
}
