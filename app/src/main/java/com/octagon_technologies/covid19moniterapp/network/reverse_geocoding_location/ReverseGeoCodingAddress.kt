package com.octagon_technologies.covid19moniterapp.network.reverse_geocoding_location


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class ReverseGeoCodingAddress(
    @Json(name = "city")
    val city: String?,
    @Json(name = "country")
    val country: String?,
    @Json(name = "country_code")
    val countryCode: String?,
    @Json(name = "postcode")
    val postcode: String?,
    @Json(name = "residential")
    val residential: String?,
    @Json(name = "state")
    val state: String?,
    @Json(name = "suburb")
    val suburb: String?
)