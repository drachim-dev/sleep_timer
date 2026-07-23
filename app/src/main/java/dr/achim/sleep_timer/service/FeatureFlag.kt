package dr.achim.sleep_timer.service

import dr.achim.sleep_timer.BuildConfig

enum class FeatureFlag(val enabled: Boolean = BuildConfig.DEBUG) {
    DelayLightsOff
}