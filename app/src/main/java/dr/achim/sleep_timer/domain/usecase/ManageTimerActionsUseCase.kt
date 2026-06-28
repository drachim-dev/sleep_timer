package dr.achim.sleep_timer.domain.usecase

import dr.achim.sleep_timer.data.SettingsRepository
import dr.achim.sleep_timer.model.EndActions
import dr.achim.sleep_timer.model.HueActionSource
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

    suspend fun setHueLights(source: HueActionSource, enabled: Boolean) {
        when (source) {
            HueActionSource.START -> settingsRepository.setStartHueLights(enabled)
            HueActionSource.END -> settingsRepository.setEndHueLights(enabled)
        }
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
