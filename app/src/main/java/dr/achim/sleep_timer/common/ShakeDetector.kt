package dr.achim.sleep_timer.common

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlin.math.sqrt

class ShakeDetector(private val onShakeDetected: () -> Unit) : SensorEventListener {
    private var lastCrossingTime: Long = 0L
    private var lastExtensionTime: Long = 0L

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0] / SensorManager.GRAVITY_EARTH
        val y = event.values[1] / SensorManager.GRAVITY_EARTH
        val z = event.values[2] / SensorManager.GRAVITY_EARTH
        val gForce = sqrt(x * x + y * y + z * z)

        if (gForce <= SHAKE_THRESHOLD) return

        val now = System.currentTimeMillis()

        // debounce: ignore if this is just the tail of the same shake
        if (now - lastCrossingTime < SLOP_MS) return
        lastCrossingTime = now

        // cooldown: only extend the timer once per shake session
        if (now - lastExtensionTime < COOLDOWN_MS) return
        lastExtensionTime = now

        onShakeDetected()
        Log.d(TAG, "Shake detected: $gForce")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    companion object {
        private const val SHAKE_THRESHOLD: Float = 3f   // g-force
        private const val SLOP_MS: Long = 500L          // debounce a single shake event
        private const val COOLDOWN_MS: Long = 2_000L    // max one extension per session
    }
}