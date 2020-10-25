package com.octagon_technologies.covid19moniterapp.network.allCountries

data class SummaryData(
    val NewConfirmed: Int,
    val NewDeaths: Int,
    val NewRecovered: Int,
    val TotalConfirmed: Int,
    val TotalDeaths: Int,
    val TotalRecovered: Int
)