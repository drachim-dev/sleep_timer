package dr.achim.sleep_timer.data

import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.SystemClock
import android.util.Log
import android.view.KeyEvent
import dr.achim.sleep_timer.common.TAG
import dr.achim.sleep_timer.domain.repository.AudioRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.milliseconds

class AudioRepositoryImpl(private val audioManager: AudioManager) : AudioRepository {

companion object {
    private val PAUSE_VERIFY_TIMEOUT = 1_500.milliseconds
    private val AUDIO_FOCUS_SETTLE_DELAY = 1_500.milliseconds
}

    override fun setRelativeMediaVolume(level: Int, flags: Int) {
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val targetVolume = (maxVolume * level / 100f).toInt()
        Log.d(TAG, "Setting media volume to $targetVolume ($level%)")
        setAbsoluteMediaVolume(targetVolume, flags)
    }

    override suspend fun stopMedia(fadeDurationMillis: Long) {
        val originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        var focusRequest: AudioFocusRequest? = null

        try {
            fadeOut(originalVolume, fadeDurationMillis)

            focusRequest = buildAlarmFocusRequest().also(::requestFocus)
            if (!pauseUntilSilent()) {
                Log.w(TAG, "Media still active after all pause attempts")
            }
            delay(AUDIO_FOCUS_SETTLE_DELAY)
        } finally {
            restoreVolume(originalVolume)
            abandonFocus(focusRequest)
        }
    }

    /** Sends pause (then stop) and waits for the music stream to go idle, retrying a few times. */
    private suspend fun pauseUntilSilent(): Boolean {
        val keys = listOf(
            KeyEvent.KEYCODE_MEDIA_PAUSE,
            KeyEvent.KEYCODE_MEDIA_PAUSE,
            KeyEvent.KEYCODE_MEDIA_STOP, // last resort for players that ignore PAUSE
        )
        for (key in keys) {
            dispatchMediaKey(key)
            val silent = withTimeoutOrNull(PAUSE_VERIFY_TIMEOUT) {
                while (audioManager.isMusicActive) delay(100.milliseconds)
                true
            }
            if (silent == true) return true
        }
        return false
    }

    private fun dispatchMediaKey(keyCode: Int) {
        val now = SystemClock.uptimeMillis()
        try {
            audioManager.dispatchMediaKeyEvent(KeyEvent(now, now, KeyEvent.ACTION_DOWN, keyCode, 0))
            audioManager.dispatchMediaKeyEvent(KeyEvent(now, now, KeyEvent.ACTION_UP, keyCode, 0))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to dispatch media key $keyCode", e)
        }
    }

    /**
     * Steps the music volume down to 0 evenly over [durationMillis].
     * With a 1s minimum and a typical max volume of 15-30, each step is
     * at least ~30 ms, so the integer division can't collapse to zero.
     */
    private suspend fun fadeOut(originalVolume: Int, durationMillis: Long) {
        if (originalVolume <= 0) return // already silent, nothing to fade

        val stepDelay = durationMillis / originalVolume
        for (volume in (originalVolume - 1) downTo 0) {
            delay(stepDelay.milliseconds)
            setAbsoluteMediaVolume(volume)
        }
    }

    private fun setAbsoluteMediaVolume(volume: Int, flags: Int = 0) {
        try {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, flags)
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to set media volume to $volume", e)
        }
    }

    private fun restoreVolume(originalVolume: Int) {
        if (originalVolume <= 0) return
        Log.d(TAG, "Restoring media volume to $originalVolume")
        setAbsoluteMediaVolume(originalVolume)
    }

    private fun buildAlarmFocusRequest(): AudioFocusRequest =
        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()

    private fun requestFocus(request: AudioFocusRequest): Boolean =
        when (val result = audioManager.requestAudioFocus(request)) {
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> true
            else -> {
                Log.w(TAG, "Audio focus not granted (result=$result)")
                false
            }
        }

    private fun abandonFocus(request: AudioFocusRequest?) {
        request ?: return
        try {
            audioManager.abandonAudioFocusRequest(request)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to abandon audio focus", e)
        }
    }
}
