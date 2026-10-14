package dr.achim.sleep_timer.data

import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.SystemClock
import android.util.Log
import android.view.KeyEvent
import dr.achim.sleep_timer.common.TAG
import dr.achim.sleep_timer.domain.repository.AudioRepository
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

class AudioRepositoryImpl(private val audioManager: AudioManager) : AudioRepository {

    companion object {
        private val PAUSE_VERIFY_TIMEOUT = 1_500.milliseconds
        private val AUDIO_FOCUS_SETTLE_DELAY = 1_500.milliseconds
        private val FOCUS_CHECK_DELAY = 200.milliseconds
    }

    private val stopMutex = Mutex()

    override fun setRelativeMediaVolume(level: Int, flags: Int) {
        if (audioManager.isVolumeFixed) return

        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val clampedLevel = level.coerceIn(0, 100)
        val targetVolume = (maxVolume * clampedLevel / 100f).roundToInt()
        Log.d(TAG, "Setting media volume to $targetVolume ($clampedLevel%)")
        setAbsoluteMediaVolume(targetVolume, flags)
    }

    override suspend fun stopMedia(fadeDurationMillis: Long) = stopMutex.withLock {
        val originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        var focusRequest: AudioFocusRequest? = null
        var stoppedSuccessfully = true

        try {
            fadeOut(originalVolume, fadeDurationMillis)

            focusRequest = buildMediaFocusRequest().also(::requestFocus)
            stoppedSuccessfully = pauseUntilSilent()
            if (!stoppedSuccessfully) {
                Log.w(TAG, "Media still active after all pause attempts")
            }
            delay(AUDIO_FOCUS_SETTLE_DELAY)
        } finally {
            withContext(NonCancellable) {
                abandonFocus(focusRequest)
                if (stoppedSuccessfully) {
                    restoreVolume(originalVolume)
                } else {
                    Log.w(TAG, "Skipping volume restore because media did not stop")
                }
            }
        }
    }

    /**
     * Checks if audio focus already stopped playback, otherwise sends pause (then stop)
     * and waits for the music stream to go idle.
     */
    private suspend fun pauseUntilSilent(): Boolean {
        delay(FOCUS_CHECK_DELAY)
        if (!audioManager.isMusicActive) return true

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
     */
    private suspend fun fadeOut(originalVolume: Int, durationMillis: Long) {
        if (originalVolume <= 0 || audioManager.isVolumeFixed) return

        if (durationMillis <= 0L) {
            setAbsoluteMediaVolume(0)
            return
        }

        val stepDelay = (durationMillis / originalVolume).coerceAtLeast(1L)
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
        if (originalVolume <= 0 || audioManager.isVolumeFixed) return
        Log.d(TAG, "Restoring media volume to $originalVolume")

        setAbsoluteMediaVolume(originalVolume)
    }

    private fun buildMediaFocusRequest(): AudioFocusRequest =
        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
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