package com.nikm3.tilted

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.Display
import android.view.Surface
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.rotationMatrix
import com.nikm3.tilted.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var display: Display
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

        // Prepare to lock orientation
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        display = windowManager.defaultDisplay
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
            var rotationMatrixAdjusted = FloatArray(9)
            when (display.rotation) {
                Surface.ROTATION_0 -> {
                    rotationMatrixAdjusted = rotationMatrix.clone();
                }
                Surface.ROTATION_90 -> {
                    SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X,
                        rotationMatrixAdjusted);
                }
                Surface.ROTATION_180 -> {
                    SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y,
                        rotationMatrixAdjusted);
                }
                Surface.ROTATION_270 -> {
                    SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X,
                        rotationMatrixAdjusted);
                }
            }
            val orientationValues = FloatArray(3)
            if (rotationOK) {SensorManager.getOrientation(rotationMatrixAdjusted,orientationValues)}
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