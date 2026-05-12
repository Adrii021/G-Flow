package com.gflow.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SensorService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    // ⚠️ IMPORTANTE: CAMBIA ESTA IP POR LA IP LOCAL DE TU PC ⚠️
    // ⚠️ IMPORTANTE: Esta es la IP real de tu ordenador en tu red Wi-Fi/Ethernet actual
    private val SERVER_URL = "ws://192.168.1.50:8000/ws/sensor"

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        
        // Configurar Sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        
        // Configurar WebSocket
        connectWebSocket()
    }

    private fun connectWebSocket() {
        val request = Request.Builder().url(SERVER_URL).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("WebSocket Conectado")
            }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("Error de WebSocket: ${t.message}")
            }
        })
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // 1. Enviar datos por Broadcast para que los vea SensorActivity
            val broadcastIntent = Intent("SENSOR_DATA")
            broadcastIntent.setPackage(packageName)
            broadcastIntent.putExtra("X", x)
            broadcastIntent.putExtra("Y", y)
            broadcastIntent.putExtra("Z", z)
            sendBroadcast(broadcastIntent)

            // 2. Enviar datos al Servidor por WebSocket
            val json = JSONObject()
            json.put("x", x)
            json.put("y", y)
            json.put("z", z)
            
            webSocket?.send(json.toString())
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun startForegroundService() {
        val channelId = "gflow_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "G-Flow Sensor Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("G-Flow Activo")
            .setContentText("Transmitiendo datos del acelerómetro...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(1, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        webSocket?.close(1000, "App Detenida")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
