package com.example.covid19moniterapp.ui.allCountries

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.covid19moniterapp.database.DataBase

class AllCountriesViewModelFactory(
    private val dataBase: DataBase,
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllCountriesViewModel::class.java)) {
            return AllCountriesViewModel(dataBase, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}