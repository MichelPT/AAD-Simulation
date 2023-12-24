package com.dicoding.habitapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dicoding.habitapp.R
import com.dicoding.habitapp.ui.detail.DetailHabitActivity
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.HABIT_TITLE
import com.dicoding.habitapp.utils.NOTIFICATION_CHANNEL_ID

class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val habitId = inputData.getInt(HABIT_ID, 0)
    private val habitTitle = inputData.getString(HABIT_TITLE)
    private var contextNotification = ctx

    override fun doWork(): Result {
        val prefManager = androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val shouldNotify = prefManager.getBoolean(applicationContext.getString(R.string.pref_key_notify), false)

        //TODO 12 : If notification preference on, show notification with pending intent
        if (shouldNotify){
            val intent = Intent(applicationContext, DetailHabitActivity::class.java)

            intent.putExtra(HABIT_ID, habitId)

            val pendingIntent = TaskStackBuilder.create(applicationContext).run {
                addNextIntentWithParentStack(intent)

                getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
            }

            val notificationManager = contextNotification.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val notificationHabit =
                NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notifications)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(habitTitle)
                    .setContentText(contextNotification.getString(R.string.notify_content))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setColor(
                        ContextCompat.getColor(
                            applicationContext,
                            android.R.color.transparent
                        )
                    )

                    .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                    .setSound(sound)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    inputData.getString(NOTIFICATION_CHANNEL_ID),
                    NotificationManager.IMPORTANCE_HIGH
                )

                channel.enableVibration(true)
                channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)

                notificationHabit.setChannelId(NOTIFICATION_CHANNEL_ID)
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(1, notificationHabit.build())
        }
        return Result.success()
    }

}
