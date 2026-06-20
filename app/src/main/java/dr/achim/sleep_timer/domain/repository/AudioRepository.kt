package dr.achim.sleep_timer.domain.repository

interface AudioRepository {
    /**
     * Sets the media volume level.
     * @param level Volume level in percent (0-100).
     * @param flags Optional flags for AudioManager.
     */
    fun setMediaVolume(level: Int, flags: Int = 0)

    /**
     * Requests and immediately abandons audio focus to stop media playback.
     */
    fun stopMedia()
}
