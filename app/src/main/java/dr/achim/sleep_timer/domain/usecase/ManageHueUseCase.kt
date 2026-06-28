package dr.achim.sleep_timer.domain.usecase

import dr.achim.sleep_timer.data.HueRepository
import dr.achim.sleep_timer.data.LinkResult
import dr.achim.sleep_timer.data.TimerController
import dr.achim.sleep_timer.data.remote.hue.HueBridge
import dr.achim.sleep_timer.model.TimerActionSource
import kotlinx.coroutines.flow.firstOrNull

class ManageHueUseCase(
    private val hueRepository: HueRepository,
    private val timerController: TimerController
) {

    suspend fun discoverBridges(): List<HueBridge> = hueRepository.discoverBridges()

    suspend fun discoverBridgeByIp(ip: String): HueBridge? = hueRepository.discoverBridgeByIp(ip)

    suspend fun link(bridge: HueBridge): LinkResult = hueRepository.link(bridge)

    suspend fun unlink() = hueRepository.unlink()

    suspend fun fetchGroups(ip: String, username: String) = hueRepository.fetchGroups(ip, username)

    fun getPairedIp() = hueRepository.getPairedIp()

    fun getPairedUser() = hueRepository.getPairedUser()

    fun observeGroups(source: TimerActionSource) = when (source) {
        TimerActionSource.START -> hueRepository.getStartGroups()
        TimerActionSource.END -> hueRepository.getEndGroups()
    }

    fun getGroups(source: TimerActionSource) = when (source) {
        TimerActionSource.START -> hueRepository.getStartGroups()
        TimerActionSource.END -> hueRepository.getEndGroups()
    }

    suspend fun setGroups(source: TimerActionSource, groups: Set<String>) = when (source) {
        TimerActionSource.START -> hueRepository.setStartGroups(groups)
        TimerActionSource.END -> hueRepository.setEndGroups(groups)
    }

    fun hasNearbyPermission() = timerController.hasNearbyPermission()

    fun getNearbyPermissions() = timerController.getNearbyPermissions()

    suspend fun isConfigured(source: TimerActionSource): Boolean {
        val ip = hueRepository.getPairedIp().firstOrNull()
        val user = hueRepository.getPairedUser().firstOrNull()
        if (ip == null || user == null) return false

        val groups = when (source) {
            TimerActionSource.START -> hueRepository.getStartGroups().firstOrNull()
            TimerActionSource.END -> hueRepository.getEndGroups().firstOrNull()
        }
        return groups?.isNotEmpty() == true
    }
}
