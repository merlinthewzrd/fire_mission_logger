package com.example.firemissionlogger

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class StartActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        // Reference to the callsign input
        val callsignInput = findViewById<EditText>(R.id.callsignInput)

        // Load the saved callsign from SharedPreferences
        val sharedPref = getSharedPreferences("FireMissionPrefs", Context.MODE_PRIVATE)
        val savedCallsign = sharedPref.getString("CALLSIGN", "")
        callsignInput.setText(savedCallsign) // Restore saved callsign if it exists

        // Function to handle the intent with the callsign
        fun navigateToActivity(callsign: String, targetActivity: Class<*>) {
            if (callsign.isNotEmpty()) {
                // Save the callsign to SharedPreferences
                with(sharedPref.edit()) {
                    putString("CALLSIGN", callsign)
                    apply()
                }

                // Navigate to the target activity
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
            navigateToActivity(callsign, Fox1Activity::class.java)
        }

        // Button for FOX 2 Logger
        val fox2LoggerButton = findViewById<Button>(R.id.btnFox2Logger)
        fox2LoggerButton.setOnClickListener {
            val callsign = callsignInput.text.toString()
            navigateToActivity(callsign, Fox2Activity::class.java)
        }

        // Button for LOGS
        val logsButton = findViewById<Button>(R.id.btnLogs)
        logsButton.setOnClickListener {
            val callsign = callsignInput.text.toString()
            navigateToActivity(callsign, LogsActivity::class.java)
        }
    }
}
