package eu.kanade.domain.track.service

import android.content.Context
import android.content.SharedPreferences
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import eu.kanade.tachiyomi.util.system.workManager

class DelayedTrackingUpdateJob(private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return if (clearLegacyTrackingQueue(context.legacyTrackingQueue())) {
            Result.success()
        } else {
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "DelayedTrackingUpdate"

        fun scheduleCleanup(context: Context) {
            if (context.legacyTrackingQueue().all.isEmpty()) return

            val request = OneTimeWorkRequestBuilder<DelayedTrackingUpdateJob>()
                .addTag(TAG)
                .build()
            context.workManager.enqueueUniqueWork(TAG, ExistingWorkPolicy.REPLACE, request)
        }
    }
}

internal fun clearLegacyTrackingQueue(preferences: SharedPreferences): Boolean {
    return preferences.edit().clear().commit()
}

private fun Context.legacyTrackingQueue(): SharedPreferences {
    return getSharedPreferences("tracking_queue", Context.MODE_PRIVATE)
}
