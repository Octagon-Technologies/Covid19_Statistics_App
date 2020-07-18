package com.example.covid19moniterapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertString(data: DataClass)

    @Query("SELECT * FROM dataClass ORDER BY name_id DESC LIMIT 1")
    fun getFormerString(): DataClass?
}

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
    fun insertAllCountriesClass(allClass: AllCountriesDatabaseClass)

    @Query("SELECT * FROM allCountriesDataClass")
    fun getAllCountriesClass(): AllCountriesDatabaseClass
}
