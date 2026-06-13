package dr.achim.sleep_timer.model

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
    DYNAMIC
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val glowEffectEnabled: Boolean = true,
    val glowIntensity: Float = 20f,
    val extendOnShake: Boolean = false
)
