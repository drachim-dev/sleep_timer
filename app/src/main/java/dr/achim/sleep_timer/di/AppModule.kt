package dr.achim.sleep_timer.di

import dr.achim.sleep_timer.presentation.home.HomeViewModel
import dr.achim.sleep_timer.presentation.settings.SettingsViewModel
import dr.achim.sleep_timer.presentation.hue.HueDiscoveryViewModel
import dr.achim.sleep_timer.presentation.hue.RoomSelectionViewModel
import dr.achim.sleep_timer.presentation.timer.TimerViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val appModule = module {
    viewModel<HomeViewModel>()
    viewModel<TimerViewModel>()
    viewModel<SettingsViewModel>()
    viewModel<HueDiscoveryViewModel>()
    viewModel<RoomSelectionViewModel>()
}
