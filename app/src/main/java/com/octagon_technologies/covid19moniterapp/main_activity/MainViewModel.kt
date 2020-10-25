package com.octagon_technologies.covid19moniterapp.main_activity

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.octagon_technologies.covid19moniterapp.SettingsDataStore
import com.octagon_technologies.covid19moniterapp.Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(private val context: Context) : ViewModel() {
    private var _theme = MutableLiveData<Theme>()
    val theme: LiveData<Theme>
        get() = _theme

    var location = MutableLiveData<String>()

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val settingsDataStore: SettingsDataStore by lazy {
        SettingsDataStore(context)
    }

    init {
        uiScope.launch {
            location.value = settingsDataStore.getLocation().first()
            Timber.d("In MainViewModel, location.value is ${location.value}")
        }
        getCurrentTheme()
    }

    fun getCurrentTheme() {
        uiScope.launch {
            _theme.value = settingsDataStore.getTheme().first()
        }
    }

    fun changeThemeToWhite() {
        settingsDataStore.editTheme(Theme.LIGHT)
        uiScope.launch {
            _theme.value = Theme.LIGHT
        }
    }

    fun changeThemeToDark() {
        settingsDataStore.editTheme(Theme.DARK)
        uiScope.launch {
            _theme.value = Theme.DARK
        }
    }
}