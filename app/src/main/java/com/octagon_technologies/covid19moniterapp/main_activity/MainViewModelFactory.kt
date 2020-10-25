package com.octagon_technologies.covid19moniterapp.main_activity

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}