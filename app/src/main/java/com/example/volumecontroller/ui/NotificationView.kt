package com.example.volumecontroller.ui

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.widget.RemoteViews
import androidx.annotation.IdRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.volumecontroller.helper.Action
import com.example.volumecontroller.R
import com.example.volumecontroller.presenter.NotificationPresenter

class NotificationView(private val context: Context) {

    private var notificationLayoutSmall: RemoteViews? = null
    private var notificationLayoutExpanded: RemoteViews? = null
    private var notification: NotificationCompat.Builder = createNotification()
    private var notificationPresenter = NotificationPresenter(context)
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val notificationId: Int = 1

    companion object {
        const val CHANNEL_ID = "volume_notification_channel_id"
        const val TAG = "NotificationVolume"
    }

    fun createNotification(): NotificationCompat.Builder {

        notificationLayoutSmall = RemoteViews(context.packageName,
            R.layout.notification_small
        ).apply {
            setOnClickVolumeBoost(R.id.volume_media_up_small, Action().volumeMusicUp)
            setOnClickVolumeBoost(R.id.volume_media_down_small, Action().volumeMusicDown)
            setProgressBar(R.id.volume_media_progress_bar_small,
                maxVolume(AudioManager.STREAM_MUSIC),
                currentVolume(AudioManager.STREAM_MUSIC),
                false)
        }
        notificationLayoutExpanded =
            RemoteViews(context.packageName,
                R.layout.notification_expanded
            ).apply {
                //media progress bar
                setOnClickVolumeBoost(R.id.volume_media_up, Action().volumeMusicUp)
                setOnClickVolumeBoost(R.id.volume_media_down, Action().volumeMusicDown)
                setProgressBar(R.id.volume_media_progress_bar,
                    maxVolume(AudioManager.STREAM_MUSIC),
                    currentVolume(AudioManager.STREAM_MUSIC),
                    false)
                //ring progress bar
                setOnClickVolumeBoost(R.id.volume_ring_up, Action().volumeRingUp)
                setOnClickVolumeBoost(R.id.volume_ring_down, Action().volumeRingDown)
                setProgressBar(R.id.volume_ring_progress_bar,
                    maxVolume(AudioManager.STREAM_RING),
                    currentVolume(AudioManager.STREAM_RING),
                    false)
                //alarm progress bar
                setOnClickVolumeBoost(R.id.volume_alarm_up, Action().volumeAlarmUp)
                setOnClickVolumeBoost(R.id.volume_alarm_down, Action().volumeAlarmDown)
                setProgressBar(R.id.volume_alarm_progress_bar,
                    maxVolume(AudioManager.STREAM_ALARM),
                    currentVolume(AudioManager.STREAM_ALARM),
                    false)
            }
        NotificationPresenter(context).createNotificationChannel()

        return NotificationCompat.Builder(context,
            CHANNEL_ID
        ).apply {
            setSmallIcon(R.drawable.ic_volume_up_24)
            setStyle(NotificationCompat.DecoratedCustomViewStyle())
            setCustomContentView(notificationLayoutSmall)
            setStyle(null)
            setOnlyAlertOnce(true)
            setOngoing(true)
            setAutoCancel(false)
            setCustomBigContentView(notificationLayoutExpanded)
            priority = NotificationCompat.PRIORITY_LOW

        }
    }


    private fun RemoteViews.setOnClickVolumeBoost(@IdRes buttonId: Int, action: String) {
        val intent = Intent(action)
        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        setOnClickPendingIntent(buttonId, pendingIntent)
    }

    fun currentVolume(streamType: Int): Int {
        return VolumeActivity.audioManager.getStreamVolume(streamType)
    }

    fun maxVolume(streamType: Int): Int {
        return VolumeActivity.audioManager.getStreamMaxVolume(streamType)
    }

    operator fun invoke() {
        refreshNotification()
    }

    fun refreshNotification() {
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, notification.build())
        }
    }
}
