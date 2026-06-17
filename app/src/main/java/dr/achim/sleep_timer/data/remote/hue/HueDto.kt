package dr.achim.sleep_timer.data.remote.hue

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HueBridge(
    val id: String,
    @SerialName("internalipaddress")
    val ipAddress: String,
    val name: String? = null,
)

@Serializable
data class HueGroup(
    val id: String = "",
    val name: String,
    val type: String
)

@Serializable
internal data class HuePairingRequest(val devicetype: String)

@Serializable
internal data class HuePairingResponse(
    val success: HueSuccess? = null,
    val error: HueError? = null
)

@Serializable
internal data class HueSuccess(val username: String)

@Serializable
internal data class HueError(val type: Int, val description: String)

@Serializable
internal data class HueStateRequest(val on: Boolean)

@Serializable
internal data class HueConfig(
    val name: String,
    val bridgeid: String
)
