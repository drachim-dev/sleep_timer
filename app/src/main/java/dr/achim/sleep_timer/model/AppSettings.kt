package dr.achim.sleep_timer.model

import android.os.Build

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
    DYNAMIC
}

data class AppSettings(
    val themeMode: ThemeMode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        ThemeMode.DYNAMIC
    } else {
        ThemeMode.SYSTEM
    },
    val glowEffectEnabled: Boolean = true,
    val glowIntensity: Float = 20f,
    val extendOnShake: Boolean = false,
    val extendOnShakeMinutes: Int = 15
)
