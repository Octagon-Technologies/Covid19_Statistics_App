package com.octagon_technologies.covid19moniterapp.network.allCountries
import androidx.annotation.Keep

@Keep
data class EachCountry(
    val Country: String,
    val CountryCode: String,
    val Date: String,
    val NewConfirmed: Int,
    val NewDeaths: Int,
    val NewRecovered: Int,
    val Premium: Premium,
    val Slug: String,
    val TotalConfirmed: Int,
    val TotalDeaths: Int,
    val TotalRecovered: Int
)