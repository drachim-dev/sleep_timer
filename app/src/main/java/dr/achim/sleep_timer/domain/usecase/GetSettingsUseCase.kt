package dr.achim.sleep_timer.domain.usecase

import dr.achim.sleep_timer.data.SettingsRepository
import dr.achim.sleep_timer.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetSettingsUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Flow<AppSettings> = combine(
        repository.themeMode,
        repository.glowEffectEnabled,
        repository.glowIntensity,
        repository.extendOnShake
    ) { themeMode, glowEnabled, intensity, extendOnShake ->
        AppSettings(
            themeMode = themeMode,
            glowEffectEnabled = glowEnabled,
            glowIntensity = intensity,
            extendOnShake = extendOnShake
        )
    }
}
