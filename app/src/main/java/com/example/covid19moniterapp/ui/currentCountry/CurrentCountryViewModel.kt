package com.example.covid19moniterapp.ui.currentCountry

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.covid19moniterapp.*
import com.example.covid19moniterapp.database.AllCountriesDatabaseClass
import com.example.covid19moniterapp.database.CurrentDatabaseClass
import com.example.covid19moniterapp.database.DataBase
import com.example.covid19moniterapp.network.AllCountriesObject
import com.example.covid19moniterapp.network.CurrentCountryObject
import com.example.covid19moniterapp.network.allCountries.MainGlobalData
import com.example.covid19moniterapp.network.currentCountry.CurrentCountryItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import timber.log.Timber
import java.net.UnknownHostException

class CurrentCountryViewModel(private val context: Context): ViewModel() {

    val database = DataBase.getInstance(context)!!
    val adapter = GroupAdapter<GroupieViewHolder>()
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val myDataStore = MyDataStore(context)
    private lateinit var mainGlobalData: MainGlobalData

//    private val provinceList

    private val provinceList = ArrayList<String>()
    val spinnerArrayAdapter = ArrayAdapter(context, R.layout.simple_spinner_item, provinceList).apply {
        this.setDropDownViewResource(R.layout.simple_spinner_item)
    }

    init{
        uiScope.launch {
            refreshAllCountriesData()
        }
    }

    private var _currentCountryData = MutableLiveData<List<CurrentCountryItem>>()
    val currentCountryData: LiveData<List<CurrentCountryItem>>
        get() = _currentCountryData
//    val currentCountryDataMap = LinkedHashMap<String, EachCurrentHistoryGroup>()

    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status>
        get() = _status

    private var _countryTotal = MutableLiveData<Total>()
    val countryTotal: LiveData<Total>
        get() = _countryTotal

    fun getCurrentCountry(){
        try{
            _status.value = Status.LOADING
            CoroutineScope(Dispatchers.Main).launch {
                refreshData()
                Timber.d("refreshData called in uiScope.launch")
            }
            Timber.d("getCurrentCountry called")
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

    fun getAllCountries(){
        try {
            uiScope.launch {
                refreshAllCountriesData()
            }
        }
        catch (e: Throwable){
            Timber.e(e)
        }
    }

    private suspend fun refreshAllCountriesData() {
        withContext(Dispatchers.IO) {

            try {
                val allCountries =
                    AllCountriesObject.futureRetrofitService.getAllCountriesAsync().await()
                val allCountriesDatabaseClass =
                    AllCountriesDatabaseClass(mainGlobalData = allCountries)

                database.allCountriesDao.insertAllCountriesClass(allCountriesDatabaseClass)
            }
            catch (e: UnknownHostException) {
                Timber.e(e)
                context.showShortToast("No internet connection available")
            }
            finally {
                mainGlobalData = database.allCountriesDao.getAllCountriesClass().mainGlobalData
            }

        }
    }

    private fun getCurrentCountryDetails() {
        val location =
            _currentCountryData.value?.get(0)?.Country //runBlocking {  myDataStore.getLocation().first() }
        Timber.d("location is $location")

        try {
            mainGlobalData.Countries?.forEach {
                if (location == it.Country) {
                    val total = Total(
                        it.TotalConfirmed,
                        it.TotalDeaths,
                        it.TotalRecovered,
                        it.NewConfirmed,
                        it.NewRecovered,
                        it.Country,
                        it.CountryCode
                    )
                    Timber.d("total is $total")
                    uiScope.launch {
                        _countryTotal.value = total
                    }
                }
            }
        }
        catch (e: UninitializedPropertyAccessException) {
            Timber.e(e)
            context.showShortToast("No internet available.")
        }
    }

    fun addProvincesToAdapter(){
        _currentCountryData.value?.let {
            for (country in it) {
                country.Province?.let { province ->
                    val newProvince = if (province.isEmpty()) "Mainland" else province

                    Timber.d("provinceList.contains(newProvince) is ${provinceList.contains(newProvince)}")
                    if (!provinceList.contains(newProvince)) {

                        provinceList.add(newProvince)
                        spinnerArrayAdapter.notifyDataSetChanged()

                    }
                }
            }

//            if (provinceList.size > 1 && !provinceList.contains("Total")) {
//                provinceList.add(0, "Total")
//                spinnerArrayAdapter.notifyDataSetChanged()
//            }
        }
    }

    fun spinnerOnItemClickListener(position: Int) {
        adapter.clear()
        Timber.i("provinceList[position] is ${provinceList[position]}")

        _currentCountryData.value?.reversed()?.let { list ->
            val arrayOfEachCurrentHistoryGroup = ArrayList<EachCurrentHistoryGroup>()
            for (eachItem in list){
                eachItem.Province?.let {
                    if (provinceList[position] == "Mainland"){
                        if (it == ""){
                            arrayOfEachCurrentHistoryGroup.add(EachCurrentHistoryGroup(eachItem))
                        }
                    }
                    else {
                        if (it == provinceList[position]) {
                            arrayOfEachCurrentHistoryGroup.add(EachCurrentHistoryGroup(eachItem))
                        }
                    }
                }
            }

            adapter.updateAsync(arrayOfEachCurrentHistoryGroup)
        }
    }

    fun loadImage(imgView: ImageView, imgUrl: String){
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()

        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image))
            .into(imgView)
    }

    private suspend fun refreshData() {
        withContext(Dispatchers.IO) {
            try {
                Timber.d("Start of refreshData")
                val currentLocation = myDataStore.getLocation().first()
                Timber.d("currentLocation is $currentLocation")
                val currentCountry = CurrentCountryObject.currentRetrofitService.getCurrentWeatherAsync(currentLocation).await()
                val currentDatabaseClass = CurrentDatabaseClass(current_country = currentCountry)
                Timber.i("Online Country is ${currentCountry[0].Country}")

                database.currentDao.insertCurrentDataClass(currentDatabaseClass)
            }
            catch (e : UnknownHostException) {
                Timber.e(e)
                context.showShortToast("No internet connection available.")
            }
            finally {
                uiScope.launch {
                    _currentCountryData.value = database.currentDao.getCurrentDataClass().current_country
                    getCurrentCountryDetails()
                }
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        provinceList.clear()
    }
}