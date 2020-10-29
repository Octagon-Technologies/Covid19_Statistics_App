package com.octagon_technologies.covid19_statistics_app.ui.find_location

import android.Manifest
import android.content.Context
import android.location.LocationManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.karumi.dexter.Dexter.withContext
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.octagon_technologies.covid19_statistics_app.SettingsDataStore
import com.octagon_technologies.covid19_statistics_app.network.reverse_geocoding_location.ReverseGeoCodingLocation
import com.octagon_technologies.covid19_statistics_app.shared_code.MainLocationObject
import com.octagon_technologies.covid19_statistics_app.shared_code.MainLocationObject.turnOnGPS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

class FindLocationViewModel(private val context: Context) : ViewModel() {
    private val uiScope = CoroutineScope(Dispatchers.Main)
    val settingsDataStore = SettingsDataStore(context)

    var liveRecentAdapterPosition = MutableLiveData(-1)

    private var _reversedGeoCodingLocation = MutableLiveData<ReverseGeoCodingLocation>()
    val reversedGeoCodingLocation: LiveData<ReverseGeoCodingLocation>
        get() = _reversedGeoCodingLocation

    private var _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private var _favouriteLocationsList = MutableLiveData<ArrayList<String>?>()
    val favouriteLocationsList: LiveData<ArrayList<String>?>
        get() = _favouriteLocationsList

    private var _recentLocationsList = MutableLiveData<ArrayList<String>?>()
    val recentLocationsList: LiveData<ArrayList<String>?>
        get() = _recentLocationsList


    fun getHistoryLocations(isRecent: Boolean = false) {
        uiScope.launch {
            if (isRecent) {
                _recentLocationsList.value =
                    settingsDataStore.getHistoryLocations(isRecent).first().distinct().toMutableList() as ArrayList<String>
                Timber.d("Recent size is ${_recentLocationsList.value}")
            } else {
                _favouriteLocationsList.value =
                    settingsDataStore.getHistoryLocations(isRecent).first()
                Timber.d("Favourite size is ${_recentLocationsList.value}")
            }

        }
    }

    fun saveLocationInDataStore() {
        settingsDataStore.editLocation(
            _reversedGeoCodingLocation.value?.reverseGeoCodingAddress?.country ?: return
        )
    }

    private fun getLocation() {
        uiScope.launch {
            _reversedGeoCodingLocation.value =
                MainLocationObject.getLocationNameFromCoordinatesAsync(
                    turnOnGPS(context)
                )
            _reversedGeoCodingLocation.value?.also {
                Timber.d("_coordinates.value is ${_reversedGeoCodingLocation.value}")
                _isLoading.value = false
            }
        }

    }

    fun turnOnLocation() {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        Timber.d("gpsEnabled is $gpsEnabled")
        getLocation()
    }

    fun checkIfPermissionIsGranted() {
        _isLoading.value = true
        withContext(context)
            .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    Timber.d("onPermissionGranted called")
                    turnOnLocation()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Timber.d("onPermissionDenied called")
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    Timber.d("onPermissionDenied called")
                }
            })
            .withErrorListener {
                Timber.e(it.toString())
            }
            .check()
    }
}