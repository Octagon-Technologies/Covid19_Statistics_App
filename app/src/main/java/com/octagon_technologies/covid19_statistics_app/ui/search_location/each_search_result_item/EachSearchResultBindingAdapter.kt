package com.octagon_technologies.covid19_statistics_app.ui.search_location.each_search_result_item

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.octagon_technologies.covid19_statistics_app.*
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
        settingsDataStore.addHistoryLocation(location, true)
        liveLocation.value = location
        bindingCountryName.text = location
        findNavController().popBackStack(R.id.currentFragment, false)
    }
}

fun ImageView.removeFromRecent(location: String, theme: Theme?, liveRecentAdapterPosition: MutableLiveData<Int>?, position: Int) {
    val uiScope: CoroutineScope by lazy { CoroutineScope(Dispatchers.Main) }
    val settingsDataStore: SettingsDataStore by lazy {
        SettingsDataStore(context)
    }

    setImageResource(getActualIconFromTheme(IconType.Checked, theme))
    setOnClickListener {
        uiScope.launch {
            settingsDataStore.removeHistoryLocation(location, true)
            liveRecentAdapterPosition?.value = position
        }
    }
}

fun ImageView.addToFavouriteIcon(location: String?, theme: Theme?) {
    val uiScope: CoroutineScope by lazy { CoroutineScope(Dispatchers.Main) }
    val addImage: Drawable.ConstantState? by lazy {
        ResourcesCompat.getDrawable(
            resources,
            getActualIconFromTheme(IconType.AddItem, theme),
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
                    settingsDataStore.addHistoryLocation(
                        location ?: return@launch,
                        isRecent = false
                    )
                    getActualIconFromTheme(IconType.Checked, theme)
                } else {
                    settingsDataStore.removeHistoryLocation(
                        location ?: return@launch,
                        isRecent = false
                    )
                    getActualIconFromTheme(IconType.AddItem, theme)
                }
            )
        }
    }

}
