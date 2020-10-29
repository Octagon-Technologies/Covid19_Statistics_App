package com.octagon_technologies.covid19_statistics_app.ui.allCountries

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.octagon_technologies.covid19_statistics_app.Status
import com.octagon_technologies.covid19_statistics_app.database.MainDataBase
import com.octagon_technologies.covid19_statistics_app.network.allCountries.Global
import com.octagon_technologies.covid19_statistics_app.shared_code.MainGlobalItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class AllCountriesViewModel(val context: Context) : ViewModel() {

    private val mainDataBase = MainDataBase.getInstance(context)
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _liveGlobal = MutableLiveData<Global>()
    val liveGlobal: LiveData<Global>
        get() = _liveGlobal

    private var _status = MutableLiveData(Status.LOADING)
    val status: LiveData<Status>
        get() = _status

    init {
        getAllCountries()
    }

    fun getAllCountries() {
        try {
            uiScope.launch {
                _liveGlobal.value = MainGlobalItem.getGlobalAsync(mainDataBase)
                _status.value = Status.DONE
            }
        } catch (netError: UnknownHostException) {
            _status.value = Status.NETWORK_ERROR
        }
    }


}