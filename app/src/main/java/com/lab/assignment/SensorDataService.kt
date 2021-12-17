package com.lab.assignment

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import android.os.Binder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.io.FileDescriptor

/**
 * Service for monitoring barometric pressure and temperature
 * It broadcasts these values on change
 */
class SensorDataService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var pressure: Sensor? = null
    private var temprature: Sensor? = null
    private var mPressureValue: Float = 0.0f
    private var mTemperatureValue: Float = 0.0f

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        pressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
        temprature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        sensorManager.registerListener(this, pressure, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, temprature, SensorManager.SENSOR_DELAY_NORMAL)

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
        return LocalBinder()
    }

    internal class LocalBinder : Binder() {
        fun getService(): SensorDataService {
            return SensorDataService()
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_PRESSURE) {
            mPressureValue = event.values[0]
        }
        if (event.sensor.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            mTemperatureValue = event.values[0]
        }
        // add values in intent for broadcasting
        var sensorIntent = Intent("sensorIntent")
        sensorIntent.putExtra("mPressureValue", mPressureValue.toString())
        sensorIntent.putExtra("mTemperatureValue", mTemperatureValue.toString())

        // broadcast intent
        LocalBroadcastManager.getInstance(this).sendBroadcast(sensorIntent)
    }

    // not necessary right now
    override fun onAccuracyChanged(sensor: Sensor?, i: Int) {

    }
}