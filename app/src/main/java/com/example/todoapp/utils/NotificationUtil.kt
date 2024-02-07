package com.example.todoapp.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.todoapp.R
import javax.inject.Inject

class NotificationUtil @Inject constructor(
    private val context: Context,
    private val channelId:String = "channelId",
    private val pendingIntent: PendingIntent? = null
) {

    fun create(
        title:String = "Deadline!",
        text:String = "You hava a task to complete for today"
    ): Notification {
        val channel =
            NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = context.getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(channel)
        return context.createNotification(title, text, channelId, pendingIntent)
    }

    private fun Context.createNotification(
        title: String,
        text: String,
        channelId: String,
        pendingIntent: PendingIntent?
    ) = Notification.Builder(this, channelId)
        .setSmallIcon(R.drawable.icon_save_white)
        .setContentTitle(title)
        .setContentText(text)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()

    companion object{
        @JvmStatic
        fun Context.getPendingIntent(activity:Class<*>):PendingIntent{
            val intent = Intent(this, activity)
            return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        }
    }
}