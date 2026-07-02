package dr.achim.sleep_timer.model

import android.os.Build
import androidx.annotation.StringRes
import dr.achim.sleep_timer.R

enum class ThemeMode(@StringRes val displayName: Int, val minSdk: Int = 0) {
    SYSTEM(R.string.settings_theme_mode_system),
    LightDynamic(R.string.settings_theme_mode_dynamic_light, Build.VERSION_CODES.S),
    DarkDynamic(R.string.settings_theme_mode_dynamic_dark, Build.VERSION_CODES.S),
    LightOrange(R.string.settings_theme_mode_light_orange),
    DarkOrange(R.string.settings_theme_mode_dark_orange);

    companion object {
        val default: ThemeMode
            get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                DarkDynamic
            } else {
                DarkOrange
            }
    }
}
