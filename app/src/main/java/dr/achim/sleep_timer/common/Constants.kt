package dr.achim.sleep_timer.common

import kotlin.time.Duration.Companion.milliseconds

object Constants {
    val EXTEND_ON_SHAKE_STEPS = listOf(5, 10, 15, 20, 30, 45, 60)
    val MIN_LOADING_DURATION = 400.milliseconds

    const val REVIEW_INSTALL_DAYS_THRESHOLD = 10
    const val REVIEW_TIMER_COUNT_THRESHOLD = 5
    const val REVIEW_INTERVAL_DAYS = 30
}
