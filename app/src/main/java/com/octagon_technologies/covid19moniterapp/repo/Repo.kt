package com.octagon_technologies.covid19moniterapp.repo

import android.content.Context
import com.octagon_technologies.covid19moniterapp.SettingsDataStore
import com.octagon_technologies.covid19moniterapp.database.CurrentDatabaseClass
import com.octagon_technologies.covid19moniterapp.database.GlobalDatabaseClass
import com.octagon_technologies.covid19moniterapp.database.MainDataBase
import com.octagon_technologies.covid19moniterapp.network.CurrentCountryObject
import com.octagon_technologies.covid19moniterapp.network.GlobalRetrofitObject
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