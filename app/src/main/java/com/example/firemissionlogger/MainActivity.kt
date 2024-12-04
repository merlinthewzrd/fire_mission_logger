package com.example.firemissionlogger

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : BaseActivity() {

    private lateinit var targetNumber: EditText
    private lateinit var numRounds: EditText
    private lateinit var typeRounds: EditText
    private lateinit var unitToFire: EditText
    private lateinit var methodControl: EditText
    private lateinit var sheaf: EditText
    private lateinit var timeReceived: EditText
    private lateinit var timeSent: EditText
    private lateinit var gunsReady: EditText
    private lateinit var shot: EditText
    private lateinit var roundsComplete: EditText
    private lateinit var endOfMission: EditText
    private lateinit var notes: EditText
    private lateinit var charge: EditText
    private lateinit var fuze: EditText
    private lateinit var spinnerHowSent: Spinner
    private lateinit var spinnerRecalc: Spinner


    private val timeStampFormat = "HH:mm:ss"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupNavigationDrawer(R.id.drawer_layout, R.id.nav_view, R.id.toolbar)

        // Retrieve the callsign from the Intent
        intent.getStringExtra("CALLSIGN_KEY") ?: "default_callsign"
        // Initialize input fields
        targetNumber = findViewById(R.id.targetNumber)
        numRounds = findViewById(R.id.numRounds)
        typeRounds = findViewById(R.id.typeRounds)
        unitToFire = findViewById(R.id.unitToFire)
        methodControl = findViewById(R.id.methodControl)
        sheaf = findViewById(R.id.sheaf)
        notes = findViewById(R.id.notes)
        charge = findViewById(R.id.charge)
        fuze = findViewById(R.id.fuze)


        // Initialize timestamp EditTexts
        timeReceived = findViewById(R.id.timeReceived)
        timeSent = findViewById(R.id.timeSent)
        gunsReady = findViewById(R.id.gunsReady)
        shot = findViewById(R.id.shot)
        roundsComplete = findViewById(R.id.roundsComplete)
        endOfMission = findViewById(R.id.endOfMission)

        //Initialize spinners
        spinnerHowSent = findViewById(R.id.spinnerHowSent)
        spinnerRecalc = findViewById(R.id.spinnerRecalc)

        // Initialize buttons and set onClickListeners
        findViewById<Button>(R.id.btnTimeReceived).setOnClickListener { setTimeStamp(timeReceived) }
        findViewById<Button>(R.id.btnTimeSent).setOnClickListener { setTimeStamp(timeSent) }
        findViewById<Button>(R.id.btnGunsReady).setOnClickListener { setTimeStamp(gunsReady) }
        findViewById<Button>(R.id.btnShot).setOnClickListener { setTimeStamp(shot) }
        findViewById<Button>(R.id.btnRoundsComplete).setOnClickListener { setTimeStamp(roundsComplete) }
        findViewById<Button>(R.id.btnEndOfMission).setOnClickListener { setTimeStamp(endOfMission) }

        findViewById<Button>(R.id.btnSave).setOnClickListener { saveToCSV() }
        findViewById<Button>(R.id.btnClear).setOnClickListener { clearFields() }
    }

    private fun setTimeStamp(editText: EditText) {
        val currentTime = SimpleDateFormat(timeStampFormat, Locale.getDefault()).format(Date())
        editText.setText(currentTime)
    }

    private fun saveToCSV() {
        val target = targetNumber.text.toString()

        if (target.isEmpty()) {
            Toast.makeText(this, "Please enter the Target Number before saving.", Toast.LENGTH_SHORT).show()
            return
        }
        val howSent = spinnerHowSent.selectedItem.toString()
        val recalc = spinnerRecalc.selectedItem.toString()

        val data = "${target}," +
                "${numRounds.text}," +
                "${typeRounds.text}," +
                "${charge.text}, " +
                "${fuze.text}, " +
                "${unitToFire.text}," +
                "${methodControl.text}," +
                "${sheaf.text}," +
                "${timeReceived.text}," +
                "${timeSent.text}," +
                "${gunsReady.text}," +
                "${shot.text}," +
                "${roundsComplete.text}," +
                "${endOfMission.text}," +
                "${howSent}, " +
                "${recalc}, " +
                "${notes.text}\n"
        val callsign = intent.getStringExtra("CALLSIGN") ?: "default_callsign"
        val filename = "${callsign}_${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}.csv"

        val contentResolver = contentResolver

        // Check if the file already exists
        val existingFileUri = contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            arrayOf(MediaStore.MediaColumns._ID),
            "${MediaStore.MediaColumns.DISPLAY_NAME}=? AND ${MediaStore.MediaColumns.RELATIVE_PATH}=?",
            arrayOf(filename, "Documents/FireMissions/"),
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id)
            } else {
                null
            }
        }

        val uri = existingFileUri ?: contentResolver.insert(MediaStore.Files.getContentUri("external"), ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents/FireMissions/")
        })

        if (uri != null) {
            try {
                contentResolver.openOutputStream(uri, "wa")?.use { outputStream ->
                    writeDataToStream(outputStream, data, append = existingFileUri != null)
                }
                Toast.makeText(this, "Data saved to Documents/FireMissions", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this, "Failed to create file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun writeDataToStream(outputStream: OutputStream, data: String, append: Boolean) {
        outputStream.bufferedWriter().use { writer ->
            if (!append) {
                writer.append("Target Number,Number of Rounds,Type of Rounds,Charge,Fuze,Unit to Fire,Method of Control,Sheaf,Time Received,Time Sent,Guns Ready/Laid,Shot,Rounds Complete,End of Mission,How Sent,RECALC,Notes\n")
            }
            writer.append(data)
        }
    }

    private fun clearFields() {
        targetNumber.setText("")
        numRounds.setText("")
        typeRounds.setText("")
        unitToFire.setText("")
        methodControl.setText("")
        sheaf.setText("")
        timeReceived.setText("")
        timeSent.setText("")
        gunsReady.setText("")
        shot.setText("")
        roundsComplete.setText("")
        endOfMission.setText("")
        notes.setText("")

    }
}
