package com.nickspatties.timeclock.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

data class UserPreferences(
    val countDownEnabled: Boolean,
    val countDownEndTime: Long,
    val countDownWarningEnabled: Boolean,
)

object PreferenceKeys {
    val COUNT_DOWN_END_TIME = longPreferencesKey("count_down_end_time")
    val COUNT_DOWN_ENABLED = booleanPreferencesKey("count_down_enabled")
    val COUNT_DOWN_WARNING_ENABLED = booleanPreferencesKey("count_down_warning_enabled")
}

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("UserPreferencesRepo", "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            mapUserPreferences(preferences)
        }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        val countDownEnabled = preferences[PreferenceKeys.COUNT_DOWN_ENABLED] ?: false
        val countDownEndTime = preferences[PreferenceKeys.COUNT_DOWN_END_TIME] ?: 0
        val countDownWarningEnabled = preferences[PreferenceKeys.COUNT_DOWN_WARNING_ENABLED] ?: true
        return UserPreferences(countDownEnabled, countDownEndTime, countDownWarningEnabled)
    }

    suspend fun updateCountDownEndTime(endTime: Long) {
        dataStore.edit {
            it[PreferenceKeys.COUNT_DOWN_END_TIME] = endTime
        }
    }

    suspend fun updateCountDownWarningEnabled(enabled: Boolean) {
        dataStore.edit {
            it[PreferenceKeys.COUNT_DOWN_WARNING_ENABLED] = enabled
        }
    }

    suspend fun <T> updatePreference(preference: Preferences.Key<T>, data: T) {
        dataStore.edit {
            it[preference] = data
        }
    }
}