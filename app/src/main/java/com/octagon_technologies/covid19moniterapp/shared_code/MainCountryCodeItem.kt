package com.octagon_technologies.covid19moniterapp.shared_code

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import com.octagon_technologies.covid19moniterapp.network.CountryCodeObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.net.UnknownHostException

class MainCountryCodeItem(context: Context) {
    private val dataStore = context.createDataStore("country_code_data_store")
    private val countryCodeKey = preferencesKey<String>("country_code_key")

    suspend fun getCountryCode(countryName: String): String? {
        return withContext(Dispatchers.Default) {
            try {
                val remoteCountryCode =
                    CountryCodeObject.countryCodeRetrofitService.getCountryCodeAsync(countryName = countryName)
                        .await()[0].countryCodeName
                Timber.d("remoteCountryCode is $remoteCountryCode")

                if (remoteCountryCode != null) {
                    editCountryCode(remoteCountryCode)
                }

                remoteCountryCode
            }   catch (uhe: UnknownHostException) {
                Timber.e(uhe, "No Network")
                getLocalCountryCodeAsync()
            }catch (http: HttpException) {
                getLocalCountryCodeAsync()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    private fun editCountryCode(flagUrl: String) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                dataStore.edit {
                    it[countryCodeKey] = flagUrl
                }
            }
        }
    }

    private suspend fun getLocalCountryCodeAsync(): String? {
        return withContext(Dispatchers.IO) {
            dataStore.data
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
                    it[countryCodeKey]
                }
                .first()
        }
    }


}