package dr.achim.sleep_timer.di

import dr.achim.sleep_timer.domain.usecase.CheckTimerPermissionsUseCase
import dr.achim.sleep_timer.domain.usecase.ControlTimerUseCase
import dr.achim.sleep_timer.domain.usecase.GetLastSelectedMinutesUseCase
import dr.achim.sleep_timer.domain.usecase.GetQuickTimesUseCase
import dr.achim.sleep_timer.domain.usecase.GetSettingsUseCase
import dr.achim.sleep_timer.domain.usecase.GetTimerStatusUseCase
import dr.achim.sleep_timer.domain.usecase.ManageQuickLaunchUseCase
import dr.achim.sleep_timer.domain.usecase.ManageTimerActionsUseCase
import dr.achim.sleep_timer.domain.usecase.UpdateLastSelectedMinutesUseCase
import dr.achim.sleep_timer.domain.usecase.UpdateQuickTimeUseCase
import dr.achim.sleep_timer.domain.usecase.UpdateSettingsUseCase
import org.koin.dsl.module
import org.koin.plugin.module.dsl.factory

val domainModule = module {
    factory<GetQuickTimesUseCase>()
    factory<UpdateQuickTimeUseCase>()
    factory<GetLastSelectedMinutesUseCase>()
    factory<UpdateLastSelectedMinutesUseCase>()
    factory<GetSettingsUseCase>()
    factory<UpdateSettingsUseCase>()

    factory<GetTimerStatusUseCase>()
    factory<ControlTimerUseCase>()
    factory<ManageTimerActionsUseCase>()
    factory<ManageQuickLaunchUseCase>()
    factory<CheckTimerPermissionsUseCase>()
}
