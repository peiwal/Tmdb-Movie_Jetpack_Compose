package petrov.ivan.tmdb.application

import android.app.Application
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import petrov.ivan.tmdb.R
import petrov.ivan.tmdb.components.DaggerTmdbComponents
import petrov.ivan.tmdb.components.TmdbComponents
import petrov.ivan.tmdb.workers.NotifyWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit

class TmdbApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        createNotificationChannel()
        sheduleNotify()
    }

    private val tmdbComponents: TmdbComponents by lazy(mode = LazyThreadSafetyMode.NONE) {
        DaggerTmdbComponents.builder()
            .build()
    }

    fun getTmdbComponent() : TmdbComponents {
        return tmdbComponents
    }

    private fun createNotificationChannel() {
        val name = getString(R.string.notification_channel_name)
        val descriptionText = getString(R.string.notification_channel_description)
        val importance = NotificationManagerCompat.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannelCompat.Builder(NOTIFICATION_CHANNEL_USAGE_APP_ID, importance)
            .setName(name)
            .setDescription(descriptionText)
            .build()

        NotificationManagerCompat.from(this).createNotificationChannel(mChannel)
    }

    private fun sheduleNotify() {
        val request = OneTimeWorkRequestBuilder<NotifyWorker>()
            .setInitialDelay(1, TimeUnit.DAYS)
            .addTag(NotifyWorker.TAG)
            .build()
        WorkManager.getInstance(this)
            .enqueueUniqueWork(NotifyWorker.TAG, ExistingWorkPolicy.REPLACE, request)
    }

    companion object {
        const val NOTIFICATION_CHANNEL_USAGE_APP_ID = "notify_usage_app"
    }
}