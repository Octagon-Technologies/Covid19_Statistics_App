package com.octagon_technologies.covid19_statistics_app.shared_code

import com.octagon_technologies.covid19_statistics_app.database.CurrentDatabaseClass
import com.octagon_technologies.covid19_statistics_app.database.MainDataBase
import com.octagon_technologies.covid19_statistics_app.network.CurrentCountryObject
import com.octagon_technologies.covid19_statistics_app.network.currentCountry.CurrentCountry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import retrofit2.HttpException
import timber.log.Timber
import java.net.UnknownHostException

object MainCurrentCountryItem {

    suspend fun getCurrentCountryAsync(
        mainDataBase: MainDataBase?,
        location: String?
    ): ArrayList<CurrentCountry>? {
        return withContext(Dispatchers.Default) {
            try {
                Timber.d("location is $location")
                val listOfCurrentCountry =
                    CurrentCountryObject.currentRetrofitService.getCurrentCountryAsync(
                        location!!
                    ).await().sortedByDescending {
                        DateTime(it.Date).toLocalDate().toDate().time
                    }

                insertCurrentCountryToDatabase(mainDataBase, listOfCurrentCountry)

                listOfCurrentCountry.toList().toMutableList() as ArrayList<CurrentCountry>
            }  catch (uhe: UnknownHostException) {
                Timber.e(uhe)
                getLocalCurrentCountryAsync(mainDataBase)
            }catch (npe: NullPointerException) {
                getLocalCurrentCountryAsync(mainDataBase)
            } catch (e: HttpException) {
                Timber.e(e)
                getLocalCurrentCountryAsync(mainDataBase)
            } catch (t: Throwable) {
                Timber.e(t)
                throw t
            }
        }
    }

    private suspend fun insertCurrentCountryToDatabase(
        mainDatabase: MainDataBase?,
        listOfCurrentCountry: List<CurrentCountry>
    ) {
        withContext(Dispatchers.IO) {
            val currentDatabaseClass =
                CurrentDatabaseClass(listOfCurrentCountry = listOfCurrentCountry)
            mainDatabase?.currentDao?.insertCurrentDataClass(currentDatabaseClass)
        }
    }

    private suspend fun getLocalCurrentCountryAsync(
        mainDatabase: MainDataBase?
    ): ArrayList<CurrentCountry>? {
        return withContext(Dispatchers.IO) { mainDatabase?.currentDao?.getCurrentDataClass()?.listOfCurrentCountry as ArrayList<CurrentCountry>? }
    }
}