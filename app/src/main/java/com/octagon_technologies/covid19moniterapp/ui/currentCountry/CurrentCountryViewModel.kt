package com.octagon_technologies.covid19moniterapp.ui.currentCountry

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.octagon_technologies.covid19moniterapp.Status
import com.octagon_technologies.covid19moniterapp.database.MainDataBase
import com.octagon_technologies.covid19moniterapp.network.allCountries.EachCountry
import com.octagon_technologies.covid19moniterapp.network.currentCountry.CurrentCountry
import com.octagon_technologies.covid19moniterapp.shared_code.MainCountryCodeItem
import com.octagon_technologies.covid19moniterapp.shared_code.MainCurrentCountryItem
import com.octagon_technologies.covid19moniterapp.shared_code.MainGlobalItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class CurrentCountryViewModel(context: Context) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val mainDataBase = MainDataBase.getInstance(context)
    private val mainFlagItem = MainCountryCodeItem(context)

    private var _liveCountryCode = MutableLiveData<String>()
    val liveCountryCode: LiveData<String>
        get() = _liveCountryCode

    private var _arrayOfAllCountries = MutableLiveData<ArrayList<EachCountry>>()
    val arrayOfAllCountries: LiveData<ArrayList<EachCountry>>
        get() = _arrayOfAllCountries

    private var _arrayOfCurrentCountry = MutableLiveData<ArrayList<CurrentCountry>>()
    val arrayOfCurrentCountry: LiveData<ArrayList<CurrentCountry>>
        get() = _arrayOfCurrentCountry

    private var _status = MutableLiveData(Status.LOADING)
    val status: LiveData<Status>
        get() = _status


    fun loadData(location: String?) {
        Timber.d("loadData called")
        _status.value = Status.LOADING
        getCurrentCountry(location)
        getAllCountries()
    }

    private fun getCurrentCountry(location: String?) {
        uiScope.launch {
            _arrayOfCurrentCountry.value =
                MainCurrentCountryItem.getCurrentCountryAsync(mainDataBase, location)
            _arrayOfCurrentCountry.value?.get(0)?.Country?.let { getFlag(it) }
            Timber.d("End of getCurrentCountry with country name is ${_arrayOfCurrentCountry.value?.get(0)?.Country}")
        }
    }

    private fun getAllCountries() {
        uiScope.launch {
            _arrayOfAllCountries.value = MainGlobalItem.getGlobalAsync(mainDataBase)?.Countries as ArrayList<EachCountry>?
        }
    }

    private fun getFlag(countryName: String) {
        uiScope.launch {
            _liveCountryCode.value = mainFlagItem.getCountryCode(countryName)
            _status.value = Status.DONE
        }
    }
}