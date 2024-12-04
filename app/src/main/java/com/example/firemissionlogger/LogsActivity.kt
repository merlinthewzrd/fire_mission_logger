package com.example.firemissionlogger

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.widget.*
import androidx.appcompat.widget.Toolbar

@Suppress("DEPRECATION")
class LogsActivity : BaseActivity() {

    private val REQUEST_CODE_OPEN_DIRECTORY = 100
    private var directoryUri: Uri? = null
    private val csvFileList = mutableListOf<Pair<String, Uri>>()  // List to hold file names and URIs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logs)

        // Set up the Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Logs"

        // Set up the Navigation Drawer
        setupNavigationDrawer(R.id.drawer_layout, R.id.nav_view, R.id.toolbar)

        // Reference to the ListView and TableLayout
        val listView = findViewById<ListView>(R.id.logsListView)
        val csvTableLayout = findViewById<TableLayout>(R.id.csvTableLayout)

        // Prompt the user to select a directory
        openDirectoryPicker()

        // Handle item click to open or view the selected CSV file
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedUri = csvFileList[position].second  // Get the URI of the selected file
            openCsvFile(selectedUri, csvTableLayout)
        }
    }

    // Function to open directory picker
    private fun openDirectoryPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, REQUEST_CODE_OPEN_DIRECTORY)
    }

    // Handle the result of directory picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_OPEN_DIRECTORY && resultCode == RESULT_OK) {
            // Get the selected directory's URI
            directoryUri = data?.data
            if (directoryUri != null) {
                // Persist the permission for future access to the selected directory
                val takeFlags: Int = data!!.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                contentResolver.takePersistableUriPermission(directoryUri!!, takeFlags)

                // Query and display CSV files from the selected directory
                queryCsvFilesFromDirectory(directoryUri!!)
            }
        }
    }

    // Method to query CSV files from the selected directory
    private fun queryCsvFilesFromDirectory(directoryUri: Uri) {
        val listView = findViewById<ListView>(R.id.logsListView)
        csvFileList.clear()  // Clear the list of files before querying

        // Use the DocumentsContract API to list files in the directory
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(directoryUri, DocumentsContract.getTreeDocumentId(directoryUri))
        val projection = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE
        )

        val cursor = contentResolver.query(childrenUri, projection, null, null, null)
        cursor?.use {
            val displayNameColumn = it.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
            val documentIdColumn = it.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)

            while (it.moveToNext()) {
                val displayName = it.getString(displayNameColumn)
                val documentId = it.getString(documentIdColumn)

                // Only add files that end with .csv
                if (displayName.endsWith(".csv", ignoreCase = true)) {
                    val fileUri = DocumentsContract.buildDocumentUriUsingTree(directoryUri, documentId)
                    csvFileList.add(Pair(displayName, fileUri))  // Store the file name and its URI
                    Log.d("QueryFiles", "Found CSV file: $displayName")
                }
            }
        }

        if (csvFileList.isNotEmpty()) {
            // Display only the file names in the ListView
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, csvFileList.map { it.first })
            listView.adapter = adapter
        } else {
            Toast.makeText(this, "No CSV files found in the selected directory", Toast.LENGTH_SHORT).show()
        }
    }

    // Method to handle opening the CSV file and displaying its content as a table
    private fun openCsvFile(fileUri: Uri, csvTableLayout: TableLayout) {
        try {
            // Open the file using content resolver
            val inputStream = contentResolver.openInputStream(fileUri)
            val csvContent = inputStream?.bufferedReader()?.readLines()

            // Clear the previous table content
            csvTableLayout.removeAllViews()

            if (csvContent != null) {
                // Iterate through each line in the CSV
                for ((lineIndex, line) in csvContent.withIndex()) {
                    val tableRow = TableRow(this)
                    val columns = line.split(",")  // Split the CSV line by commas

                    // Create TextViews for each column and add them to the TableRow
                    for (column in columns) {
                        val textView = TextView(this).apply {
                            text = column.trim()
                            setPadding(16, 8, 16, 8)
                        }
                        tableRow.addView(textView)
                    }

                    // Add the row to the TableLayout
                    csvTableLayout.addView(tableRow)

                    // Optionally set a background for alternating rows (e.g., zebra striping)
                    if (lineIndex % 2 == 0) {
                        tableRow.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
                    }
                }

                // Make the table visible
                csvTableLayout.visibility = TableLayout.VISIBLE
            } else {
                Toast.makeText(this, "Error reading file", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // Handle any exceptions when reading the file
            Toast.makeText(this, "Error reading file: $fileUri", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}
