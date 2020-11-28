package com.example.volumecontroller.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import com.example.volumecontroller.helper.Action
import com.example.volumecontroller.presenter.VolumeActivityPresenter
import com.example.volumecontroller.ui.NotificationView
import com.example.volumecontroller.ui.VolumeActivity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

private const val TAG = "BroadcastReceiver"

open class NotificationBroadcastReceiver(): BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationView = NotificationView(context!!)
        val volumePresenter = VolumeActivityPresenter(context, VolumeActivity.seekBarList)

        goAsyncBroadcast(CoroutineScope(IO), IO) {
            val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            when (intent?.action) {
                "android.media.VOLUME_CHANGED_ACTION" -> {
                    Log.d(TAG, "VolumeChanged")
                    notificationView.refreshNotification()
                    volumePresenter.refreshAllSeekBars()
//                    VolumeActivity().volumePresenter.refreshAllSeekBars()
                }
                Intent.ACTION_HEADSET_PLUG -> {
                    notificationView.refreshNotification()
                }
                AudioManager.ACTION_AUDIO_BECOMING_NOISY -> {
                    notificationView.refreshNotification()
                }
                Action().volumeMusicUp -> {
                    Log.d(TAG, " I got my broadcast!")
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0)
                }
                Action().volumeMusicDown -> {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0)
                }
                Action().volumeRingUp -> {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_RAISE, 0)
                }
                Action().volumeRingDown -> {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_LOWER, 0)
                }
                Action().volumeAlarmUp -> {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_RAISE, 0)
                }
                Action().volumeAlarmDown -> {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_LOWER, 0)
                }
            }
        }

    }

    private fun BroadcastReceiver.goAsyncBroadcast(
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        block: suspend () -> Unit
    ) {
        val pendingResult = goAsync()
        coroutineScope.launch(dispatcher) {
            block()
            pendingResult.finish()
        }
    }

}