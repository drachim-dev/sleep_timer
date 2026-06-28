package dr.achim.sleep_timer.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.provider.AlarmClock
import dr.achim.sleep_timer.domain.model.AppCategory
import dr.achim.sleep_timer.domain.model.QuickLaunchApp
import dr.achim.sleep_timer.domain.repository.QuickLaunchRepository

class QuickLaunchRepositoryImpl(private val context: Context) : QuickLaunchRepository {

    @SuppressLint("QueryPermissionsNeeded")
    override fun getQuickLaunchApps(): List<QuickLaunchApp> {
        val packageManager = context.packageManager
        val apps = mutableMapOf<String, QuickLaunchApp>()

        // Discover apps by common intents that match your manifest queries
        val intents = listOf(
            Intent(Intent.ACTION_VIEW).apply { setType("audio/*") } to AppCategory.MEDIA,
            Intent(Intent.ACTION_VIEW).apply { setType("video/*") } to AppCategory.MEDIA,
            Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_APP_MUSIC) } to AppCategory.MEDIA,
            Intent(AlarmClock.ACTION_SHOW_ALARMS) to AppCategory.ALARM,
            Intent("android.intent.action.SET_ALARM") to AppCategory.ALARM
        )

        intents.forEach { (intent, category) ->
            val resolved = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            resolved.forEach { resolveInfo ->
                val packageName = resolveInfo.activityInfo.packageName
                if (packageName != context.packageName && !apps.containsKey(packageName)) {
                    apps[packageName] = QuickLaunchApp(
                        name = resolveInfo.loadLabel(packageManager).toString(),
                        packageName = packageName,
                        icon = resolveInfo.loadIcon(packageManager),
                        category = category
                    )
                }
            }
        }

        // Also check visible applications for relevant categories (leveraging <queries> visibility)
        val allVisibleApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        allVisibleApps.forEach { appInfo ->
            val packageName = appInfo.packageName
            if (packageName != context.packageName && !apps.containsKey(packageName)) {
                val isRelevantMedia = appInfo.category == ApplicationInfo.CATEGORY_AUDIO ||
                        appInfo.category == ApplicationInfo.CATEGORY_VIDEO

                if (isRelevantMedia) {
                    apps[packageName] = QuickLaunchApp(
                        name = packageManager.getApplicationLabel(appInfo).toString(),
                        packageName = packageName,
                        icon = packageManager.getApplicationIcon(appInfo),
                        category = AppCategory.MEDIA
                    )
                }
            }
        }

        return apps.values.filter {
            packageManager.getLaunchIntentForPackage(it.packageName) != null
        }.sortedBy { it.name }
    }

    override fun getQuickLaunchApp(packageName: String): QuickLaunchApp? {
        return try {
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val category = if (appInfo.category == ApplicationInfo.CATEGORY_AUDIO ||
                appInfo.category == ApplicationInfo.CATEGORY_VIDEO
            ) AppCategory.MEDIA else AppCategory.ALARM // Defaulting to ALARM if not media for simplicity, or we could try to infer more

            QuickLaunchApp(
                name = packageManager.getApplicationLabel(appInfo).toString(),
                packageName = packageName,
                icon = packageManager.getApplicationIcon(appInfo),
                category = category
            )
        } catch (_: PackageManager.NameNotFoundException) {
            null
        }
    }

    override fun getDefaultAlarmClockPackage(): String? {
        val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
        val resolved =
            context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolved?.activityInfo?.packageName
    }
}
