package com.octagon_technologies.covid19moniterapp.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.octagon_technologies.covid19moniterapp.network.allCountries.Global
import com.octagon_technologies.covid19moniterapp.network.currentCountry.CurrentCountry
import com.octagon_technologies.covid19moniterapp.network.location.Location
import com.octagon_technologies.covid19moniterapp.network.reverse_geocoding_location.ReverseGeoCodingLocation
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

const val BASE_URL = "https://api.covid19api.com/"
const val COUNTRY_IMAGE_BASE_URL = "https://www.countryflags.io/"
const val LOCATION_BASE = "https://us1.locationiq.com/v1/"
const val LOCATION_KEY = "2a13f417c6d3f3"

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
    this.readTimeout(60, TimeUnit.SECONDS)
    this.writeTimeout(60, TimeUnit.SECONDS)
}

val retrofit: Retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .client(httpClient.build())
    .baseUrl(BASE_URL)
    .build()

val countryCodeRetrofit: Retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .client(httpClient.build())
    .baseUrl("https://restcountries.eu/rest/v2/name/")
    .build()

val locationRetrofit: Retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(httpClient.build())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(LOCATION_BASE)
    .build()


interface LocationApiService {
    //  https://api.locationiq.com/v1/autocomplete.php?key=2a13f417c6d3f3&q=Miller%20Estate&limit=10
    @GET("autocomplete.php")
    fun getLocationSuggestionsAsync(
        @Query("key") key: String = LOCATION_KEY,
        @Query("q") query: String,
        @Query("limit") limit: Int = 10
    ): Deferred<List<Location>>

    //  https://us1.locationiq.com/v1/reverse.php?key=2a13f417c6d3f3&lat=-1.3135887888876425&lon=36.81903851535387&zoom=8&format=json
    @GET("reverse.php")
    fun getLocationNameFromCoordinatesAsync(
        @Query("key") key: String = LOCATION_KEY,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("zoom") zoom: Int = 8,
        @Query("format") format: String = "json"
    ): Deferred<ReverseGeoCodingLocation>
}

interface CountryCodeApiService {
    @GET("{countryName}")
    fun getCountryCodeAsync(
        @Path("countryName") countryName: String,
        @Query("fields") field: String = "alpha2Code"
    ): Deferred<List<CountryCode>>
}

interface GlobalApiService {
    @GET("summary")
    fun getGlobalAsync(): Deferred<Global>
}

// https://api.covid19api.com/total/country/Kenya
interface CurrentCountryApiService {
    @GET("total/country/{countryName}")
    fun getCurrentCountryAsync(
        @Path("countryName") countryName: String
    ): Deferred<List<CurrentCountry>>
}

// https://restcountries.eu/rest/v2/name/South%20Africa?fields=flag
object CountryCodeObject {
    val countryCodeRetrofitService: CountryCodeApiService by lazy {
        countryCodeRetrofit.create(CountryCodeApiService::class.java)
    }
}

//all countries full url = https://api.covid19api.com/summary
object GlobalRetrofitObject {
    val globalRetrofitService: GlobalApiService by lazy {
        retrofit.create(GlobalApiService::class.java)
    }
}

// current eachCountry full url = https://api.covid19api.com/total/country/south-africa
object CurrentCountryObject {
    val currentRetrofitService: CurrentCountryApiService by lazy {
        retrofit.create(CurrentCountryApiService::class.java)
    }
}

object LocationRetrofitObject{
    val locationRetrofitService: LocationApiService by lazy {
        locationRetrofit.create(LocationApiService::class.java)
    }
}