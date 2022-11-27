package com.timer.lab2_timer

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity
import com.zeugmasolutions.localehelper.Locales

class SettingsActivity : AppCompatActivity() {
    private var menu: ActionBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_Lab2_timer)

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