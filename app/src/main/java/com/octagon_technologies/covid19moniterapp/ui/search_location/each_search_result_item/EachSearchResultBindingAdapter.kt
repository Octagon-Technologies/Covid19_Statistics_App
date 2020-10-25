package com.octagon_technologies.covid19moniterapp.ui.search_location.each_search_result_item

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.octagon_technologies.covid19moniterapp.R
import com.octagon_technologies.covid19moniterapp.SettingsDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@BindingAdapter("addLocationToStorage", "addBindingCountryName", "addLiveLocation")
fun ConstraintLayout.addLocationToStorage(
    location: String?,
    bindingCountryName: TextView,
    liveLocation: MutableLiveData<String>
) {
    val settingsDataStore: SettingsDataStore by lazy {
        SettingsDataStore(context)
    }

    setOnClickListener {
        settingsDataStore.editLocation(location ?: return@setOnClickListener)
        liveLocation.value = location
        bindingCountryName.text = location
        findNavController().popBackStack(R.id.currentFragment, false)
    }
}

@BindingAdapter("addToFavouriteIcon")
fun ImageView.addToFavouriteIcon(location: String?) {
    val uiScope: CoroutineScope by lazy { CoroutineScope(Dispatchers.Main) }
    val addImage: Drawable.ConstantState? by lazy {
        ResourcesCompat.getDrawable(
            resources,
            R.drawable.ic_add_circle_outline_black,
            null
        )?.constantState
    }
    val settingsDataStore: SettingsDataStore by lazy {
        SettingsDataStore(context)
    }


    setOnClickListener {
        uiScope.launch {
            setImageResource(
                if (drawable.constantState == addImage) {
                    settingsDataStore.addFavouriteLocation(
                        location ?: return@launch
                    )
                    R.drawable.ic_check_circle_white
                } else {
                    settingsDataStore.removeFavouriteLocation(
                        location ?: return@launch
                    )
                    R.drawable.ic_add_circle_outline_black
                }
            )
        }
    }

}
