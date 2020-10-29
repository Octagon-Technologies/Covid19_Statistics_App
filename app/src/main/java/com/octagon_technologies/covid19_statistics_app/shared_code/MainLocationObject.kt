package com.octagon_technologies.covid19_statistics_app.shared_code

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.octagon_technologies.covid19_statistics_app.network.LocationRetrofitObject
import com.octagon_technologies.covid19_statistics_app.network.reverse_geocoding_location.ReverseGeoCodingLocation
import com.octagon_technologies.covid19_statistics_app.ui.find_location.Coordinates
import retrofit2.HttpException
import timber.log.Timber
import java.net.UnknownHostException

object MainLocationObject {

    @SuppressLint("MissingPermission")
    fun turnOnGPS(context: Context): Coordinates {
        var coordinates: Coordinates? = null
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            .getCurrentLocation(LocationRequest.PRIORITY_LOW_POWER, null)

        fusedLocationProviderClient.addOnSuccessListener {
            coordinates = Coordinates(lon = it.longitude, lat = it.latitude)
        }

        return coordinates ?: Coordinates(lon = 36.3, lat = -1.674)
    }

    suspend fun getLocationNameFromCoordinatesAsync(
        coordinates: Coordinates
    ): ReverseGeoCodingLocation? {
        return try {
            val remoteReversedLocation =
                LocationRetrofitObject.locationRetrofitService.getLocationNameFromCoordinatesAsync(
                    lat = coordinates.lat,
                    lon = coordinates.lon
                ).await()

            remoteReversedLocation
        } catch (uhe: UnknownHostException) {
            Timber.e(uhe, "No internet")
            null
        } catch (npe: NullPointerException) {
            Timber.e(npe)
            null
        } catch (e: HttpException) {
            Timber.e(e)
            null
        } catch (e: Exception) {
            Timber.e(e)
            throw e
        }
    }

}