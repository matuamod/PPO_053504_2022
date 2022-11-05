package com.timer.lab2_timer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar

class SettingsActivity : AppCompatActivity() {
    private var menu: ActionBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, MySettingsFragment())
            .commit()

        menu = supportActionBar
        menu?.setDisplayShowHomeEnabled(true)
        menu?.setDisplayHomeAsUpEnabled(true)
        menu?.title = "Settings"
    }


    override fun onBackPressed() {
        super.onBackPressed()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}