package com.example.covid19moniterapp

import android.content.Context
import android.widget.Toast

var cityName : String = ""

enum class Status{LOADING, LOCATION_ERROR, NETWORK_ERROR, DONE}

data class Total(val totalCases: Int, val totalDeaths: Int, val totalRecovered: Int, val newCases: Int, val newRecovered: Int, val countryName: String, val countryCode: String)

fun Context.showShortToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.showLongToast(text: String) {
    Toast.makeText(this.applicationContext, text, Toast.LENGTH_LONG).show()
}