package dr.achim.sleep_timer

import android.app.Application
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import dr.achim.sleep_timer.di.appModule
import dr.achim.sleep_timer.di.dataModule
import dr.achim.sleep_timer.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Purchases.configure(
            PurchasesConfiguration.Builder(this, BuildConfig.REVENUECAT_KEY).build()
        )

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(
                dataModule,
                domainModule,
                appModule
            )
        }
    }
}
