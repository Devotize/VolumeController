package com.example.volumecontroller.presenter

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.IdRes
import com.example.volumecontroller.R
import com.example.volumecontroller.ui.NotificationView
import com.example.volumecontroller.ui.VolumeActivity

class NotificationPresenter (private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(NotificationView.CHANNEL_ID, name, importance).apply {
                description = descriptionText
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun currentVolume(streamType: Int): Int {
        return VolumeActivity.audioManager.getStreamVolume(streamType)
    }

    fun maxVolume(streamType: Int): Int {
        return VolumeActivity.audioManager.getStreamMaxVolume(streamType)
    }


}