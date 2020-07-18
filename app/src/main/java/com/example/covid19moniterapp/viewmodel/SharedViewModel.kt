package com.example.covid19moniterapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.covid19moniterapp.Status
import com.example.covid19moniterapp.cityName
import com.example.covid19moniterapp.database.DataClass
import com.example.covid19moniterapp.database.DataBase
import com.example.covid19moniterapp.network.CurrentCountryItem
import com.example.covid19moniterapp.network.allCountries.AllCountries
import com.example.covid19moniterapp.network.currentCountry.CurrentCountry
import com.example.covid19moniterapp.repo.Repo
import kotlinx.coroutines.*
import retrofit2.HttpException
import timber.log.Timber
import java.net.UnknownHostException

class SharedViewModel(private val dataBase: DataBase) : ViewModel() {
    val repoInstance = Repo(dataBase)

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _allCountriesData = MutableLiveData<AllCountries>()
    val allCountriesData: LiveData<AllCountries>
        get() = _allCountriesData

    private var _currentCountryData = MutableLiveData<List<com.example.covid19moniterapp.network.currentCountry.CurrentCountryItem>>()
    val currentCountryData: LiveData<List<com.example.covid19moniterapp.network.currentCountry.CurrentCountryItem>>
        get() = _currentCountryData

    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status>
        get() = _status

    init {
        uiScope.launch {
            repoInstance.refreshData()
        }
        getData()
        setAllCountries()
        setCurrentCountry()
    }

    fun insertCityName(){
        uiScope.launch {
            withContext(Dispatchers.IO){
                val data = DataClass(name_text = cityName)
                dataBase.databaseDao.insertString(data)
            }
        }
    }

    private fun getData(){
        uiScope.launch {
            cityName = suspendGetData()
        }
    }

    private suspend fun suspendGetData(): String{
        return withContext(Dispatchers.IO){
            dataBase.databaseDao.getFormerString()?.name_text ?: "Hello"
        }
    }

    fun setAllCountries(){
        try {
            _status.value = Status.LOADING
            uiScope.launch {
                repoInstance.refreshData()
            }
            _allCountriesData.value = repoInstance.allCountriesData?.all_countries
            Timber.i("_allCountries country is ${_allCountriesData.value?.Countries?.get(0)?.Country}")
            _status.value = Status.DONE
        }
        catch (https: HttpException){
            _status.value = Status.LOCATION_ERROR
        }
        catch (netError: UnknownHostException){
            _status.value = Status.NETWORK_ERROR
        }
    }

    fun setCurrentCountry(){
        try{
            _status.value = Status.LOADING
            uiScope.launch {
                repoInstance.refreshData()
            }
            _currentCountryData.value = repoInstance.currentCountryData?.current_country

            Timber.i("_currentCountryData.value.date is ${_currentCountryData.value?.get(0)?.Date} and repoInstance.currentCountryData?.current_country is ${repoInstance.currentCountryData?.current_country?.get(0)?.Date}")

            _status.value = Status.DONE
        }
        catch (https: HttpException){
            _status.value = Status.LOCATION_ERROR
            Timber.e(https)
        }
        catch (netError: UnknownHostException){
            _status.value = Status.NETWORK_ERROR
            Timber.e(netError)
        }
    }
}