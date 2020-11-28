package com.example.volumecontroller.helper

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import java.lang.Math.abs

class ResizeAnimation(private val view: View): Animation() {

    private var deltaHeight: Int = 0 // distance between start and end height
    private var startHeight: Int = 0
    private var endHeight: Int = 0


    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        view.layoutParams.height = (startHeight + deltaHeight * interpolatedTime).toInt()


        view.requestLayout()
    }

    fun setParams(startHeight: Int, endHeight: Int) {
        this.startHeight = startHeight
        this.endHeight = endHeight
        deltaHeight = kotlin.math.abs(endHeight) - kotlin.math.abs(startHeight)
    }

    override fun setDuration(durationMillis: Long) {
        super.setDuration(durationMillis)
    }

    override fun willChangeBounds(): Boolean {
        return true
    }

}