package com.example.firemissionlogger

import android.content.Intent
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.firemissionlogger.R.*
import com.google.android.material.navigation.NavigationView

open class BaseActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    // Helper method to navigate with the callsign
    private fun navigateWithCallsign(targetActivity: Class<*>, callsign: String) {
        val intent = Intent(this, targetActivity).apply {
            putExtra("CALLSIGN", callsign)
        }
        startActivity(intent)
    }

    // Setup navigation drawer with provided IDs
    fun setupNavigationDrawer(drawerLayoutId: Int, navViewId: Int, toolbarId: Int) {
        drawerLayout = findViewById(drawerLayoutId)
        val toolbar = findViewById<Toolbar>(toolbarId)
        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(this, drawerLayout, string.open, string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        // Retrieve the callsign from SharedPreferences
        val sharedPreferences = getSharedPreferences("FireMissionPrefs", MODE_PRIVATE)
        val callsign = sharedPreferences.getString("CALLSIGN", "default_callsign") ?: "default_callsign"

        // Set up navigation item selection
        val navView = findViewById<NavigationView>(navViewId)
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                id.nav_fox1_logger -> navigateWithCallsign(Fox1Activity::class.java, callsign)
                id.nav_fox2_logger -> navigateWithCallsign(Fox2Activity::class.java, callsign)
                id.nav_logs -> navigateWithCallsign(LogsActivity::class.java, callsign)
                id.nav_start -> navigateWithCallsign(StartActivity::class.java, callsign)
                id.nav_exit -> {
                    // Show an exit confirmation dialog
                    val dialog = AlertDialog.Builder(this)
                        .setTitle("Exit")
                        .setMessage("Are you sure you want to exit the app?")
                        .setPositiveButton("Yes") { _, _ -> finishAffinity() }
                        .setNegativeButton("No", null)
                        .create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(color.white))
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(color.white))


                }
            }
            drawerLayout.closeDrawers() // Close the drawer after selecting an item
            true
        }
    }

    // Handle the action bar toggle clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
