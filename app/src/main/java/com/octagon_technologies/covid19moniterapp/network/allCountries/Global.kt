package com.octagon_technologies.covid19moniterapp.network.allCountries

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class Global(
    val Countries: List<EachCountry>?,
    val Date: String,

    @Json(name = "Global")
    val summaryData: SummaryData
)