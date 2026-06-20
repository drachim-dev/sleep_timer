package dr.achim.sleep_timer.domain.usecase

import dr.achim.sleep_timer.data.SettingsRepository
import dr.achim.sleep_timer.model.ThemeMode

class UpdateSettingsUseCase(private val repository: SettingsRepository) {
    suspend fun setThemeMode(themeMode: ThemeMode) {
        repository.setThemeMode(themeMode)
    }

    suspend fun setGlowEffectEnabled(enabled: Boolean) {
        repository.setGlowEffectEnabled(enabled)
    }

    suspend fun setGlowIntensity(intensity: Float) {
        repository.setGlowIntensity(intensity)
    }

    suspend fun setExtendOnShake(enabled: Boolean) {
        repository.setExtendOnShake(enabled)
    }

    suspend fun setExtendOnShakeMinutes(minutes: Int) {
        repository.setExtendOnShakeMinutes(minutes)
    }
}
