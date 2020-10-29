package com.octagon_technologies.covid19_statistics_app.repo

import android.content.Context
import com.octagon_technologies.covid19_statistics_app.SettingsDataStore
import com.octagon_technologies.covid19_statistics_app.database.CurrentDatabaseClass
import com.octagon_technologies.covid19_statistics_app.database.GlobalDatabaseClass
import com.octagon_technologies.covid19_statistics_app.database.MainDataBase
import com.octagon_technologies.covid19_statistics_app.network.CurrentCountryObject
import com.octagon_technologies.covid19_statistics_app.network.GlobalRetrofitObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber


class Repo(context: Context) {
    private val mainDataBase = MainDataBase.getInstance(context)
    private val settingsDataStore = SettingsDataStore(context)
    private val countryName = settingsDataStore.getLocation()

    suspend fun getRemoteData() {
        withContext(Dispatchers.IO) {
            Timber.d("countryName is ${countryName.first()}")

            val remoteCurrentCountry =
                CurrentCountryObject.currentRetrofitService.getCurrentCountryAsync(countryName.first()).await()
            val remoteGlobal =
                GlobalRetrofitObject.globalRetrofitService.getGlobalAsync().await()

            val currentDatabaseClass = CurrentDatabaseClass(listOfCurrentCountry = remoteCurrentCountry)
            val globalDatabaseClass = GlobalDatabaseClass(global = remoteGlobal)

            mainDataBase?.currentDao?.insertCurrentDataClass(currentDatabaseClass)
            mainDataBase?.globalDao?.insertGlobalClass(globalDatabaseClass)
        }
    }

}