package com.example.firemissionlogger

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast


class StartActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        // Set up the navigation drawer
        setupNavigationDrawer(R.id.drawer_layout, R.id.nav_view, R.id.toolbar)

        // Reference to the callsign input
        val callsignInput = findViewById<EditText>(R.id.callsignInput)

        // Function to handle the intent with the callsign
        fun navigateToActivity(callsign: String, targetActivity: Class<*>) {
            if (callsign.isNotEmpty()) {
                val intent = Intent(this, targetActivity)
                intent.putExtra("CALLSIGN", callsign)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please enter a callsign", Toast.LENGTH_SHORT).show()
            }
        }

        // Button for FOX 1 Logger
        val fox1LoggerButton = findViewById<Button>(R.id.btnFox1Logger)
        fox1LoggerButton.setOnClickListener {
            val callsign = callsignInput.text.toString()
            navigateToActivity(callsign, Fox1LoggerActivity::class.java)
        }

        // Button for FOX 2 Logger (MainActivity)
        val fox2LoggerButton = findViewById<Button>(R.id.btnFox2Logger)
        fox2LoggerButton.setOnClickListener {
            val callsign = callsignInput.text.toString()
            navigateToActivity(callsign, MainActivity::class.java)
        }

        // Button for LOGS
        val logsButton = findViewById<Button>(R.id.btnLogs)
        logsButton.setOnClickListener {
            val callsign = callsignInput.text.toString()
            navigateToActivity(callsign, LogsActivity::class.java)
        }
    }
}
