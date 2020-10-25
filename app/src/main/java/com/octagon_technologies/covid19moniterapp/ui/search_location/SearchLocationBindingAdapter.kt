package com.octagon_technologies.covid19moniterapp.ui.search_location

import android.widget.Button
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import com.octagon_technologies.covid19moniterapp.R
import com.octagon_technologies.covid19moniterapp.SettingsDataStore
import com.octagon_technologies.covid19moniterapp.ui.find_location.FindLocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@BindingAdapter("getFindLocationViewModel", "getLifecycleOwner")
fun Button.getCurrentLocation(
    findLocationViewModel: FindLocationViewModel,
    lifecycleOwner: LifecycleOwner
) {
    val settingsDataStore: SettingsDataStore by lazy {
        SettingsDataStore(context)
    }

    findLocationViewModel.reversedGeoCodingLocation.observe(lifecycleOwner, {
        CoroutineScope(Dispatchers.Main).launch {
            settingsDataStore.editLocation(it.reverseGeoCodingAddress?.country ?: return@launch)
        }
        findNavController().popBackStack(R.id.currentFragment, false)
    })

    setOnClickListener {
        findLocationViewModel.checkIfPermissionIsGranted()
    }
}

