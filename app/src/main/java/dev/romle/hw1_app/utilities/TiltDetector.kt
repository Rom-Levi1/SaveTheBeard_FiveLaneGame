package dev.romle.hw1_app.utilities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dev.romle.hw1_app.interfaces.TiltCallback
import kotlin.math.abs

class TiltDetector(context: Context, private var tiltCallback: TiltCallback?) {

    private val sensorManager = context
        .getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val sensor = sensorManager
        .getDefaultSensor(Sensor.TYPE_ACCELEROMETER) as Sensor

    private lateinit var sensorEventListener: SensorEventListener

    var tiltCounterX: Int = 0 // no tilt
        private set

    private var timestamp: Long = 0L

    init {
        initEventListener()
    }

    private fun initEventListener() {
        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                calculateTilt(x)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // pass
            }

        }
    }

    private fun calculateTilt(x: Float) {
        if (System.currentTimeMillis() - timestamp >= 500) {
            timestamp = System.currentTimeMillis()
            if (x >= 3.0) {
                tiltCounterX = 1 // tilt right
                tiltCallback?.tiltX()
            }
            else if (x <= -3.0){
                tiltCounterX = -1 // tilt left
                tiltCallback?.tiltX()
            }
            else {
                tiltCounterX = 0 // no tilt
                tiltCallback?.tiltX()
            }

        }
    }

    fun start() {
        sensorManager
            .registerListener(
                sensorEventListener,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
    }

    fun stop() {
        sensorManager
            .unregisterListener(
                sensorEventListener,
                sensor
            )
    }
}