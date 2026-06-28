package dr.achim.sleep_timer.common

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory
import dr.achim.sleep_timer.data.SettingsRepository
import dr.achim.sleep_timer.domain.repository.TimerRepository
import dr.achim.sleep_timer.model.TimerState
import kotlinx.coroutines.flow.first

class ReviewManager(
    private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val timerRepository: TimerRepository
) {
    private val manager = ReviewManagerFactory.create(context)

    suspend fun tryShowReview(activity: Activity) {
        if (shouldShowReview()) {
            launchReviewFlow(activity)
            settingsRepository.setLastReviewTimestamp(System.currentTimeMillis())
        }
    }

    private suspend fun shouldShowReview(): Boolean {
        val installTime = context.packageManager
            .getPackageInfo(context.packageName, 0)
            .firstInstallTime
        val installDays = (System.currentTimeMillis() - installTime).toDays()

        val timerStartCount = settingsRepository.timerStartCount.first()
        val lastReviewTimestamp = settingsRepository.lastReviewTimestamp.first()
        val timerState = timerRepository.timerState.value

        return timerState is TimerState.Idle &&
                installDays > Constants.REVIEW_INSTALL_DAYS_THRESHOLD &&
                timerStartCount > Constants.REVIEW_TIMER_COUNT_THRESHOLD &&
                (System.currentTimeMillis() - lastReviewTimestamp).toDays() > Constants.REVIEW_INTERVAL_DAYS
    }

    private fun launchReviewFlow(activity: Activity) {
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                manager.launchReviewFlow(activity, reviewInfo)
            }
        }
    }
}
