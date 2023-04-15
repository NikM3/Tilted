package com.nikm3.tilted

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import com.nikm3.tilted.databinding.ActivityMainBinding
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit


class MainActivity : Activity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gameScreen: AnimatedView
    private lateinit var playerScore: TextView

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // View binding
        gameScreen = binding.gameScreen
        playerScore = binding.scoreCounter

        // Instantiate sensors and initialize score
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager!!
            .getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        playerScore.text = getString(R.string.player_score, 0)
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

    /**
     * Tell the view what direction to move the puck.
     * It should slide in the direction that the phone is tilted.
     * Brute force the score by constantly rewriting it.
     */
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            gameScreen.updatePuck(
                event.values[0].toInt(),
                event.values[1].toInt()
            )
            playerScore.text = getString(R.string.player_score, gameScreen.score)
            if (gameScreen.konfettiTime) {
                confetti()
            }
        }
    }

    /**
     * Pop some confetti to congratulate the user
     */
    private fun confetti() {
        val party = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            position = Position.Relative(gameScreen.relativeX, gameScreen.relativeY),
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
        )
        val konfetti = findViewById<nl.dionsegijn.konfetti.xml.KonfettiView>(R.id.konfettiView)
        konfetti.start(party)
        gameScreen.konfettiTime = false
    }
}