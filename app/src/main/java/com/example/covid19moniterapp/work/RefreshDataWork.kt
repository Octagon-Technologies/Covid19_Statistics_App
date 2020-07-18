package com.example.covid19moniterapp.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.covid19moniterapp.database.DataBase
import com.example.covid19moniterapp.repo.Repo
import timber.log.Timber

class RefreshDataWork(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    val db = DataBase.getInstance(appContext)
    val repo = Repo(database = db!!)

    companion object {
        const val WORK_NAME = "RefreshWorkData"
    }

    override suspend fun doWork(): Result {
        return try {
            repo.searchData()
            repo.refreshData()
            Timber.i("doWork: Work done successfully")
            Result.success()
        } catch (largeData: OutOfMemoryError){
            Timber.i("doWork: Work failed because loaded data was to large")
            Result.failure()
        } catch (e: Exception){
            Timber.i("Due to $e, work will retry")
            Result.retry()
        }
    }
}