package com.octagon_technologies.covid19_statistics_app

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException

class SettingsDataStore(context: Context) {
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val type = Types.newParameterizedType(List::class.java, String::class.java)
    private val jsonAdapter: JsonAdapter<List<String>> = moshi.adapter(type)

    private val dataStore = context.createDataStore("settings")
    private val locationDataStore = context.createDataStore("location_dataStore")

    private val locationKey = preferencesKey<String>("actual_location")
    private val themeKey = preferencesKey<String>("theme")
    private val favouriteLocationKey = preferencesKey<String>("favourite_location_key")
    private val recentLocationKey = preferencesKey<String>("recent_location_key")

    fun editTheme(theme: Theme) {
        CoroutineScope(Dispatchers.Main).launch {
            dataStore.edit {
                it[themeKey] = theme.name
                Timber.d("preferences[themeKey] is ${it[themeKey]}")
                Timber.d("In editTheme(), favouriteLocationKey is ${it[favouriteLocationKey]} and recentLocationKey is ${it[recentLocationKey]}")
            }
        }
    }

    fun getTheme(): Flow<Theme> {
        return dataStore.data
            .map {
                val theme = Theme.valueOf(it[themeKey] ?: "LIGHT")
                Timber.d("theme is $theme")
                Timber.d("In getTheme(), favouriteLocationKey is ${it[favouriteLocationKey]} and recentLocationKey is ${it[recentLocationKey]}")
                return@map theme
            }
    }

    fun addHistoryLocation(newLocation: String, isRecent: Boolean) {
        CoroutineScope(Dispatchers.Main).launch {
            val formerList = getHistoryLocations(isRecent).first()
            formerList.add(newLocation)
            Timber.d("adding formerList in isRecent as $isRecent is $formerList")
            locationDataStore.edit {
                    it[if (isRecent) recentLocationKey else favouriteLocationKey] =
                    jsonAdapter.toJson(formerList.toList())

                Timber.d("End of ${if (isRecent) "recent" else "favourite"} history addition with data as ${it[if (isRecent) recentLocationKey else favouriteLocationKey]}")
            }
        }
    }

    fun removeHistoryLocation(newLocation: String, isRecent: Boolean) {
        CoroutineScope(Dispatchers.Main).launch {
            val formerList = getHistoryLocations(isRecent).first()
            Timber.d("remove formerList is $formerList")

            if (formerList.remove(newLocation)) {
                locationDataStore.edit {
                    it[if (isRecent) recentLocationKey else favouriteLocationKey] =
                        jsonAdapter.toJson(formerList.toList())
                }
            }
        }
    }

    fun clearHistoryLocation(isRecent: Boolean) {
        CoroutineScope(Dispatchers.Main).launch {
            locationDataStore.edit {
                Timber.d("clearHistoryLocation called with isRecent as $isRecent")
                it[if (isRecent) recentLocationKey else favouriteLocationKey] = jsonAdapter.toJson(
                    listOf()
                )
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun getHistoryLocations(isRecent: Boolean = false): Flow<ArrayList<String>> {
        return withContext(Dispatchers.IO) {
            locationDataStore.data.map {
                Timber.d("History location in $isRecent is ${it[if (isRecent) recentLocationKey else favouriteLocationKey]}")
                jsonAdapter.fromJson(
                    it[if (isRecent) recentLocationKey else favouriteLocationKey]
                        ?: return@map ArrayList()
                ) as ArrayList<String>? ?: ArrayList()
            }
        }
    }

    fun editLocation(newValue: String) {
        CoroutineScope(Dispatchers.Main).launch {
            dataStore.edit { preferences ->
                preferences[locationKey] = newValue
                Timber.d("preferences[locationKey] is ${preferences[locationKey]}")
            }
        }
    }

    fun getLocation(): Flow<String> {
        return dataStore.data
            .catch {
                if (it is IOException) {
                    Timber.e(it)
                    emit(emptyPreferences())
                } else {
                    Timber.e(it)
                    throw it
                }
            }
            .map {
                val location = it[locationKey] ?: "Australia"
                Timber.d("location is $location")
                return@map location
            }
    }
}