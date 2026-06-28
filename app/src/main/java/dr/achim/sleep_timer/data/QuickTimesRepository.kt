package dr.achim.sleep_timer.data

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.quickTimesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "quick_times_prefs",
    produceMigrations = { context ->
        listOf(
            SharedPreferencesMigration(
                context = context,
                sharedPreferencesName = "FlutterSharedPreferences",
                keysToMigrate = setOf(
                    "flutter.pref_key_initial_time"
                )
            ),
            object : DataMigration<Preferences> {
                override suspend fun shouldMigrate(currentData: Preferences): Boolean {
                    return currentData.asMap().keys.any { it.name.startsWith("flutter.") }
                }

                override suspend fun migrate(currentData: Preferences): Preferences {
                    val mutablePreferences = currentData.toMutablePreferences()
                    currentData.asMap().forEach { (key, value) ->
                        if (key.name.startsWith("flutter.")) {
                            val cleanName = key.name.removePrefix("flutter.")
                            mutablePreferences.remove(key)
                            when (value) {
                                is Boolean -> mutablePreferences[booleanPreferencesKey(cleanName)] = value
                                is Float -> mutablePreferences[floatPreferencesKey(cleanName)] = value
                                is Int -> mutablePreferences[intPreferencesKey(cleanName)] = value
                                is Long -> {
                                    if (cleanName == "pref_key_initial_time") {
                                        mutablePreferences[intPreferencesKey(cleanName)] = value.toInt()
                                    } else {
                                        mutablePreferences[longPreferencesKey(cleanName)] = value
                                    }
                                }
                                is String -> mutablePreferences[stringPreferencesKey(cleanName)] = value
                                is Set<*> -> {
                                    @Suppress("UNCHECKED_CAST")
                                    mutablePreferences[stringSetPreferencesKey(cleanName)] = value as Set<String>
                                }
                            }
                        }
                    }
                    return mutablePreferences.toPreferences()
                }

                override suspend fun cleanUp() {}
            }
        )
    }
)

class QuickTimesRepository(context: Context) {

    private val dataStore: DataStore<Preferences> = context.quickTimesDataStore

    companion object {
        private val QUICK_TIMES_KEY = stringPreferencesKey("quick_times")
        private val LAST_SELECTED_MINUTES_KEY = intPreferencesKey("pref_key_initial_time")

        /**
         * Default values
         */
        private val DEFAULT_QUICK_TIMES = listOf(15, 30, 45, 60, -1) // -1 as placeholder
        private const val DEFAULT_SELECTED_MINUTES = 15
    }

    val quickTimes: Flow<List<Int>> = dataStore.data.map { preferences ->
        val quickTimesString = preferences[QUICK_TIMES_KEY]
        quickTimesString?.split(",")?.map { it.toInt() } ?: DEFAULT_QUICK_TIMES
    }

    val lastSelectedMinutes: Flow<Int> = dataStore.data.map { preferences ->
        val value = preferences.asMap()[LAST_SELECTED_MINUTES_KEY]
        when (value) {
            is Int -> value
            is Long -> value.toInt()
            else -> DEFAULT_SELECTED_MINUTES
        }
    }

    suspend fun updateQuickTime(index: Int, minutes: Int) {
        dataStore.edit { preferences ->
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
        dataStore.edit { preferences ->
            preferences[LAST_SELECTED_MINUTES_KEY] = minutes
        }
    }
}
