package dr.achim.sleep_timer.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "quick_times_prefs")

class QuickTimesRepository(private val context: Context) {

    companion object {
        private val QUICK_TIMES_KEY = stringPreferencesKey("quick_times")
        private val LAST_SELECTED_MINUTES_KEY = intPreferencesKey("last_selected_minutes")
        private val DEFAULT_QUICK_TIMES = listOf(15, 30, 45, 60, -1) // -1 as placeholder
        private const val DEFAULT_SELECTED_MINUTES = 50
    }

    val quickTimes: Flow<List<Int>> = context.dataStore.data.map { preferences ->
        val quickTimesString = preferences[QUICK_TIMES_KEY]
        quickTimesString?.split(",")?.map { it.toInt() } ?: DEFAULT_QUICK_TIMES
    }

    val lastSelectedMinutes: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[LAST_SELECTED_MINUTES_KEY] ?: DEFAULT_SELECTED_MINUTES
    }

    suspend fun updateQuickTime(index: Int, minutes: Int) {
        context.dataStore.edit { preferences ->
            val currentList =
                preferences[QUICK_TIMES_KEY]?.split(",")?.map { it.toInt() }?.toMutableList()
                    ?: DEFAULT_QUICK_TIMES.toMutableList()

            if (index in currentList.indices) {
                currentList[index] = minutes
                preferences[QUICK_TIMES_KEY] = currentList.joinToString(",")
            }
        }
    }

    suspend fun updateLastSelectedMinutes(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[LAST_SELECTED_MINUTES_KEY] = minutes
        }
    }
}
