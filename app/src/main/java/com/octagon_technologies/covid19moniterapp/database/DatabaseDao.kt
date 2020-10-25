package com.octagon_technologies.covid19moniterapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CurrentWeatherDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrentDataClass(currentDatabaseClass: CurrentDatabaseClass)

    @Query("SELECT * FROM currentCountryDataClass")
    fun getCurrentDataClass():CurrentDatabaseClass

}

@Dao
interface AllCountriesWeatherDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGlobalClass(globalDatabaseClass: GlobalDatabaseClass)

    @Query("SELECT * FROM allCountriesDataClass")
    fun getGlobalClass(): GlobalDatabaseClass
}
