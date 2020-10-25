package com.octagon_technologies.covid19moniterapp.ui.search_location

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.util.*

class SearchLocationViewModel(val context: Context) : ViewModel() {
    private val viewModelJob = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _locationSuggestions = MutableLiveData<ArrayList<String>>()
    val locationSuggestions: LiveData<ArrayList<String>>
        get() = _locationSuggestions

    fun getLocationSuggestions(query: String) {
        if (query.isEmpty()) return
        val arrayOfSuggestions = arrayListOf<String>()
        arrayOfSuggestions.addAll(listOfAllCountries.filter { it.toLowerCase(Locale.getDefault()).contains(query.toLowerCase(
            Locale.getDefault()), true) }.toList())

        _locationSuggestions.value = arrayOfSuggestions
    }
}