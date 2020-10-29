package com.octagon_technologies.covid19_statistics_app.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.octagon_technologies.covid19_statistics_app.repo.Repo
import timber.log.Timber

class RefreshDataWork(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    private val repo = Repo(appContext)

    companion object {
        const val WORK_NAME = "RefreshWorkData"
    }

    override suspend fun doWork(): Result {
        return try {
            repo.getRemoteData()
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