package com.dicoding.todoapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dicoding.todoapp.R
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.data.TaskRepository
import com.dicoding.todoapp.ui.detail.DetailTaskActivity
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.NOTIFICATION_CHANNEL_ID
import com.dicoding.todoapp.utils.TASK_ID

class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val channelName = inputData.getString(NOTIFICATION_CHANNEL_ID)

    private fun getPendingIntent(task: Task): PendingIntent? {
        val intent = Intent(applicationContext, DetailTaskActivity::class.java).apply {
            putExtra(TASK_ID, task.id)
        }
        return TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }
    }

    override fun doWork(): Result {
        //TODO 14 : If notification preference on, get nearest active task from repository and show notification with pending intent
        val taskRepository = TaskRepository.getInstance(applicationContext)
        val task = taskRepository.getNearestActiveTask()
        val contentText = applicationContext.resources.getString(
            R.string.notify_content,
            DateConverter.convertMillisToString(task.dueDateMillis)
        )
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(applicationContext, channelName ?: "Notification")
            .setContentTitle(task.title)
            .setContentText(contentText)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_notifications)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(getPendingIntent(task))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nChannel = NotificationChannel(
                channelName,
                "channelName",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            builder.setChannelId(channelName?:"Notification")
            notificationManager.createNotificationChannel(nChannel)
        }

        val notification = builder.build()
        notificationManager.notify(100, notification)
        return Result.success()
    }

}
