package dr.achim.sleep_timer.domain.repository

import dr.achim.sleep_timer.domain.model.QuickLaunchApp

interface QuickLaunchRepository {
    fun getQuickLaunchApps(): List<QuickLaunchApp>
    fun getQuickLaunchApp(packageName: String): QuickLaunchApp?
    fun getDefaultAlarmClockPackage(): String?
}
