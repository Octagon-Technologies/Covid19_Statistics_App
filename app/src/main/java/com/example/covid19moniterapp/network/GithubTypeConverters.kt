package com.example.covid19moniterapp.network

import android.util.Log
import androidx.room.TypeConverter
import com.example.covid19moniterapp.network.allCountries.MainGlobalData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class GithubTypeConverters {
        var gson: Gson = Gson()

        @TypeConverter
        fun stringToAllCountries(data: String?): MainGlobalData? {
            if (data == null) {
                Log.i("TypeConverters", "currentData is null")
                return null
            }
            val listType: Type = object : TypeToken<MainGlobalData?>() {}.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun allCountriesToString(mainGlobalData: MainGlobalData): String {
//            Log.i("TypeConverters", gson.toJson(currentWeatherDataClass))
            return gson.toJson(mainGlobalData)
        }

        @TypeConverter
        fun stringToCurrentCountry(data: String?): List<com.example.covid19moniterapp.network.currentCountry.CurrentCountryItem>?  {
            if (data == null) {
                Log.i("TypeConverters", "futureData is null")
                return null
            }
            val listType: Type = object : TypeToken<List<com.example.covid19moniterapp.network.currentCountry.CurrentCountryItem>?>() {}.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun futureWeatherDataClassToString(currentCountry: List<com.example.covid19moniterapp.network.currentCountry.CurrentCountryItem>): String {
//            Log.i("TypeConverter", gson.toJson(futureWeatherDataClass))
            return gson.toJson(currentCountry)
        }
    }

//        var gson: Gson = Gson()
//
//        @TypeConverter
//        fun stringToSomeObjectList(data: String?): List<All> {
//            if (data == null) {
//                return Collections.emptyList()
//            }
//            val listType: Type =
//                object : TypeToken<List<All?>?>() {}.type
//            return gson.fromJson(data, listType)
//        }
//
//        @TypeConverter
//        fun someObjectListToString(someObjects: List<All?>?): String {
//            return gson.toJson(someObjects)
//        }
