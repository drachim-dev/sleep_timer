package dr.achim.sleep_timer.domain.usecase

import dr.achim.sleep_timer.common.combine
import dr.achim.sleep_timer.data.SettingsRepository
import dr.achim.sleep_timer.model.AppSettings
import kotlinx.coroutines.flow.Flow

class GetSettingsUseCase(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<AppSettings> = combine(
        repository.themeMode,
        repository.glowEffectEnabled,
        repository.glowIntensity,
        repository.extendOnShake,
        repository.extendOnShakeMinutes,
        repository.lightsOffDelay,
        repository.lightsOffDelaySeconds,
        repository.timerStartCount,
        repository.lastReviewTimestamp
    ) { themeMode, glowEnabled, intensity, extendOnShake, extendOnShakeMinutes, lightsOffDelay, lightsOffDelaySeconds, timerStartCount, lastReviewTimestamp ->
        AppSettings(
            themeMode = themeMode,
            glowEffectEnabled = glowEnabled,
            glowIntensity = intensity,
            extendOnShake = extendOnShake,
            extendOnShakeMinutes = extendOnShakeMinutes,
            lightsOffDelay = lightsOffDelay,
            lightsOffDelaySeconds = lightsOffDelaySeconds,
            timerStartCount = timerStartCount,
            lastReviewTimestamp = lastReviewTimestamp
        )
    }
}
