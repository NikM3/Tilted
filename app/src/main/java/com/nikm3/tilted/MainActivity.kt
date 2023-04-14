package com.nikm3.tilted

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import kotlin.random.Random


class MainActivity : Activity(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    var animatedView: AnimatedView? = null
    var puckDrawable = ShapeDrawable()
    var goalDrawable = ShapeDrawable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_main);


        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        max_x = displayMetrics.widthPixels
        max_y = displayMetrics.heightPixels
        puckX = max_x / 2
        puckY = max_y / 2

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager!!
            .getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        animatedView = AnimatedView(this)
        setContentView(animatedView)
    }

    override fun onResume() {
        super.onResume()
        sensorManager!!.registerListener(
            this, accelerometer,
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(this)
    }

    override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {
        /* Intentionally left blank */
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            puckX -= event.values[0].toInt()
            puckY += event.values[1].toInt()
        }
    }

    inner class AnimatedView(context: Context?) : View(context) {
        init {
            goalDrawable = ShapeDrawable(OvalShape())
            puckDrawable = ShapeDrawable(OvalShape())
            puckDrawable.paint.color = -0x53dd
            puckDrawable.setBounds(
                puckX,
                puckY,
                puckX + Companion.puckWidth,
                puckY + Companion.puckHeight
            )
            newGoal()
        }

        override fun onDraw(canvas: Canvas) {
            makeGoal(canvas)
            checkBounds()
            puckDrawable.setBounds(
                puckX,
                puckY,
                puckX + Companion.puckWidth,
                puckY + Companion.puckHeight
            )
            puckDrawable.draw(canvas)
            checkGoal()
            invalidate()
        }

        private fun newGoal() {
            goalX = Random.nextInt(1, max_x - goalWidth - 1)
            goalY = Random.nextInt(1, max_y - goalHeight - 1)
        }

        private fun checkGoal() {
            val puckTop = puckY
            val puckBot = puckY + Companion.puckHeight
            val puckLeft = puckX
            val puckRight = puckX + Companion.puckWidth
            val goalTop = goalY
            val goalBot = goalY + Companion.goalHeight
            val goalLeft = goalX
            val goalRight = goalX + Companion.goalWidth
            if (puckLeft > goalLeft &&
                puckRight < goalRight &&
                puckTop > goalTop &&
                puckBot < goalBot
            )
                newGoal()
        }

        private fun makeGoal(canvas: Canvas) {
            goalDrawable.setBounds(
                goalX,
                goalY,
                goalX + Companion.goalWidth,
                goalY + Companion.goalHeight
            )
            goalDrawable.draw(canvas)
            invalidate()
        }

        private fun checkBounds() {
            val puckTop = puckY
            val puckBot = puckY + Companion.puckHeight
            val puckLeft = puckX
            val puckRight = puckX + Companion.puckWidth
            if (puckLeft < x) {
                puckX = x.toInt()
            } else if (puckRight > width) {
                puckX = width - Companion.puckWidth
            }
            if (puckTop < y) {
                puckY = y.toInt()
            } else if (puckBot > height) {
                puckY = height - Companion.puckHeight
            }
        }
    }

    companion object {
        const val puckWidth = 50
        const val puckHeight = 50
        var puckX: Int = 0
        var puckY: Int = 0
        var max_x: Int = 0
        var max_y: Int = 0
        var goalX: Int = 0
        var goalY: Int = 0
        var goalWidth: Int = puckWidth * 4
        var goalHeight: Int = puckHeight * 4
    }
}