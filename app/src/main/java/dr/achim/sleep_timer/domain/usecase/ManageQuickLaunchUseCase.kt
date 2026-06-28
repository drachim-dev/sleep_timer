package dr.achim.sleep_timer.domain.usecase

import dr.achim.sleep_timer.data.SettingsRepository
import dr.achim.sleep_timer.domain.model.QuickLaunchApp
import dr.achim.sleep_timer.domain.repository.QuickLaunchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ManageQuickLaunchUseCase(
    private val settingsRepository: SettingsRepository,
    private val quickLaunchRepository: QuickLaunchRepository
) {
    fun getApps(): List<QuickLaunchApp> = quickLaunchRepository.getQuickLaunchApps()

    fun getSelectedApps(): Flow<List<QuickLaunchApp?>> {
        return combine(
            settingsRepository.quickLaunchApp1,
            settingsRepository.quickLaunchApp2
        ) { app1, app2 ->
            val q1 = app1?.let { quickLaunchRepository.getQuickLaunchApp(it) }
                ?: quickLaunchRepository.getDefaultAlarmClockPackage()
                    ?.let { quickLaunchRepository.getQuickLaunchApp(it) }
            val q2 = app2?.let { quickLaunchRepository.getQuickLaunchApp(it) }
            listOf(q1, q2)
        }
    }

    suspend fun setQuickLaunchApp(index: Int, packageName: String?) {
        if (index == 0) {
            settingsRepository.setQuickLaunchApp1(packageName)
        } else {
            settingsRepository.setQuickLaunchApp2(packageName)
        }
    }
}
