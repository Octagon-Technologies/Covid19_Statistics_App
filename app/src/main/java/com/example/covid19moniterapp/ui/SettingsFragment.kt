package com.example.covid19moniterapp.ui

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.covid19moniterapp.MainActivity
import com.example.covid19moniterapp.R
import com.example.covid19moniterapp.cityName
import com.example.covid19moniterapp.database.DataBase
import com.example.covid19moniterapp.repo.Repo
import com.example.covid19moniterapp.viewmodel.SharedViewModelFactory
import com.example.covid19moniterapp.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val SET_LOCATION = "setLocation"
const val USE_DEVICE_LOCATION = "USE_DEVICE_LOCATION"

class FragmentSettings: PreferenceFragmentCompat() {

    private lateinit var viewModel: SharedViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.root_preferences)

        val application = requireNotNull(this.activity).application
        val dataBase = DataBase.getInstance(application)
        val repo = Repo(dataBase!!)

        viewModel = ViewModelProvider(this,
            SharedViewModelFactory(
                dataBase
            )
        ).get(SharedViewModel::class.java)

        (activity as MainActivity).supportActionBar?.title = "Settings"

            findPreference<Preference>(SET_LOCATION)!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener{ preference, newValue ->
            val stringValue = newValue.toString()
            cityName = stringValue
            viewModel.insertCityName()

            repo.searchData()
            CoroutineScope(Dispatchers.Main).launch {
                repo.refreshData()
            }

            true
        }

    }
}