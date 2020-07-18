package com.example.covid19moniterapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.covid19moniterapp.network.allCountries.AllCountries
import com.example.covid19moniterapp.network.currentCountry.CurrentCountry
import com.example.covid19moniterapp.network.currentCountry.CurrentCountryItem
import com.squareup.moshi.Json

// TODO: Add type converter for entities

const val MAIN_VALUE = 1
const val CURRENT_WEATHER_VALUE = 10
const val FUTURE_WEATHER_VALUE = 5

@Entity(tableName = "dataClass")
data class DataClass(
        @PrimaryKey(autoGenerate = false)
        val name_id: Int = MAIN_VALUE,

        @ColumnInfo
        val name_text: String?
)

@Entity(tableName = "allCountriesDataClass")
data class AllCountriesDatabaseClass(
        @PrimaryKey(autoGenerate = false)
        val all_countries_id: Int = FUTURE_WEATHER_VALUE,

        @ColumnInfo
        val all_countries: AllCountries

)

@Entity(tableName = "currentCountryDataClass")
data class CurrentDatabaseClass(
        @PrimaryKey(autoGenerate = false)
        val current_country_id: Int = CURRENT_WEATHER_VALUE,

        @ColumnInfo
        val current_country: List<CurrentCountryItem>

)

@Entity(tableName = "countryImageDataClass")
data class CountryImage(
        @PrimaryKey(autoGenerate = false)
        val country_code: String,

        @ColumnInfo
        val country_https: String
)



/*

// TODO: Glide converter

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?){
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()

        Glide.with(imgView.context)
                .load(imgUri)
                .apply(RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image))
                .into(imgView)
    }
}
 */
