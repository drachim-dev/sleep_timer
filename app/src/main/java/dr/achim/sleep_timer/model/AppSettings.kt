package dr.achim.sleep_timer.model

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.default,
    val glowEffectEnabled: Boolean = true,
    val glowIntensity: Float = 20f,
    val extendOnShake: Boolean = false,
    val extendOnShakeMinutes: Int = 15,
    val timerStartCount: Int = 0,
    val lastReviewTimestamp: Long = 0L
)
