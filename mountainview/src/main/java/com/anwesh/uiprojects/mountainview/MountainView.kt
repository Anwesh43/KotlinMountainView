package com.anwesh.uiprojects.mountainview

/**
 * Created by anweshmishra on 04/06/18.
 */

import android.view.View
import android.content.Context
import android.graphics.*
import android.view.MotionEvent

val MOUNTAIN_NODES : Int = 5


class MountainView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var prevScale : Float = 0f, var dir : Float = 0f) {

        fun update(stopcb : (Float) -> Unit) {
            scale += dir * 0.1f
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                stopcb(prevScale)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch (ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class MountainNode(var i : Int, val state : State = State()) {

        private var next : MountainNode? = null

        private var prev : MountainNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < MOUNTAIN_NODES - 1) {
                next = MountainNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            paint.color = Color.parseColor("#f39c12")
            prev?.draw(canvas, paint)
            val gap : Float = canvas.width.toFloat()/(MOUNTAIN_NODES)
            val h : Float = canvas.height.toFloat()
            canvas.save()
            canvas.translate(i * gap, h + (h/20) * (1 - state.scale))
            val path : Path = Path()
            path.moveTo(0f, 0f)
            path.lineTo(gap/10, -h/20)
            path.lineTo(0.9f * gap, -h/20)
            path.lineTo(gap, 0f)
            canvas.drawPath(path, paint)
            canvas.restore()
        }

        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : MountainNode {
            var curr : MountainNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class MountainList (var i : Int) {

        private var curr : MountainNode = MountainNode(0)

        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(stopcb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                stopcb(it)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            curr.startUpdating(startcb)
        }
    }

    data class Renderer(var view : MountainView) {

        private val animator : Animator = Animator(view)

        private val mountainList : MountainList = MountainList(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            mountainList.draw(canvas, paint)
            animator.animate {
                mountainList.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            mountainList.startUpdating {
                animator.start()
            }
        }
    }
}