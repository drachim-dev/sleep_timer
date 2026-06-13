package dr.achim.sleep_timer

import android.app.Application
import dr.achim.sleep_timer.di.appModule
import dr.achim.sleep_timer.di.dataModule
import dr.achim.sleep_timer.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(
                dataModule,
                domainModule,
                appModule
            )
        }
    }
}
