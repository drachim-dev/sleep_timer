package dr.achim.sleep_timer.model

import kotlinx.serialization.Serializable

@Serializable
data class TimerActions(
    val startActions: StartActions = StartActions(),
    val endActions: EndActions = EndActions()
)

@Serializable
enum class TimerActionSource {
    START, END
}

@Serializable
enum class TimerActionType {
    ADJUST_VOLUME,
    HUE_LIGHTS,
    DND,
    STOP_MEDIA,
    TURN_OFF_SCREEN,
    TURN_OFF_BLUETOOTH
}

@Serializable
data class StartActions(
    val adjustVolume: Boolean = false,
    val volumeLevel: Int? = null,
    val enableDnd: Boolean = false,
    val hueLights: Boolean = false
)

@Serializable
data class EndActions(
    val stopMedia: Boolean = true,
    val adjustVolume: Boolean = false,
    val volumeLevel: Int? = null,
    val turnOffScreen: Boolean = false,
    val turnOffBluetooth: Boolean = false,
    val hueLights: Boolean = false
)
