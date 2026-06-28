package dr.achim.sleep_timer.domain.usecase

import dr.achim.sleep_timer.domain.repository.AudioRepository

class SetMediaVolumeUseCase(private val audioRepository: AudioRepository) {

    operator fun invoke(level: Int, flags: Int = 0) = audioRepository.setMediaVolume(level, flags)
}
