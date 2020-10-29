package com.octagon_technologies.covid19_statistics_app.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.octagon_technologies.covid19_statistics_app.network.allCountries.Global
import com.octagon_technologies.covid19_statistics_app.network.currentCountry.CurrentCountry

@Entity(tableName = "currentCountryDataClass")
data class CurrentDatabaseClass(
        @PrimaryKey(autoGenerate = false)
        val id: Int = 1,

        @ColumnInfo
        val listOfCurrentCountry: List<CurrentCountry>

)

@Entity(tableName = "allCountriesDataClass")
data class GlobalDatabaseClass(
        @PrimaryKey(autoGenerate = false)
        val id: Int = 2,

        @ColumnInfo
        val global: Global

)

