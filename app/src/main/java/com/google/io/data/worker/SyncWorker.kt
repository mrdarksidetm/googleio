package com.google.io.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.io.data.local.IoDatabase
import com.google.io.data.repository.MockIoRepository

/**
 * TASK 2: Background Synchronization (WorkManager)
 * This class is like a "Silent Assistant". 
 * It runs in the background to keep our app's data fresh.
 */
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    /**
     * This is the work that will happen in the background.
     * WorkManager is smart: if the internet goes out or the phone restarts, 
     * it will wait and try again later!
     */
    override suspend fun doWork(): Result {
        return try {
            // 1. Get our database and repository
            val database = IoDatabase.getDatabase(applicationContext)
            val repository = MockIoRepository(database.sessionDao())

            // 2. Fetch the latest data from the "internet" (our mock)
            // This updates the Room database automatically.
            repository.refreshSessions()

            // 3. Tell WorkManager we were successful!
            Result.success()
        } catch (e: Exception) {
            // If something went wrong, we tell WorkManager to try again later.
            Result.retry()
        }
    }
}
