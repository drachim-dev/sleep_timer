package dr.achim.sleep_timer.domain.usecase

import dr.achim.sleep_timer.data.HueRepository
import dr.achim.sleep_timer.data.PairResult
import dr.achim.sleep_timer.data.remote.hue.HueBridge
import dr.achim.sleep_timer.model.HueActionSource
import kotlinx.coroutines.flow.firstOrNull

class ManageHueUseCase(private val hueRepository: HueRepository) {

    suspend fun discoverBridges(): List<HueBridge> = hueRepository.discoverBridges()

    suspend fun discoverBridgeByIp(ip: String): HueBridge? = hueRepository.discoverBridgeByIp(ip)

    suspend fun pair(bridge: HueBridge): PairResult = hueRepository.pair(bridge)

    suspend fun fetchGroups(ip: String, username: String) = hueRepository.fetchGroups(ip, username)

    fun getPairedIp() = hueRepository.getPairedIp()

    fun getPairedUser() = hueRepository.getPairedUser()

    fun getStartGroups() = hueRepository.getStartGroups()
    suspend fun setStartGroups(groups: Set<String>) = hueRepository.setStartGroups(groups)
    fun getEndGroups() = hueRepository.getEndGroups()
    suspend fun setEndGroups(groups: Set<String>) = hueRepository.setEndGroups(groups)

    suspend fun isConfigured(source: HueActionSource): Boolean {
        val ip = hueRepository.getPairedIp().firstOrNull()
        val user = hueRepository.getPairedUser().firstOrNull()
        if (ip == null || user == null) return false

        val groups = when (source) {
            HueActionSource.START -> hueRepository.getStartGroups().firstOrNull()
            HueActionSource.END -> hueRepository.getEndGroups().firstOrNull()
        }
        return groups?.isNotEmpty() == true
    }
}
