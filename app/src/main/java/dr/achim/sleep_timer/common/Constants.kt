package dr.achim.sleep_timer.common

import kotlin.time.Duration.Companion.milliseconds

object Constants {
    val ExtendOnShakeSteps = listOf(5, 10, 15, 20, 30, 45, 60)
    val MinLoadingDuration = 400.milliseconds
}
