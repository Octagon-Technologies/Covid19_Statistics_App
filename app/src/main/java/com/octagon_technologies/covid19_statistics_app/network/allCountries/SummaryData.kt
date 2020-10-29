package com.octagon_technologies.covid19_statistics_app.network.allCountries

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SummaryData(
    val NewConfirmed: Int,
    val NewDeaths: Int,
    val NewRecovered: Int,
    val TotalConfirmed: Int,
    val TotalDeaths: Int,
    val TotalRecovered: Int
)