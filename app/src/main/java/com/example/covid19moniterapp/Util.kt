package com.example.covid19moniterapp

var cityName : String = ""

enum class Status{LOADING, LOCATION_ERROR, NETWORK_ERROR, DONE}

data class Total(val totalCases: Int, val totalDeaths: Int, val totalRecovered: Int, val newCases: Int, val newRecovered: Int)