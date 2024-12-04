package com.example.firemissionlogger

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

open class BaseActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // Method to set up the navigation drawer

    fun setupNavigationDrawer(drawerLayoutId: Int, navViewId: Int, toolbarId: Int) {
        drawerLayout = findViewById(drawerLayoutId)
        val toolbar = findViewById<Toolbar>(toolbarId)
        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        val navView = findViewById<NavigationView>(navViewId)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_fox1_logger -> startActivity(Intent(this, Fox1LoggerActivity::class.java))
                R.id.nav_fox2_logger -> startActivity(Intent(this, MainActivity::class.java))
                R.id.nav_logs -> startActivity(Intent(this, LogsActivity::class.java))
                R.id.nav_start -> startActivity(Intent(this, StartActivity::class.java))
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
