package dr.achim.sleep_timer.presentation.hue

import dr.achim.sleep_timer.data.remote.hue.HueBridge

sealed interface HueDiscoveryUiAction {
    data object DiscoverBridges : HueDiscoveryUiAction
    data class StartLinking(val bridge: HueBridge) : HueDiscoveryUiAction
    data class ManualIpDiscovery(val ip: String) : HueDiscoveryUiAction
    data object LinkBridge : HueDiscoveryUiAction
    data object UnlinkBridge : HueDiscoveryUiAction
    data object CancelLinking : HueDiscoveryUiAction
    data object NavigateToRoomSelection : HueDiscoveryUiAction
    data object PermissionGranted : HueDiscoveryUiAction
    data class PermissionDenied(val shouldShowRationale: Boolean) : HueDiscoveryUiAction
}
