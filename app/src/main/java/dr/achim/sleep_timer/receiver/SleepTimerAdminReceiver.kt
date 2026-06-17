package dr.achim.sleep_timer.receiver

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import dr.achim.sleep_timer.common.UiMessageManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SleepTimerAdminReceiver : DeviceAdminReceiver(), KoinComponent {

    private val uiMessageManager: UiMessageManager by inject()

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        uiMessageManager.emitMessage("Device Admin Enabled")
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        uiMessageManager.emitMessage("Device Admin Disabled")
    }
}
