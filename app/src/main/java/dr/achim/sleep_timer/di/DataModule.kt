package dr.achim.sleep_timer.di

import android.app.NotificationManager
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.media.AudioManager
import dr.achim.sleep_timer.data.QuickLaunchRepositoryImpl
import dr.achim.sleep_timer.data.QuickTimesRepository
import dr.achim.sleep_timer.data.SettingsRepository
import dr.achim.sleep_timer.data.TimerActionExecutor
import dr.achim.sleep_timer.data.TimerController
import dr.achim.sleep_timer.data.TimerRepositoryImpl
import dr.achim.sleep_timer.domain.repository.QuickLaunchRepository
import dr.achim.sleep_timer.domain.repository.TimerRepository
import dr.achim.sleep_timer.util.UiMessageManager
import org.koin.dsl.module
import org.koin.plugin.module.dsl.bind
import org.koin.plugin.module.dsl.create
import org.koin.plugin.module.dsl.single

private fun provideNotificationManager(context: Context) =
    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

private fun provideDevicePolicyManager(context: Context) =
    context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

private fun provideAudioManager(context: Context) =
    context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

val dataModule = module {
    single<NotificationManager> { create(::provideNotificationManager) }
    single<DevicePolicyManager> { create(::provideDevicePolicyManager) }
    single<AudioManager> { create(::provideAudioManager) }

    single { QuickLaunchRepositoryImpl(get()) }.bind(QuickLaunchRepository::class)
    single<QuickTimesRepository>()
    single<SettingsRepository>()
    single { TimerRepositoryImpl() }.bind(TimerRepository::class)
    single<TimerActionExecutor>()
    single<TimerController>()
    single<UiMessageManager>()
}
