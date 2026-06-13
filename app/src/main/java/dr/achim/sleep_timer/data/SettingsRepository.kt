package dr.achim.sleep_timer.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dr.achim.sleep_timer.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_prefs")

class SettingsRepository(private val context: Context) {

    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        private val GLOW_EFFECT_ENABLED_KEY = booleanPreferencesKey("glow_effect_enabled")
        private val GLOW_INTENSITY_KEY = floatPreferencesKey("glow_intensity")
        private val EXTEND_ON_SHAKE_KEY = booleanPreferencesKey("extend_on_shake")
        private val QUICK_LAUNCH_APP_1_KEY = stringPreferencesKey("quick_launch_app_1")
        private val QUICK_LAUNCH_APP_2_KEY = stringPreferencesKey("quick_launch_app_2")
        private val START_VOLUME_LEVEL_KEY = intPreferencesKey("start_volume_level")
        private val START_ADJUST_VOLUME_KEY = booleanPreferencesKey("start_adjust_volume")
        private val START_ENABLE_DND_KEY = booleanPreferencesKey("start_enable_dnd")
        private val START_HUE_LIGHTS_KEY = booleanPreferencesKey("start_hue_lights")

        private val END_VOLUME_LEVEL_KEY = intPreferencesKey("end_volume_level")
        private val END_ADJUST_VOLUME_KEY = booleanPreferencesKey("end_adjust_volume")
        private val END_STOP_MEDIA_KEY = booleanPreferencesKey("end_stop_media")
        private val END_TURN_OFF_SCREEN_KEY = booleanPreferencesKey("end_turn_off_screen")
        private val END_TURN_OFF_BLUETOOTH_KEY = booleanPreferencesKey("end_turn_off_bluetooth")

        private const val DEFAULT_GLOW_EFFECT_ENABLED = true
        private const val DEFAULT_GLOW_INTENSITY = 20f
        private const val DEFAULT_EXTEND_ON_SHAKE = false
    }

    val themeMode: Flow<ThemeMode> = context.settingsDataStore.data.map { preferences ->
        val savedTheme = preferences[THEME_MODE_KEY]
        try {
            if (savedTheme != null) ThemeMode.valueOf(savedTheme) else ThemeMode.SYSTEM
        } catch (_: IllegalArgumentException) {
            ThemeMode.SYSTEM
        }
    }

    val glowEffectEnabled: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[GLOW_EFFECT_ENABLED_KEY] ?: DEFAULT_GLOW_EFFECT_ENABLED
    }

    val glowIntensity: Flow<Float> = context.settingsDataStore.data.map { preferences ->
        preferences[GLOW_INTENSITY_KEY] ?: DEFAULT_GLOW_INTENSITY
    }

    val extendOnShake: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[EXTEND_ON_SHAKE_KEY] ?: DEFAULT_EXTEND_ON_SHAKE
    }

    val quickLaunchApp1: Flow<String?> = context.settingsDataStore.data.map { preferences ->
        preferences[QUICK_LAUNCH_APP_1_KEY]
    }

    val quickLaunchApp2: Flow<String?> = context.settingsDataStore.data.map { preferences ->
        preferences[QUICK_LAUNCH_APP_2_KEY]
    }

    val startVolumeLevel: Flow<Int?> = context.settingsDataStore.data.map { preferences ->
        preferences[START_VOLUME_LEVEL_KEY]
    }

    val startAdjustVolume: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[START_ADJUST_VOLUME_KEY] ?: true
    }

    val startEnableDnd: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[START_ENABLE_DND_KEY] ?: false
    }

    val startHueLights: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[START_HUE_LIGHTS_KEY] ?: false
    }

    val endVolumeLevel: Flow<Int?> = context.settingsDataStore.data.map { preferences ->
        preferences[END_VOLUME_LEVEL_KEY]
    }

    val endAdjustVolume: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[END_ADJUST_VOLUME_KEY] ?: false
    }

    val endStopMedia: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[END_STOP_MEDIA_KEY] ?: true
    }

    val endTurnOffScreen: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[END_TURN_OFF_SCREEN_KEY] ?: false
    }

    val endTurnOffBluetooth: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[END_TURN_OFF_BLUETOOTH_KEY] ?: false
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        context.settingsDataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = themeMode.name
        }
    }

    suspend fun setGlowEffectEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[GLOW_EFFECT_ENABLED_KEY] = enabled
        }
    }

    suspend fun setGlowIntensity(intensity: Float) {
        context.settingsDataStore.edit { preferences ->
            preferences[GLOW_INTENSITY_KEY] = intensity
        }
    }

    suspend fun setExtendOnShake(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[EXTEND_ON_SHAKE_KEY] = enabled
        }
    }

    suspend fun setQuickLaunchApp1(packageName: String?) {
        context.settingsDataStore.edit { preferences ->
            if (packageName != null) {
                preferences[QUICK_LAUNCH_APP_1_KEY] = packageName
            } else {
                preferences.remove(QUICK_LAUNCH_APP_1_KEY)
            }
        }
    }

    suspend fun setQuickLaunchApp2(packageName: String?) {
        context.settingsDataStore.edit { preferences ->
            if (packageName != null) {
                preferences[QUICK_LAUNCH_APP_2_KEY] = packageName
            } else {
                preferences.remove(QUICK_LAUNCH_APP_2_KEY)
            }
        }
    }

    suspend fun setStartVolumeLevel(level: Int?) {
        context.settingsDataStore.edit { preferences ->
            if (level != null) {
                preferences[START_VOLUME_LEVEL_KEY] = level
            } else {
                preferences.remove(START_VOLUME_LEVEL_KEY)
            }
        }
    }

    suspend fun setStartAdjustVolume(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[START_ADJUST_VOLUME_KEY] = enabled
        }
    }

    suspend fun setStartEnableDnd(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[START_ENABLE_DND_KEY] = enabled
        }
    }

    suspend fun setStartHueLights(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[START_HUE_LIGHTS_KEY] = enabled
        }
    }

    suspend fun setEndVolumeLevel(level: Int?) {
        context.settingsDataStore.edit { preferences ->
            if (level != null) {
                preferences[END_VOLUME_LEVEL_KEY] = level
            } else {
                preferences.remove(END_VOLUME_LEVEL_KEY)
            }
        }
    }

    suspend fun setEndAdjustVolume(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[END_ADJUST_VOLUME_KEY] = enabled
        }
    }

    suspend fun setEndStopMedia(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[END_STOP_MEDIA_KEY] = enabled
        }
    }

    suspend fun setEndTurnOffScreen(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[END_TURN_OFF_SCREEN_KEY] = enabled
        }
    }

    suspend fun setEndTurnOffBluetooth(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[END_TURN_OFF_BLUETOOTH_KEY] = enabled
        }
    }
}
