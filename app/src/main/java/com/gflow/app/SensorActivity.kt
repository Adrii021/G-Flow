package com.gflow.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SensorActivity : AppCompatActivity() {

    private lateinit var tvX: TextView
    private lateinit var tvY: TextView
    private lateinit var tvZ: TextView

    // Recibe los datos enviados por el Servicio a través de Broadcasts
    private val sensorReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "SENSOR_DATA") {
                val x = intent.getFloatExtra("X", 0f)
                val y = intent.getFloatExtra("Y", 0f)
                val z = intent.getFloatExtra("Z", 0f)

                tvX.text = "X: %.2f".format(x)
                tvY.text = "Y: %.2f".format(y)
                tvZ.text = "Z: %.2f".format(z)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor)

        tvX = findViewById(R.id.tvX)
        tvY = findViewById(R.id.tvY)
        tvZ = findViewById(R.id.tvZ)
        val btnStop = findViewById<Button>(R.id.btnStop)

        btnStop.setOnClickListener {
            // Detener el servicio de envío
            val serviceIntent = Intent(this, SensorService::class.java)
            stopService(serviceIntent)
            
            // Cerrar esta actividad y volver a MainActivity
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Registrar el receiver para escuchar los datos del servicio
        val filter = IntentFilter("SENSOR_DATA")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(sensorReceiver, filter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(sensorReceiver, filter)
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(sensorReceiver)
    }
}
