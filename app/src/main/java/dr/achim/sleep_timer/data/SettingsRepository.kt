package dr.achim.sleep_timer.data

import android.content.Context
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dr.achim.sleep_timer.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_prefs")

class SettingsRepository(private val context: Context) {

    companion object {
        private val FIRST_LAUNCH_KEY = booleanPreferencesKey("pref_key_first_launch")
        private val THEME_MODE_KEY = stringPreferencesKey("pref_key_theme_mode")
        private val GLOW_EFFECT_ENABLED_KEY = booleanPreferencesKey("pref_key_glow")
        private val GLOW_INTENSITY_KEY = floatPreferencesKey("pref_key_glow_intensity")
        private val EXTEND_ON_SHAKE_KEY = booleanPreferencesKey("pref_key_extend_on_shake")
        private val EXTEND_ON_SHAKE_MINUTES_KEY = intPreferencesKey("pref_key_default_extend_time_by_shake")
        private val QUICK_LAUNCH_APP_1_KEY = stringPreferencesKey("pref_key_quick_launch_app_1")
        private val QUICK_LAUNCH_APP_2_KEY = stringPreferencesKey("pref_key_quick_launch_app_2")
        private val START_VOLUME_LEVEL_KEY = intPreferencesKey("pref_key_volume_level")
        private val START_ADJUST_VOLUME_KEY = booleanPreferencesKey("pref_key_start_adjust_volume")
        private val START_ENABLE_DND_KEY = booleanPreferencesKey("pref_key_start_enable_dnd")
        private val START_HUE_LIGHTS_KEY = booleanPreferencesKey("pref_key_start_hue_lights")
        private val END_VOLUME_LEVEL_KEY = intPreferencesKey("pref_key_volume_end_level")
        private val END_ADJUST_VOLUME_KEY = booleanPreferencesKey("pref_key_end_adjust_volume")
        private val END_STOP_MEDIA_KEY = booleanPreferencesKey("pref_key_end_stop_media")
        private val END_TURN_OFF_SCREEN_KEY = booleanPreferencesKey("pref_key_end_turn_off_screen")
        private val END_TURN_OFF_BLUETOOTH_KEY = booleanPreferencesKey("pref_key_end_turn_off_bluetooth")
        private val END_HUE_LIGHTS_KEY = booleanPreferencesKey("pref_key_end_hue_lights")

        private val HUE_BRIDGE_IP_KEY = stringPreferencesKey("pref_key_hue_bridge_ip")
        private val HUE_API_USER_KEY = stringPreferencesKey("pref_key_hue_api_user")
        private val HUE_START_GROUPS_KEY = stringSetPreferencesKey("pref_key_hue_start_groups")
        private val HUE_END_GROUPS_KEY = stringSetPreferencesKey("pref_key_hue_end_groups")

        private val TIMER_START_COUNT_KEY = intPreferencesKey("pref_key_timer_start_count")
        private val LAST_REVIEW_TIMESTAMP_KEY = longPreferencesKey("pref_key_last_review_timestamp")

        /**
         * Default values
         */
        private const val DEFAULT_GLOW_EFFECT_ENABLED = true
        private const val DEFAULT_GLOW_INTENSITY = 40f
        private const val DEFAULT_EXTEND_ON_SHAKE = false
        private const val DEFAULT_EXTEND_ON_SHAKE_MINUTES = 15
    }

    val isFirstLaunch: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[FIRST_LAUNCH_KEY] ?: true
    }

    val themeMode: Flow<ThemeMode> = context.settingsDataStore.data.map { preferences ->
        val savedTheme = preferences[THEME_MODE_KEY]
        val defaultTheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ThemeMode.DYNAMIC
        } else {
            ThemeMode.SYSTEM
        }
        try {
            val theme = if (savedTheme != null) ThemeMode.valueOf(savedTheme) else defaultTheme
            if (theme == ThemeMode.DYNAMIC && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                ThemeMode.SYSTEM
            } else {
                theme
            }
        } catch (_: IllegalArgumentException) {
            defaultTheme
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

    val extendOnShakeMinutes: Flow<Int> = context.settingsDataStore.data.map { preferences ->
        preferences[EXTEND_ON_SHAKE_MINUTES_KEY] ?: DEFAULT_EXTEND_ON_SHAKE_MINUTES
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
        preferences[START_ADJUST_VOLUME_KEY] ?: false
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

    val endHueLights: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[END_HUE_LIGHTS_KEY] ?: false
    }

    val hueBridgeIp: Flow<String?> = context.settingsDataStore.data.map { preferences ->
        preferences[HUE_BRIDGE_IP_KEY]
    }

    val hueApiUser: Flow<String?> = context.settingsDataStore.data.map { preferences ->
        preferences[HUE_API_USER_KEY]
    }

    val hueStartGroups: Flow<Set<String>> = context.settingsDataStore.data.map { preferences ->
        preferences[HUE_START_GROUPS_KEY] ?: emptySet()
    }

    val hueEndGroups: Flow<Set<String>> = context.settingsDataStore.data.map { preferences ->
        preferences[HUE_END_GROUPS_KEY] ?: emptySet()
    }

    val timerStartCount: Flow<Int> = context.settingsDataStore.data.map { preferences ->
        preferences[TIMER_START_COUNT_KEY] ?: 0
    }

    val lastReviewTimestamp: Flow<Long> = context.settingsDataStore.data.map { preferences ->
        preferences[LAST_REVIEW_TIMESTAMP_KEY] ?: 0L
    }

    suspend fun setFirstLaunchCompleted() {
        context.settingsDataStore.edit { preferences ->
            preferences[FIRST_LAUNCH_KEY] = false
        }
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

    suspend fun setExtendOnShakeMinutes(minutes: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[EXTEND_ON_SHAKE_MINUTES_KEY] = minutes
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

    suspend fun setEndHueLights(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[END_HUE_LIGHTS_KEY] = enabled
        }
    }

    suspend fun setHueBridgeSettings(ip: String, user: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[HUE_BRIDGE_IP_KEY] = ip
            preferences[HUE_API_USER_KEY] = user
        }
    }

    suspend fun setHueStartGroups(groups: Set<String>) {
        context.settingsDataStore.edit { preferences ->
            preferences[HUE_START_GROUPS_KEY] = groups
        }
    }

    suspend fun setHueEndGroups(groups: Set<String>) {
        context.settingsDataStore.edit { preferences ->
            preferences[HUE_END_GROUPS_KEY] = groups
        }
    }

    suspend fun setLastReviewTimestamp(timestamp: Long) {
        context.settingsDataStore.edit { preferences ->
            preferences[LAST_REVIEW_TIMESTAMP_KEY] = timestamp
        }
    }

    suspend fun clearHueBridgeSettings() {
        context.settingsDataStore.edit { preferences ->
            preferences.remove(HUE_BRIDGE_IP_KEY)
            preferences.remove(HUE_API_USER_KEY)
            preferences.remove(HUE_START_GROUPS_KEY)
            preferences.remove(HUE_END_GROUPS_KEY)
            preferences[START_HUE_LIGHTS_KEY] = false
            preferences[END_HUE_LIGHTS_KEY] = false
        }
    }

    suspend fun incrementAndGetTimerStartCount(): Int {
        return context.settingsDataStore.edit { preferences ->
            val current = preferences[TIMER_START_COUNT_KEY] ?: 0
            val next = current + 1
            preferences[TIMER_START_COUNT_KEY] = next
        }[TIMER_START_COUNT_KEY] ?: 0
    }
}
