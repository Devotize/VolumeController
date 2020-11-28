package com.example.volumecontroller.presenter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.media.AudioManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.SwitchCompat
import com.example.volumecontroller.helper.ResizeAnimation
import com.example.volumecontroller.ui.NotificationView
import com.example.volumecontroller.ui.VolumeActivity
import kotlinx.android.synthetic.main.activity_volume_layout.*

class VolumeActivityPresenter(private val context: Context, private val seekBarList: ArrayList<Pair<AppCompatSeekBar, Int>>) {

    companion object{
        const val TAG = "VolumeActivityPresenter"
        const val VOLUME_SHARED_PREFERENCES = "VOLUME_SHARED_PREFERENCES"
        private const val EXTENDED_SWITCH = "EXTENDED_SWITCH"
    }
    private val audioManager = VolumeActivity.audioManager
    private val sharedPreferences = context.getSharedPreferences(VOLUME_SHARED_PREFERENCES, MODE_PRIVATE)


    private fun refreshSeekBar(seekBar: SeekBar, streamType: Int) {
        val currentValueOfStreamType = audioManager.getStreamVolume(streamType)
        seekBar.progress = currentValueOfStreamType

    }

    fun refreshAllSeekBars() {
//        Toast.makeText(context, "${VolumeActivity().seekBarList}", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "SeekBarList: ${seekBarList.first()}")
        seekBarList.forEach {
            refreshSeekBar(it.first, it.second)
            connectWithStreamType(it.first, it.second)
            it.first.max = NotificationView(context).maxVolume(it.second)
        }
    }

    private fun connectWithStreamType(seekBar: SeekBar, streamType: Int) {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                audioManager.setStreamVolume(streamType, progress, 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    refreshSeekBar(seekBar, streamType)
                }
            }

        })
    }

    fun saveSwitchState(switch: SwitchCompat) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(EXTENDED_SWITCH, switch.isChecked)
        editor.apply()
    }

    fun getSwitchState(switch: SwitchCompat): Boolean {
        return sharedPreferences.getBoolean(EXTENDED_SWITCH, false)
    }

    fun animateExtendedVolumeSettings(sheet: View, isShown: Boolean) {
//        val animatorSet = AnimatorSet()
//        val height = sheet.height
//        animatorSet.removeAllListeners()
//        animatorSet.end()
//        animatorSet.cancel()
//
//        val animator = ObjectAnimator.ofFloat(sheet, "translationY", (if (isShown) height else 0).toFloat())
//        animator.duration = 500
//        animatorSet.play(animator)
//        animatorSet.start()

        val resizeAnimation = ResizeAnimation(sheet)
        resizeAnimation.duration = 500

        if (isShown) {
            sheet.visibility = View.VISIBLE
            resizeAnimation.setParams(0, VolumeActivity.heightOfExtendSheet)
            Log.d(TAG, "Match_Parent: ${LinearLayout.LayoutParams.MATCH_PARENT}")
        } else {
            resizeAnimation.setParams(VolumeActivity.heightOfExtendSheet, 0)
//            sheet.visibility = View.GONE
        }

        sheet.startAnimation(resizeAnimation)

    }

    fun initExtendVolumeLayout(switch: SwitchCompat, extendLayout: View) {
        if (getSwitchState(switch)) {
//            extendLayout.layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT
            extendLayout.visibility = View.VISIBLE
            Log.d(TAG, "Switch state: True")
        } else {
//            extendLayout.layoutParams.height = 0
            extendLayout.visibility = View.GONE
        }
    }

    fun hideOrShowExtendLayout(isChecked: Boolean, extendLayout: View) {
        if (isChecked) {
//            extendLayout.visibility = View.VISIBLE
            extendLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        } else {
//            extendLayout.visibility = View.GONE
            extendLayout.layoutParams = LinearLayout.LayoutParams(0, 0)
        }
    }

}