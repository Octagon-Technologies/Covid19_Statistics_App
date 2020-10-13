package com.example.covid19moniterapp

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException

class MyDataStore(context: Context) {
    private val dataStore = context.createDataStore("location_pref")
    private val locationKey = preferencesKey<String>("actual-location")

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
                }
                else {
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