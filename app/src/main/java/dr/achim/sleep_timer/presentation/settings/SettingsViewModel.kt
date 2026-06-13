package dr.achim.sleep_timer.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dr.achim.sleep_timer.data.TimerController
import dr.achim.sleep_timer.domain.usecase.CheckTimerPermissionsUseCase
import dr.achim.sleep_timer.domain.usecase.GetSettingsUseCase
import dr.achim.sleep_timer.domain.usecase.ManageTimerActionsUseCase
import dr.achim.sleep_timer.domain.usecase.UpdateSettingsUseCase
import dr.achim.sleep_timer.model.AppSettings
import dr.achim.sleep_timer.model.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val timerController: TimerController,
    getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val manageTimerActionsUseCase: ManageTimerActionsUseCase,
    private val checkTimerPermissionsUseCase: CheckTimerPermissionsUseCase,
) : ViewModel() {

    private val settings = getSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings()
        )

    val themeMode: StateFlow<ThemeMode> = settings
        .map { it.themeMode }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings().themeMode
        )

    val glowEffectEnabled: StateFlow<Boolean> = settings
        .map { it.glowEffectEnabled }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings().glowEffectEnabled
        )

    val glowIntensity: StateFlow<Float> = settings
        .map { it.glowIntensity }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings().glowIntensity
        )

    val extendOnShake: StateFlow<Boolean> = settings
        .map { it.extendOnShake }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings().extendOnShake
        )

    private val _isDeviceAdminEnabled = MutableStateFlow(checkTimerPermissionsUseCase.isDeviceAdminEnabled())
    val isDeviceAdminEnabled: StateFlow<Boolean> = _isDeviceAdminEnabled.asStateFlow()

    private val _hasNotificationAccess = MutableStateFlow(checkTimerPermissionsUseCase.hasNotificationAccess())
    val hasNotificationAccess: StateFlow<Boolean> = _hasNotificationAccess.asStateFlow()

    fun onAction(action: SettingsUiAction) {
        when (action) {
            is SettingsUiAction.RefreshDeviceAdminStatus -> refreshDeviceAdminStatus()
            is SettingsUiAction.RefreshDndStatus -> refreshDndStatus()
            is SettingsUiAction.DisableDeviceAdmin -> disableDeviceAdmin()
            is SettingsUiAction.DisableNotificationAccess -> disableNotificationAccess()
            is SettingsUiAction.SetThemeMode -> setThemeMode(action.themeMode)
            is SettingsUiAction.SetGlowEffectEnabled -> setGlowEffectEnabled(action.enabled)
            is SettingsUiAction.SetGlowIntensity -> setGlowIntensity(action.intensity)
            is SettingsUiAction.SetExtendOnShake -> setExtendOnShake(action.enabled)
        }
    }

    private fun refreshDeviceAdminStatus(): Boolean {
        val enabled = checkTimerPermissionsUseCase.isDeviceAdminEnabled()
        _isDeviceAdminEnabled.value = enabled
        return enabled
    }

    private fun refreshDndStatus(): Boolean {
        val enabled = checkTimerPermissionsUseCase.hasNotificationAccess()
        _hasNotificationAccess.value = enabled
        return enabled
    }

    private fun disableDeviceAdmin() {
        timerController.removeActiveAdmin()
        _isDeviceAdminEnabled.value = false
        viewModelScope.launch {
            manageTimerActionsUseCase.setEndTurnOffScreen(false)
        }
    }

    private fun disableNotificationAccess() {
        timerController.removeActiveAdmin()
        _isDeviceAdminEnabled.value = false
        viewModelScope.launch {
            manageTimerActionsUseCase.setEndTurnOffScreen(false)
        }
    }

    private fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            updateSettingsUseCase.setThemeMode(themeMode)
        }
    }

    private fun setGlowEffectEnabled(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.setGlowEffectEnabled(enabled)
        }
    }

    private fun setGlowIntensity(intensity: Float) {
        viewModelScope.launch {
            updateSettingsUseCase.setGlowIntensity(intensity)
        }
    }

    private fun setExtendOnShake(enabled: Boolean) {
        viewModelScope.launch {
            updateSettingsUseCase.setExtendOnShake(enabled)
        }
    }
}
