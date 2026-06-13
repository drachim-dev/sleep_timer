package dr.achim.sleep_timer.domain.usecase

import dr.achim.sleep_timer.data.QuickTimesRepository

class UpdateQuickTimeUseCase(private val repository: QuickTimesRepository) {
    suspend operator fun invoke(index: Int, minutes: Int) {
        if (minutes > 0) {
            repository.updateQuickTime(index, minutes)
        }
    }
}
