package dr.achim.sleep_timer.domain.model

data class QuickLaunchApp(
    val name: String,
    val packageName: String,
    val icon: Any? = null,
    val category: AppCategory = AppCategory.MEDIA
)

enum class AppCategory {
    MEDIA, ALARM
}
