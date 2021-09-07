/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package petrov.ivan.tmdb.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.app.NotificationManager

import android.app.Notification

import androidx.core.app.NotificationCompat

import android.app.PendingIntent

import android.content.Intent
import petrov.ivan.tmdb.MainActivity
import petrov.ivan.tmdb.R
import petrov.ivan.tmdb.application.TmdbApplication


class NotifyWorker(
        context: Context,
        workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        showNotification()
        return Result.success()
    }

    private fun showNotification() {
        val ctx = this.applicationContext
        val intent = Intent(ctx, MainActivity::class.java)
        val contentIntent =
            PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(ctx, TmdbApplication.NOTIFICATION_CHANNEL_USAGE_APP_ID)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentTitle(ctx.getString(R.string.notification_app_usage_title))
            .setSmallIcon(R.drawable.filmstrip)
            .setContentText(ctx.getString(R.string.notification_app_usage_content_text))
            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            .setContentIntent(contentIntent)


        val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, builder.build());
    }

    companion object {
        const val TAG = "NotifyWorker"
    }
}
