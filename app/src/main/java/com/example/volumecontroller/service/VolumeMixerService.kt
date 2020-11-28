package com.example.volumecontroller.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.volumecontroller.helper.Action
import com.example.volumecontroller.receiver.NotificationBroadcastReceiver
import com.example.volumecontroller.ui.NotificationView


class VolumeMixerService: Service() {

    private val binder = LocalBinder()
    val CHANNEL_ID = "VolumeServiceNotification"
    private lateinit var volumeNotification: NotificationView
    private lateinit var broadcastReceiver: NotificationBroadcastReceiver

    inner class LocalBinder : Binder() {
        fun getService(): VolumeMixerService = this@VolumeMixerService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }


    override fun onCreate() {
        super.onCreate()

        Log.d("BootReceiver", "Service binded")

        volumeNotification =
            NotificationView(applicationContext)
        broadcastReceiver =
            NotificationBroadcastReceiver()


        startForeground(volumeNotification.notificationId, volumeNotification.createNotification().build())
        registerVolumeReceiver()

    }

    private fun registerVolumeReceiver() {

        val filter = IntentFilter(Action().volumeMusicUp).apply {
            addAction(Action().volumeMusicDown)
            addAction(Action().volumeAlarmUp)
            addAction(Action().volumeAlarmDown)
            addAction(Action().volumeRingUp)
            addAction(Action().volumeRingDown)
            addAction("android.media.VOLUME_CHANGED_ACTION")
            addAction(Intent.ACTION_HEADSET_PLUG)
            addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        }

        applicationContext.registerReceiver(broadcastReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

}