package dr.achim.sleep_timer.domain.usecase

import dr.achim.sleep_timer.data.QuickTimesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetQuickTimesUseCase(private val repository: QuickTimesRepository) {
    operator fun invoke(): Flow<List<Int>> = repository.quickTimes.map { quickTimes ->
        quickTimes
            .distinct()
            .sortedWith(compareBy<Int> { it == -1 }.thenBy { it })
    }
}
