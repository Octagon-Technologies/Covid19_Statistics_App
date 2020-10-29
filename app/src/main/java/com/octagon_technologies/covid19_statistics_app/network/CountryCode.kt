package com.octagon_technologies.covid19_statistics_app.network


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Keep
data class CountryCode(
    @Json(name = "alpha2Code")
    val countryCodeName: String?
)