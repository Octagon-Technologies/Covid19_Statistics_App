package com.octagon_technologies.covid19moniterapp.ui.allCountries

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.octagon_technologies.covid19moniterapp.Status
import com.octagon_technologies.covid19moniterapp.database.MainDataBase
import com.octagon_technologies.covid19moniterapp.network.allCountries.Global
import com.octagon_technologies.covid19moniterapp.shared_code.MainGlobalItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException

class AllCountriesViewModel(val context: Context) : ViewModel() {

    private val mainDataBase = MainDataBase.getInstance(context)
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _liveGlobal = MutableLiveData<Global>()
    val liveGlobal: LiveData<Global>
        get() = _liveGlobal

    private var _status = MutableLiveData<Status>()
    val status: LiveData<Status>
        get() = _status


    fun getAllCountries() {
        try {
            _status.value = Status.LOADING
            uiScope.launch {
                _liveGlobal.value = MainGlobalItem.getGlobalAsync(mainDataBase)
                _status.value = Status.DONE
            }
        } catch (https: HttpException) {
            _status.value = Status.LOCATION_ERROR
        } catch (netError: UnknownHostException) {
            _status.value = Status.NETWORK_ERROR
        }
    }


}