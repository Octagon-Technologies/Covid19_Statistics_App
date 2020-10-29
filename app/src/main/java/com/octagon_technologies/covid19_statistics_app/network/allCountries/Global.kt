package com.octagon_technologies.covid19_statistics_app.network.allCountries

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Keep
data class Global(
    @Keep
    val Countries: List<EachCountry>?,
    @Keep
    val Date: String,

    @Keep
    @Json(name = "Global")
    val summaryData: SummaryData
)