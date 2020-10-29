package com.octagon_technologies.covid19_statistics_app.shared_code

import com.octagon_technologies.covid19_statistics_app.database.GlobalDatabaseClass
import com.octagon_technologies.covid19_statistics_app.database.MainDataBase
import com.octagon_technologies.covid19_statistics_app.network.GlobalRetrofitObject
import com.octagon_technologies.covid19_statistics_app.network.allCountries.Global
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import java.net.UnknownHostException

object MainGlobalItem {

    suspend fun getGlobalAsync(
        mainDataBase: MainDataBase?
    ): Global? {
        return withContext(Dispatchers.Default) {
            try {
                val remoteGlobal =
                    GlobalRetrofitObject.globalRetrofitService.getGlobalAsync().await()
                insertGlobalToDatabase(mainDataBase, remoteGlobal)

                remoteGlobal
            }
            catch (uhe: UnknownHostException) {
                Timber.e(uhe, "No internet")
                getLocalGlobalAsync(mainDataBase)
            } catch (e: HttpException) {
                Timber.e(e, "Server error.")
                getLocalGlobalAsync(mainDataBase)
            } catch (t: Throwable) {
                Timber.e(t)
                throw t
            }
        }
    }

    private suspend fun insertGlobalToDatabase(
        mainDatabase: MainDataBase?,
        global: Global
    ) {
        withContext(Dispatchers.IO) {
            val globalDatabaseClass =
                GlobalDatabaseClass(global = global)
            mainDatabase?.globalDao?.insertGlobalClass(globalDatabaseClass)
        }
    }

    private suspend fun getLocalGlobalAsync(
        mainDatabase: MainDataBase?
    ): Global? {
        return withContext(Dispatchers.IO) { mainDatabase?.globalDao?.getGlobalClass()?.global }
    }

}