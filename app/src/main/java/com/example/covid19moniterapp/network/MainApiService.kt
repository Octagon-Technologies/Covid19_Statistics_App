package com.example.covid19moniterapp.network

import com.example.covid19moniterapp.network.allCountries.MainGlobalData
import com.example.covid19moniterapp.network.currentCountry.CurrentCountryItem
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

const val BASE_URL = "https://api.covid19api.com/"
const val COUNTRY_IMAGE_BASE_URL = "https://www.countryflags.io/"

// extra = be/flat/64.png

val moshi: Moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

var logging = HttpLoggingInterceptor().apply {
    this.level = HttpLoggingInterceptor.Level.HEADERS
}

val httpClient = OkHttpClient.Builder().apply {
    this.addInterceptor(logging)
    this.connectTimeout(60, TimeUnit.SECONDS)
    this.readTimeout(5, TimeUnit.MINUTES)
    this.writeTimeout(60, TimeUnit.SECONDS)
}

    val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(BASE_URL)
        .client(httpClient.build())
        .build()

//all weather full url = https://api.covid19api.com/summary

    interface AllCountriesApiService {
        @GET("summary")
        fun getAllCountriesAsync(): Deferred<MainGlobalData>
    }

    interface CurrentCountryApiService {
        @GET("dayone/country/{countryName}")
        fun getCurrentWeatherAsync(
            @Path("countryName") countryName: String
        ): Deferred<List<CurrentCountryItem>>
    }

// current country full url = https://api.covid19api.com/dayone/country/south-africa
// current country = dayone/country/south-africa

    object AllCountriesObject{
        val futureRetrofitService: AllCountriesApiService by lazy {
            retrofit.create(AllCountriesApiService::class.java)
        }
    }

    object CurrentCountryObject{
        val currentRetrofitService: CurrentCountryApiService by lazy {
            retrofit.create(CurrentCountryApiService::class.java)
        }
    }




