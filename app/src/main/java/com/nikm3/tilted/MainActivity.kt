package com.nikm3.tilted

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import com.nikm3.tilted.databinding.ActivityMainBinding


class MainActivity : Activity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var gameScreen: AnimatedView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager!!
            .getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gameScreen = binding.gameScreen
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
            gameScreen?.updatePuck(event.values[0].toInt(), event.values[1].toInt())
            gameScreen?.invalidate()
        }
    }
}