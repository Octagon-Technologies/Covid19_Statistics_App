package com.octagon_technologies.covid19moniterapp.database

import android.content.Context
import androidx.annotation.Keep
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        CurrentDatabaseClass::class, GlobalDatabaseClass::class],
    version = 2,
    exportSchema = false
)
@Keep
@TypeConverters(value = [(GithubTypeConverters::class)])
abstract class MainDataBase : RoomDatabase() {

    abstract val currentDao: CurrentWeatherDao
    abstract val globalDao: AllCountriesWeatherDao

    @Keep
    companion object {
        @Volatile
        @Keep
        var INSTANCE: MainDataBase? = null

        fun getInstance(context: Context): MainDataBase? {
            var instance = INSTANCE
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDataBase::class.java,
                    "namesDatabase"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return instance
        }
    }
}