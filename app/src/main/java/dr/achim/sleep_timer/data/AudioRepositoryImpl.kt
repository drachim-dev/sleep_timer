package dr.achim.sleep_timer.data

import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import dr.achim.sleep_timer.domain.repository.AudioRepository

class AudioRepositoryImpl(private val audioManager: AudioManager) : AudioRepository {

    override fun setMediaVolume(level: Int, flags: Int) {
        try {
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val targetVolume = (maxVolume * level / 100f).toInt()
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, flags)
        } catch (_: SecurityException) {
            // Permission not granted or system restriction
        }
    }

    override fun stopMedia() {
        val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener { }
            .build()
        audioManager.requestAudioFocus(focusRequest)
        audioManager.abandonAudioFocusRequest(focusRequest)
    }
}
