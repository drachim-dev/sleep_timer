package dr.achim.sleep_timer

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import dr.achim.sleep_timer.di.appModule
import dr.achim.sleep_timer.di.dataModule
import dr.achim.sleep_timer.di.domainModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@MainApplication) {}
        }

        Purchases.apply {
            logLevel = if (BuildConfig.DEBUG) LogLevel.DEBUG else LogLevel.ERROR
            configure(
                PurchasesConfiguration.Builder(
                    this@MainApplication,
                    BuildConfig.REVENUECAT_KEY
                ).build()
            )
        }

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
