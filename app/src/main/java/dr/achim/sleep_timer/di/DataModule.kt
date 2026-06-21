package dr.achim.sleep_timer.di

import android.app.NotificationManager
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.hardware.SensorManager
import android.media.AudioManager
import android.os.Vibrator
import android.os.VibratorManager
import android.os.Build
import android.net.nsd.NsdManager
import dr.achim.sleep_timer.common.UiMessageManager
import dr.achim.sleep_timer.data.AdManager
import dr.achim.sleep_timer.data.AudioRepositoryImpl
import dr.achim.sleep_timer.data.BillingRepository
import dr.achim.sleep_timer.data.HueRepository
import dr.achim.sleep_timer.data.QuickLaunchRepositoryImpl
import dr.achim.sleep_timer.data.QuickTimesRepository
import dr.achim.sleep_timer.data.SettingsRepository
import dr.achim.sleep_timer.data.TimerActionExecutor
import dr.achim.sleep_timer.data.TimerController
import dr.achim.sleep_timer.data.TimerRepositoryImpl
import dr.achim.sleep_timer.domain.repository.AudioRepository
import dr.achim.sleep_timer.domain.repository.QuickLaunchRepository
import dr.achim.sleep_timer.domain.repository.TimerRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
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

private fun provideSensorManager(context: Context) =
    context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

private fun provideVibrator(context: Context): Vibrator {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
}

private fun provideNsdManager(context: Context) =
    context.getSystemService(Context.NSD_SERVICE) as NsdManager

private fun provideHttpClient() = HttpClient(OkHttp) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        })
    }
}

val dataModule = module {
    single<NotificationManager> { create(::provideNotificationManager) }
    single<DevicePolicyManager> { create(::provideDevicePolicyManager) }
    single<AudioManager> { create(::provideAudioManager) }
    single<SensorManager> { create(::provideSensorManager) }
    single<Vibrator> { create(::provideVibrator) }
    single<NsdManager> { create(::provideNsdManager) }
    single<HttpClient> { create(::provideHttpClient) }

    single { QuickLaunchRepositoryImpl(get()) }.bind(QuickLaunchRepository::class)
    single<QuickTimesRepository>()
    single<SettingsRepository>()
    single<HueRepository>()
    single { AudioRepositoryImpl(get()) }.bind(AudioRepository::class)
    single { TimerRepositoryImpl() }.bind(TimerRepository::class)
    single<TimerActionExecutor>()
    single<TimerController>()
    single<UiMessageManager>()
    single<AdManager>()
    single<BillingRepository>()
}
