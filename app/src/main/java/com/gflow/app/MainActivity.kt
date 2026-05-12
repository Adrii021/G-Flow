package com.gflow.app

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnStart = findViewById<Button>(R.id.btnStart)
        
        btnStart.setOnClickListener {
            // Iniciar el servicio en segundo plano que envía datos
            val serviceIntent = Intent(this, SensorService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }

            // Abrir la segunda actividad para mostrar los datos
            val intent = Intent(this, SensorActivity::class.java)
            startActivity(intent)
        }
    }
}
