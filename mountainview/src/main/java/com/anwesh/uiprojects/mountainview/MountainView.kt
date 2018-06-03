package com.anwesh.uiprojects.mountainview

/**
 * Created by anweshmishra on 04/06/18.
 */

import android.view.View
import android.content.Context
import android.graphics.*
import android.view.MotionEvent

class MountainView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}