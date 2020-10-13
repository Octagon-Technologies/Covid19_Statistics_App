package com.example.covid19moniterapp.repo

import com.example.covid19moniterapp.cityName
import com.example.covid19moniterapp.database.AllCountriesDatabaseClass
import com.example.covid19moniterapp.database.CurrentDatabaseClass
import com.example.covid19moniterapp.database.DataBase
import com.example.covid19moniterapp.network.AllCountriesObject
import com.example.covid19moniterapp.network.CurrentCountryObject
import com.example.covid19moniterapp.network.allCountries.MainGlobalData
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class Repo(private val database: DataBase){

    var currentCountryData: CurrentDatabaseClass? = null
    var allCountriesData: AllCountriesDatabaseClass? = null

    var mainGlobalDataDeferred: Deferred<MainGlobalData>? = null
    var currentCountyDeferred: Deferred<List<com.example.covid19moniterapp.network.currentCountry.CurrentCountryItem>>? = null

    fun searchData() {
        mainGlobalDataDeferred = AllCountriesObject.futureRetrofitService.getAllCountriesAsync()
         currentCountyDeferred = CurrentCountryObject.currentRetrofitService.getCurrentWeatherAsync(cityName)
    }

    suspend fun refreshData() {
        withContext(Dispatchers.IO) {
            cityName = database.databaseDao.getFormerString()?.name_text.toString()

            Timber.i("cityName is $cityName")


            try {
                val currentCountry = currentCountyDeferred!!.await()
                val currentDatabaseClass = CurrentDatabaseClass(current_country = currentCountry)
                Timber.i("Online Country is ${currentCountry[0].Country}")

                val allCountries = mainGlobalDataDeferred!!.await()
                val allCountriesDatabaseClass =
                    AllCountriesDatabaseClass(mainGlobalData = allCountries)

                database.currentDao.insertCurrentDataClass(currentDatabaseClass)
                database.allCountriesDao.insertAllCountriesClass(allCountriesDatabaseClass)

                currentCountryData = database.currentDao.getCurrentDataClass()
                Timber.i("Offline Country is ${currentCountryData?.current_country?.get(0)?.Country}")
                allCountriesData = database.allCountriesDao.getAllCountriesClass()
            } catch (t: Throwable) {
                Timber.e(t)
            }
            finally{
                currentCountryData = database.currentDao.getCurrentDataClass()
                Timber.i("Offline Country is ${currentCountryData?.current_country?.get(0)?.Country}")
                allCountriesData = database.allCountriesDao.getAllCountriesClass()
            }
        }
        Timber.i("Offline Country is ${currentCountryData?.current_country?.get(0)?.Country}")
    }

}