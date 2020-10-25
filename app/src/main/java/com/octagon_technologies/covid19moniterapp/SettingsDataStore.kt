package com.octagon_technologies.covid19moniterapp

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
    private val locationKey = preferencesKey<String>("actual_location")
    private val themeKey = preferencesKey<String>("theme")
    private val favouriteLocationKey = preferencesKey<String>("favourite_location_key")

    fun editTheme(theme: Theme) {
        CoroutineScope(Dispatchers.Main).launch {
            dataStore.edit { preferences ->
                preferences[themeKey] = theme.name
                Timber.d("preferences[themeKey] is ${preferences[themeKey]}")
            }
        }
    }

    fun getTheme(): Flow<Theme> {
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
                val theme = Theme.valueOf(it[themeKey] ?: "LIGHT")
                Timber.d("theme is $theme")
                return@map theme
            }
    }

    fun addFavouriteLocation(newLocation: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val formerList = getFavouriteLocation().first()
            formerList.add(newLocation)
            Timber.d("formerList is $formerList")
            dataStore.edit {
                it[favouriteLocationKey] = jsonAdapter.toJson(formerList.toList())
            }
        }
    }

    fun removeFavouriteLocation(newLocation: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val formerList = getFavouriteLocation().first()
            if (formerList.remove(newLocation)) {
                dataStore.edit {
                    it[favouriteLocationKey] = jsonAdapter.toJson(formerList.toList())
                }
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun getFavouriteLocation(): Flow<ArrayList<String>> {
        return withContext(Dispatchers.IO) {
            dataStore.data.map {
                jsonAdapter.fromJson(
                    it[favouriteLocationKey] ?: return@map ArrayList()
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
                val location = it[locationKey] ?: "Kenya"
                Timber.d("location is $location")
                return@map location
            }
    }
}