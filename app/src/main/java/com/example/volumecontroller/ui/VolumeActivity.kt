package com.example.volumecontroller.ui

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatSeekBar
import com.example.volumecontroller.receiver.NotificationBroadcastReceiver
import com.example.volumecontroller.R
import com.example.volumecontroller.presenter.VolumeActivityPresenter
import com.example.volumecontroller.service.VolumeMixerService
import kotlinx.android.synthetic.main.activity_volume_layout.*

class VolumeActivity : AppCompatActivity() {

    companion object {
        const val TAG = "VolumeActivity"
        val seekBarList = arrayListOf<Pair<AppCompatSeekBar, Int>>()
        lateinit var audioManager: AudioManager
        var heightOfExtendSheet: Int = 0
    }

    lateinit var volumePresenter: VolumeActivityPresenter
    private lateinit var notificationVolume: NotificationView
    private lateinit var myNotificationReceiver: NotificationBroadcastReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volume_layout)

        startForegroundVolumeMixerService()
        audioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        volumePresenter = VolumeActivityPresenter(applicationContext, seekBarList)

        addSeekBarsToList()

        extend_volume_switch.apply {
            isChecked = volumePresenter.getSwitchState(this)
            setOnCheckedChangeListener { buttonView, isChecked ->
                volumePresenter.animateExtendedVolumeSettings(extend_volume_sheet, isChecked)
//                volumePresenter.hideOrShowExtendLayout(isChecked, extend_volume_sheet)
                volumePresenter.saveSwitchState(this)
            }
        }


        extend_volume_sheet.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                extend_volume_sheet.viewTreeObserver.removeOnGlobalLayoutListener(this)
                heightOfExtendSheet = extend_volume_sheet.height
                Log.d(TAG, "Expand sheet height with three observer: ${heightOfExtendSheet}}")
                volumePresenter.initExtendVolumeLayout(extend_volume_switch, extend_volume_sheet)

            }

        })


        Log.d(TAG, "Expand sheet height: ${extend_volume_sheet.height}}")
        Log.d(TAG, "Other Settings layout: ${other_settings_layout.height}}")


        myNotificationReceiver =
            NotificationBroadcastReceiver()

        volumePresenter.refreshAllSeekBars()

    }

    private fun startForegroundVolumeMixerService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceIntent = Intent(applicationContext, VolumeMixerService::class.java)
            applicationContext.startForegroundService(serviceIntent)
        }

    }
    
    private fun addSeekBarsToList() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBarList.addAll(
                listOf(
                    Pair(media_seek_bar, AudioManager.STREAM_MUSIC),
                    Pair(ring_seek_bar, AudioManager.STREAM_RING),
                    Pair(alarm_seek_bar, AudioManager.STREAM_ALARM),
                    Pair(voice_seek_bar, AudioManager.STREAM_VOICE_CALL),
                    Pair(notification_seek_bar, AudioManager.STREAM_NOTIFICATION),
                    Pair(system_seek_bar, AudioManager.STREAM_SYSTEM),
                    Pair(DTMF_seek_bar, AudioManager.STREAM_DTMF),
                    Pair(accessibility_seek_bar, AudioManager.STREAM_ACCESSIBILITY)
                )
            )
        } else {
            seekBarList.addAll(
                listOf(
                    Pair(media_seek_bar, AudioManager.STREAM_MUSIC),
                    Pair(ring_seek_bar, AudioManager.STREAM_RING),
                    Pair(alarm_seek_bar, AudioManager.STREAM_ALARM),
                    Pair(voice_seek_bar, AudioManager.STREAM_VOICE_CALL),
                    Pair(notification_seek_bar, AudioManager.STREAM_NOTIFICATION),
                    Pair(system_seek_bar, AudioManager.STREAM_SYSTEM),
                    Pair(DTMF_seek_bar, AudioManager.STREAM_DTMF)
                )
            )
        }
    }


}