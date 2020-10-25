package com.octagon_technologies.covid19moniterapp.network.currentCountry

data class CurrentCountry(
    val Active: Int,
    val City: String?,
    val CityCode: String?,
    val Confirmed: Int,
    val Country: String,
    val CountryCode: String?,
    val Date: String,
    val Deaths: Int,
    val Lat: String,
    val Lon: String,
    val Province: String?,
    val Recovered: Int
)

// 2020-01-23T00:00:00Z
// 12434678008
// 23 June 2020

