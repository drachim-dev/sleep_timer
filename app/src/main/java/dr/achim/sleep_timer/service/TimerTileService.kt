package dr.achim.sleep_timer.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import dr.achim.sleep_timer.MainActivity
import dr.achim.sleep_timer.R
import dr.achim.sleep_timer.data.BillingRepository
import dr.achim.sleep_timer.data.QuickTimesRepository
import dr.achim.sleep_timer.domain.repository.TimerRepository
import dr.achim.sleep_timer.model.TimerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class TimerTileService : TileService() {

    private val timerRepository: TimerRepository by inject()
    private val quickTimesRepository: QuickTimesRepository by inject()
    private val billingRepository: BillingRepository by inject()

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var timerStateJob: Job? = null

    override fun onStartListening() {
        super.onStartListening()
        if (!FeatureFlag.QuickSettingsTile.enabled) return

        timerStateJob?.cancel()
        timerStateJob = serviceScope.launch {
            timerRepository.timerState.collect { state ->
                updateTile(state)
            }
        }
    }

    override fun onStopListening() {
        super.onStopListening()
        timerStateJob?.cancel()
    }

    override fun onClick() {
        super.onClick()
        if (!FeatureFlag.QuickSettingsTile.enabled) return

        serviceScope.launch {
            if (!billingRepository.awaitIsPro()) {
                val intent = Intent(this@TimerTileService, MainActivity::class.java).apply {
                    action = MainActivity.ACTION_UPGRADE_PRO
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val pendingIntent = PendingIntent.getActivity(
                    this@TimerTileService,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    startActivityAndCollapse(pendingIntent)
                } else {
                    @SuppressLint("StartActivityAndCollapseDeprecated")
                    @Suppress("Deprecation")
                    startActivityAndCollapse(intent)
                }
                return@launch
            }

            val state = timerRepository.timerState.value
            if (state is TimerState.Running || state is TimerState.Paused) {
                val intent = Intent(this@TimerTileService, TimerService::class.java).apply {
                    action = TimerService.ACTION_STOP
                }
                startService(intent)
            } else {
                val minutes = quickTimesRepository.lastSelectedMinutes.first()
                val intent = Intent(this@TimerTileService, TimerService::class.java).apply {
                    action = TimerService.ACTION_START
                    putExtra(TimerService.EXTRA_DURATION_MILLIS, minutes * 60 * 1000L)
                }
                startService(intent)
            }
        }
    }

    private fun updateTile(state: TimerState) {
        val tile = qsTile ?: return
        when (state) {
            is TimerState.Running, is TimerState.Paused -> {
                tile.state = Tile.STATE_ACTIVE
                tile.label = state.formattedTime
                tile.subtitle = getString(R.string.tile_label)
            }

            is TimerState.Idle -> {
                tile.state = Tile.STATE_INACTIVE
                tile.label = getString(R.string.tile_label)
                tile.subtitle = null
            }
        }
        tile.updateTile()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
