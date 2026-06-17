package dr.achim.sleep_timer.presentation.hue

import dr.achim.sleep_timer.data.remote.hue.HueBridge

sealed interface HueDiscoveryUiAction {
    data object DiscoverBridges : HueDiscoveryUiAction
    data class StartPairing(val bridge: HueBridge) : HueDiscoveryUiAction
    data class ManualIpDiscovery(val ip: String) : HueDiscoveryUiAction
    data object PairBridge : HueDiscoveryUiAction
    data object CancelPairing : HueDiscoveryUiAction
    data object NavigateToRoomSelection : HueDiscoveryUiAction
    data object PermissionGranted : HueDiscoveryUiAction
}
