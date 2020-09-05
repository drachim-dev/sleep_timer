package dr.achim.sleep_timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor.DartEntrypoint
import io.flutter.embedding.engine.dart.DartExecutor.DartEntrypoint.createDefault
import io.flutter.plugin.common.MethodChannel
import io.flutter.view.FlutterMain

const val NOTIF_ACTION_OPEN = "NOTIF_ACTION_OPEN";
const val NOTIF_ACTION_CANCEL = "NOTIF_ACTION_CANCEL";
const val NOTIF_ACTION_PAUSE = "NOTIF_ACTION_PAUSE"
const val NOTIF_ACTION_CONTINUE = "NOTIF_ACTION_CONTINUE"
const val NOTIF_ACTION_EXTEND = "NOTIF_ACTION_EXTEND"

class NotificationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val engine = FlutterEngine(context)
        val entrypoint: DartEntrypoint = createDefault()
        engine.dartExecutor.executeDartEntrypoint(entrypoint)
        val channel = MethodChannel(engine.dartExecutor.binaryMessenger, CHANNEL_NAME)

        val id = intent.getIntExtra("id", -1)
        val arguments = mapOf("id" to id)

        when (intent.action) {
            NOTIF_ACTION_OPEN -> {
                channel.invokeMethod("NOTIF_ACTION_OPEN", arguments)
            }
            NOTIF_ACTION_CANCEL -> {
                NotificationManagerCompat.from(context).cancel(id)
                channel.invokeMethod("cancelTimer", arguments)
            }
            NOTIF_ACTION_PAUSE -> {
                channel.invokeMethod("pauseNotification", arguments)
            }
            NOTIF_ACTION_CONTINUE -> {
            }
            NOTIF_ACTION_EXTEND -> {
            }
        }
    }

}