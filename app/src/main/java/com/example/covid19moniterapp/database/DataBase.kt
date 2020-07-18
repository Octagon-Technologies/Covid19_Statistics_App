package com.example.covid19moniterapp.database

import android.content.Context
import androidx.room.*
import com.example.covid19moniterapp.network.GithubTypeConverters

@Database(entities = [DataClass::class,
    CurrentDatabaseClass::class, AllCountriesDatabaseClass::class], version = 1, exportSchema = false)
@TypeConverters(value = [(GithubTypeConverters::class)])
abstract class DataBase: RoomDatabase(){

    abstract val databaseDao: DatabaseDao
    abstract val currentDao: CurrentWeatherDao
    abstract val allCountriesDao: AllCountriesWeatherDao

    companion object{
        @Volatile
        var INSTANCE: DataBase? = null

        fun getInstance(context: Context): DataBase? {
            var instance = INSTANCE
            if (instance == null){
                instance = Room.databaseBuilder(context.applicationContext, DataBase::class.java, "namesDatabase")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return instance
        }
    }
}