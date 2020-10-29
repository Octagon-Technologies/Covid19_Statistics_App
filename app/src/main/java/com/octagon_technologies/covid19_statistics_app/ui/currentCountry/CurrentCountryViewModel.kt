package com.octagon_technologies.covid19_statistics_app.ui.currentCountry

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.octagon_technologies.covid19_statistics_app.R
import com.octagon_technologies.covid19_statistics_app.Status
import com.octagon_technologies.covid19_statistics_app.database.MainDataBase
import com.octagon_technologies.covid19_statistics_app.network.allCountries.EachCountry
import com.octagon_technologies.covid19_statistics_app.network.currentCountry.CurrentCountry
import com.octagon_technologies.covid19_statistics_app.shared_code.MainCountryCodeItem
import com.octagon_technologies.covid19_statistics_app.shared_code.MainCurrentCountryItem
import com.octagon_technologies.covid19_statistics_app.shared_code.MainGlobalItem
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


    fun loadData(context: Context, location: String?) {
        uiScope.launch {
            Timber.d("loadData called")
            _status.value = Status.LOADING
            getAllCountries()
            getCurrentCountry(context, location)
        }
    }

    private suspend fun getCurrentCountry(context: Context, location: String?) {
        _arrayOfCurrentCountry.value =
            MainCurrentCountryItem.getCurrentCountryAsync(mainDataBase, location)
        if (_arrayOfCurrentCountry.value == null) {
            Timber.d("_arrayOfCurrentCountry.value is null because network and database is null.")
            _status.value = Status.NETWORK_ERROR
        }
        try {
            val countryName = _arrayOfCurrentCountry.value?.get(0)?.Country
            countryName?.let { getFlag(it) }

            Timber.d("End of getCurrentCountry with country name is $countryName")
        } catch (e: IndexOutOfBoundsException) {
            Timber.d("API does not contain that country's data")
            Toast.makeText(
                context,
                context.resources.getString(R.string.no_covid_data_available_format, location),
                Toast.LENGTH_SHORT
            ).show()

            _status.value = Status.NETWORK_ERROR
        }
    }

    private suspend fun getAllCountries() {
        _arrayOfAllCountries.value =
            MainGlobalItem.getGlobalAsync(mainDataBase)?.Countries as ArrayList<EachCountry>?
        Timber.d("End of getAllCountries")
    }

    private suspend fun getFlag(countryName: String) {
        _liveCountryCode.value =
            if (countryName == "India") "IN" else mainFlagItem.getCountryCode(countryName)
        _status.value = Status.DONE
    }
}