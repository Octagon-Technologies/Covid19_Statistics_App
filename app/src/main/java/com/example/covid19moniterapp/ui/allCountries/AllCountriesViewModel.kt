package com.example.covid19moniterapp.ui.allCountries

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.covid19moniterapp.Status
import com.example.covid19moniterapp.database.AllCountriesDatabaseClass
import com.example.covid19moniterapp.database.DataBase
import com.example.covid19moniterapp.network.AllCountriesObject
import com.example.covid19moniterapp.network.allCountries.MainGlobalData
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.coroutines.*
import retrofit2.HttpException
import timber.log.Timber
import java.net.UnknownHostException
import java.util.*
import kotlin.collections.LinkedHashMap

class AllCountriesViewModel(val database: DataBase, val context: Context) : ViewModel() {

    val adapter = GroupAdapter<GroupieViewHolder>()
    private val countriesMap = LinkedHashMap<String, EachCountryGroup>()

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _mainGlobalDataData = MutableLiveData<MainGlobalData>()
    val mainGlobalDataData: LiveData<MainGlobalData>
        get() = _mainGlobalDataData

    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status>
        get() = _status

    init {
        getAllCountries()
    }

    private fun getAllCountries(){
        try {
            _status.value = Status.LOADING
            uiScope.launch {
                refreshData()
            }
        }
        catch (https: HttpException){
            _status.value = Status.LOCATION_ERROR
        }
        catch (netError: UnknownHostException){
            _status.value = Status.NETWORK_ERROR
        }
    }

    fun doSearchQuery(newText: String?) {
//        getAllCountries()
        Timber.d("_status.value is ${_status.value}")
        if (_status.value == Status.NETWORK_ERROR) getAllCountries()
        countriesMap.clear()

        if (newText != null && newText.isNotEmpty()) {
            mainGlobalDataData.value?.Countries?.let {
                for (country in it) {
                    if (country.Country.toLowerCase(Locale.ROOT).contains(newText.toLowerCase(Locale.ROOT))){
                        val eachCountryGroup = EachCountryGroup(country)
                        countriesMap[country.CountryCode] = eachCountryGroup
                    }
                }
                adapter.updateAsync(countriesMap.values.toList())
            }
        }
        else{
            mainGlobalDataData.value?.Countries?.forEach {
                val eachCountryGroup = EachCountryGroup(it)
                countriesMap[it.Country] = eachCountryGroup
            }
            adapter.updateAsync(countriesMap.values.toList())
        }
    }

    fun searchViewQueryListener(searchView: SearchView): SearchView.OnQueryTextListener {
        return object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchView.windowToken, 0)

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                doSearchQuery(newText)

                return true
            }
        }
    }

    private suspend fun refreshData() {
        withContext(Dispatchers.IO) {
            val allCountries =
                AllCountriesObject.futureRetrofitService.getAllCountriesAsync().await()
            val allCountriesDatabaseClass = AllCountriesDatabaseClass(mainGlobalData = allCountries)

            database.allCountriesDao.insertAllCountriesClass(allCountriesDatabaseClass)
//        _mainGlobalDataData.value = database.allCountriesDao.getAllCountriesClass().mainGlobalData
            uiScope.launch {
                _mainGlobalDataData.value = allCountries
//                _mainGlobalDataData.value = database.allCountriesDao.getAllCountriesClass().mainGlobalData
                Timber.i("_allCountries country is ${_mainGlobalDataData.value?.Countries?.get(0)?.Country}")
                _status.value = Status.DONE
            }
        }
    }

}