package com.octagon_technologies.covid19_statistics_app.ui.search_location

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.octagon_technologies.covid19_statistics_app.SettingsDataStore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.*
import kotlin.collections.ArrayList

class SearchLocationViewModel(val context: Context) : ViewModel() {
    private val viewModelJob = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _locationSuggestions = MutableLiveData<ArrayList<String>>()
    val locationSuggestions: LiveData<ArrayList<String>>
        get() = _locationSuggestions

    private val settingsDataStore: SettingsDataStore by lazy {
        SettingsDataStore(context)
    }

    val favouritesList = ArrayList<String>()

    init {
        uiScope.launch {
            favouritesList.addAll(settingsDataStore.getHistoryLocations().first())
        }
    }

    fun getLocationSuggestions(query: String) {
        if (query.isEmpty()) return
        CoroutineScope(Dispatchers.IO).launch {
            val arrayOfSuggestions = arrayListOf<String>()
            arrayOfSuggestions.addAll(listOfAllCountries.filter {
                it.toLowerCase(Locale.getDefault()).contains(
                    query.toLowerCase(
                        Locale.getDefault()
                    ), true
                )
            }.toList())

            withContext(Dispatchers.Main) {
                _locationSuggestions.value = arrayOfSuggestions
            }
        }
    }
}