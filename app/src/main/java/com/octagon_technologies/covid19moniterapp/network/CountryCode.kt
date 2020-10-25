package com.octagon_technologies.covid19moniterapp.network


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class CountryCode(
    @Json(name = "alpha2Code")
    val countryCodeName: String?
)