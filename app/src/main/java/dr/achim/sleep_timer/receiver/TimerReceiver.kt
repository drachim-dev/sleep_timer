package dr.achim.sleep_timer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import dr.achim.sleep_timer.service.TimerService

class TimerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_TIMER_EXPIRED) {
            val serviceIntent = Intent(context, TimerService::class.java).apply {
                action = TimerService.ACTION_FINISH
            }
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }

    companion object {
        const val ACTION_TIMER_EXPIRED = "dr.achim.sleep_timer.ACTION_TIMER_EXPIRED"
    }
}
