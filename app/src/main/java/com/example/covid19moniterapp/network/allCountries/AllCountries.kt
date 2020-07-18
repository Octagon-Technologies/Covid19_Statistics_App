package com.example.covid19moniterapp.network.allCountries

data class AllCountries(
    val Countries: List<Country>,
    val Date: String,
    val Global: Global
)