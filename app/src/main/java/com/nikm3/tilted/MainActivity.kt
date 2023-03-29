package com.nikm3.tilted

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.rotationMatrix
import com.nikm3.tilted.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var sensorAccelerometer: Sensor
    private lateinit var sensorMagnetometer: Sensor
    private lateinit var playerDirection: TextView
    private lateinit var playerPitch: TextView
    private lateinit var playerRoll: TextView
    private var accelerometerData = FloatArray(3)
    private var magnetometerData = FloatArray(3)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set sensors
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    override fun onStart() {
        super.onStart()

        // Set binding
        val goalDirection = binding.goalDirection
        val goalPitch = binding.goalPitch
        val goalRoll = binding.goalRoll
        playerDirection = binding.playerDirection
        playerPitch = binding.playerPitch
        playerRoll = binding.playerRoll

        sensorManager.registerListener(
            this, sensorAccelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            this, sensorMagnetometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onStop() {
        super.onStop()
        sensorManager.unregisterListener(this)
    }
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    accelerometerData = event.values.clone()
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    magnetometerData = event.values.clone()
                }
                else -> {
                    return
                }
            }
            val rotationMatrix = FloatArray(9)
            val rotationOK = SensorManager.getRotationMatrix(
                rotationMatrix,
                null,
                accelerometerData,
                magnetometerData
            )
            val orientationValues = FloatArray(3)
            if (rotationOK) {SensorManager.getOrientation(rotationMatrix,orientationValues)}
            val direction = orientationValues[0]
            val pitch = orientationValues[1]
            val roll = orientationValues[2]

            if (this::playerDirection.isInitialized)
                playerDirection.text = getString(R.string.sensor_format, direction)
            if (this::playerPitch.isInitialized)
                playerPitch.text = getString(R.string.sensor_format, pitch)
            if (this::playerRoll.isInitialized)
                playerRoll.text = getString(R.string.sensor_format, roll)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        /* Intentionally left blank */
    }
}