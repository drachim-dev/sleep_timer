package dr.achim.sleep_timer.data

import android.net.nsd.DiscoveryRequest
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import dr.achim.sleep_timer.data.remote.hue.HueBridge
import dr.achim.sleep_timer.data.remote.hue.HueGroup
import dr.achim.sleep_timer.data.remote.hue.HuePairingRequest
import dr.achim.sleep_timer.data.remote.hue.HuePairingResponse
import dr.achim.sleep_timer.data.remote.hue.HueStateRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds

class HueRepository(
    private val settingsRepository: SettingsRepository,
    private val client: HttpClient,
    private val nsdManager: NsdManager
) {

    companion object {
        private const val DISCOVERY_URL = "https://discovery.meethue.com"
        private const val DEVICE_TYPE = "Comfy Sleep Timer"
        private const val ERROR_LINK_BUTTON_NOT_PRESSED = 101
        private const val SERVICE_TYPE = "_hue._tcp."
    }

    suspend fun discoverBridges(): List<HueBridge> {
        if (Build.VERSION.SDK_INT >= 37) {
            // On Android 17+, the mDNS picker is the preferred privacy-preserving way to discover and connect
            val mdnsBridges = discoverBridgesMdns()
            if (mdnsBridges.isNotEmpty()) return mdnsBridges
        }

        val nupnpBridges = discoverBridgesNupnp()
        return nupnpBridges.ifEmpty {
            discoverBridgesMdns()
        }
    }

    private suspend fun discoverBridgesNupnp(): List<HueBridge> = try {
        client.get(DISCOVERY_URL).body()
    } catch (e: Exception) {
        println(e)
        emptyList()
    }

    suspend fun discoverBridgeByIp(ip: String): HueBridge? = try {
        val config: dr.achim.sleep_timer.data.remote.hue.HueConfig =
            client.get("http://$ip/api/config").body()
        HueBridge(
            id = config.bridgeid,
            name = config.name,
            ipAddress = ip
        )
    } catch (_: Exception) {
        null
    }

    private suspend fun discoverBridgesMdns(): List<HueBridge> = withTimeoutOrNull(5.seconds) {
        callbackFlow {
            val bridges = mutableSetOf<HueBridge>()
            val discoveryListener = object : NsdManager.DiscoveryListener {
                override fun onDiscoveryStarted(regType: String) {}

                override fun onServiceFound(service: NsdServiceInfo) {
                    val resolveListener = object : NsdManager.ResolveListener {
                        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}

                        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                            val hostAddress = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                serviceInfo.hostAddresses.firstOrNull()?.hostAddress
                            } else {
                                @Suppress("DEPRECATION")
                                serviceInfo.host.hostAddress
                            } ?: ""

                            val bridge = HueBridge(
                                id = serviceInfo.attributes["bridgeid"]?.decodeToString() ?: "",
                                name = serviceInfo.serviceName,
                                ipAddress = hostAddress,
                            )
                            bridges.add(bridge)
                            trySend(bridges.toList())
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        nsdManager.resolveService(service, { it.run() }, resolveListener)
                    } else {
                        @Suppress("DEPRECATION")
                        nsdManager.resolveService(service, resolveListener)
                    }
                }

                override fun onServiceLost(service: NsdServiceInfo) {}
                override fun onDiscoveryStopped(serviceType: String) {}
                override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                    close()
                }

                override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                    close()
                }
            }

            if (Build.VERSION.SDK_INT >= 37) { // Build.VERSION_CODES.BAKLAVA or similar
                val discoveryRequest = DiscoveryRequest.Builder(SERVICE_TYPE)
                    .setFlags(DiscoveryRequest.FLAG_SHOW_PICKER)
                    .build()
                nsdManager.discoverServices(discoveryRequest, { it.run() }, discoveryListener)
            } else {
                nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
            }

            awaitClose {
                nsdManager.stopServiceDiscovery(discoveryListener)
            }
        }.first()
    } ?: emptyList()

    suspend fun pair(ip: String): PairResult = try {
        val response: List<HuePairingResponse> = client.post("http://$ip/api") {
            contentType(ContentType.Application.Json)
            setBody(HuePairingRequest(DEVICE_TYPE))
        }.body()

        val first = response.firstOrNull()
        when {
            first?.success != null -> {
                settingsRepository.setHueBridgeSettings(ip, first.success.username)
                PairResult.Success
            }
            first?.error?.type == ERROR_LINK_BUTTON_NOT_PRESSED -> PairResult.LinkButtonNotPressed
            else -> PairResult.Error(first?.error?.description ?: "Unknown error")
        }
    } catch (e: Exception) {
        PairResult.Error(e.message ?: "Network error")
    }

    suspend fun turnOffLights(ip: String, username: String) {
        // Turning off all lights in group 0 (all lights)
        turnOffGroup(ip, username, "0")
    }

    suspend fun fetchGroups(ip: String, username: String): List<HueGroup> = try {
        val response: Map<String, HueGroup> = client.get("http://$ip/api/$username/groups").body()
        response.map { it.value.copy(id = it.key) }
    } catch (_: Exception) {
        emptyList()
    }

    suspend fun turnOffGroup(ip: String, username: String, groupId: String) = try {
        client.put("http://$ip/api/$username/groups/$groupId/action") {
            contentType(ContentType.Application.Json)
            setBody(HueStateRequest(on = false))
        }
    } catch (_: Exception) {
        // Log or handle error if needed
    }

    fun getPairedIp() = settingsRepository.hueBridgeIp
    fun getPairedUser() = settingsRepository.hueApiUser
    fun getSelectedGroups() = settingsRepository.hueSelectedGroups
    suspend fun setSelectedGroups(groups: Set<String>) = settingsRepository.setHueSelectedGroups(groups)

    fun getStartGroups() = settingsRepository.hueStartGroups
    suspend fun setStartGroups(groups: Set<String>) = settingsRepository.setHueStartGroups(groups)
    fun getEndGroups() = settingsRepository.hueEndGroups
    suspend fun setEndGroups(groups: Set<String>) = settingsRepository.setHueEndGroups(groups)
}

sealed class PairResult {
    data object Success : PairResult()
    data object LinkButtonNotPressed : PairResult()
    data class Error(val message: String) : PairResult()
}
