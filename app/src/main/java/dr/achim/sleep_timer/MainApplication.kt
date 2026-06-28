package dr.achim.sleep_timer

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import dr.achim.sleep_timer.di.appModule
import dr.achim.sleep_timer.di.dataModule
import dr.achim.sleep_timer.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Purchases.apply {
            logLevel = if (BuildConfig.DEBUG) LogLevel.DEBUG else LogLevel.ERROR
            configure(
                PurchasesConfiguration.Builder(
                    this@MainApplication,
                    BuildConfig.REVENUECAT_KEY
                ).build()
            )
        }

        MobileAds.initialize(this) {}

        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.DEBUG else Level.NONE)
            androidContext(this@MainApplication)
            modules(
                dataModule,
                domainModule,
                appModule
            )
        }
    }
}
