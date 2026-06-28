package dr.achim.sleep_timer.data

import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.util.Log
import dr.achim.sleep_timer.common.TAG
import dr.achim.sleep_timer.domain.repository.AudioRepository

class AudioRepositoryImpl(private val audioManager: AudioManager) : AudioRepository {

    override fun setMediaVolume(level: Int, flags: Int) {
        try {
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val targetVolume = (maxVolume * level / 100f).toInt()
            Log.d(TAG, "Setting volume to $targetVolume ($level%)")
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, flags)
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to set volume: ${e.message}")
        }
    }

    override fun stopMedia() {
        val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener { }
            .build()
        
        val result = audioManager.requestAudioFocus(focusRequest)
        if (result == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
            Log.w(TAG, "Audio focus request failed (possibly suppressed by Android 17+ background hardening)")
        }
        
        audioManager.abandonAudioFocusRequest(focusRequest)
    }
}
