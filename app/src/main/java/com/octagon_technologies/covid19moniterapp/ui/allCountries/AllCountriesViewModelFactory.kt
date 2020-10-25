package com.octagon_technologies.covid19moniterapp.ui.allCountries

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AllCountriesViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllCountriesViewModel::class.java)) {
            return AllCountriesViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}