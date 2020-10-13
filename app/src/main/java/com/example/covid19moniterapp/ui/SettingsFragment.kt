package com.example.covid19moniterapp.ui

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.covid19moniterapp.MainActivity
import com.example.covid19moniterapp.MyDataStore
import com.example.covid19moniterapp.R

const val SET_LOCATION = "setLocation"

class FragmentSettings: PreferenceFragmentCompat() {


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.root_preferences)

        val myDataStore = MyDataStore(requireContext())

        (activity as MainActivity).supportActionBar?.title = "Settings"

        findPreference<Preference>(SET_LOCATION)!!.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                val stringValue = newValue.toString()
                myDataStore.editLocation(stringValue)

                true
            }

    }
}