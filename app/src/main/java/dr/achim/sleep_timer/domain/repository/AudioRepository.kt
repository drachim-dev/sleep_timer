package dr.achim.sleep_timer.domain.repository

interface AudioRepository {
    /**
     * Sets the media volume level.
     * @param level Volume level in percent (0-100).
     * @param flags Optional flags for AudioManager.
     */
    fun setRelativeMediaVolume(level: Int, flags: Int = 0)

    /**
     * Fades out media volume, stops media playback by requesting audio focus,
     * and restores the original volume.
     *
     * @param fadeDurationMillis Duration of the fade out in milliseconds.
     */
    suspend fun stopMedia(fadeDurationMillis: Long = DEFAULT_FADE_DURATION_MS)

    companion object {
        const val DEFAULT_FADE_DURATION_MS = 3_000L
    }
}
