package dr.achim.sleep_timer.domain.usecase

import dr.achim.sleep_timer.data.QuickTimesRepository
import kotlinx.coroutines.flow.Flow

class GetLastSelectedMinutesUseCase(private val repository: QuickTimesRepository) {
    operator fun invoke(): Flow<Int> = repository.lastSelectedMinutes
}
