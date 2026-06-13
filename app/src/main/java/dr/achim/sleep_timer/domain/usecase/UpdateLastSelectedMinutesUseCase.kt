package dr.achim.sleep_timer.domain.usecase

import dr.achim.sleep_timer.data.QuickTimesRepository

class UpdateLastSelectedMinutesUseCase(private val repository: QuickTimesRepository) {
    suspend operator fun invoke(minutes: Int) {
        if (minutes > 0) {
            repository.updateLastSelectedMinutes(minutes)
        }
    }
}
