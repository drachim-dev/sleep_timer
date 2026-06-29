package dr.achim.sleep_timer.presentation.settings

import android.app.Activity
import dr.achim.sleep_timer.model.ThemeMode

sealed interface SettingsUiAction {
    object RefreshDeviceAdminStatus : SettingsUiAction
    data class RefreshDndStatus(val autoEnable: Boolean) : SettingsUiAction
    object DisableDeviceAdmin : SettingsUiAction
    object DisableNotificationAccess : SettingsUiAction
    data class SetThemeMode(val themeMode: ThemeMode) : SettingsUiAction
    data class SetGlowEffectEnabled(val enabled: Boolean) : SettingsUiAction
    data class SetGlowIntensity(val intensity: Float) : SettingsUiAction
    data class SetExtendOnShake(val enabled: Boolean) : SettingsUiAction
    data class SetExtendOnShakeMinutes(val minutes: Int) : SettingsUiAction
    data class PurchaseProduct(val activity: Activity, val productId: String) : SettingsUiAction
}
