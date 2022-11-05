package com.timer.lab2_timer

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.preference.SwitchPreferenceCompat

class MySettingsFragment : PreferenceFragmentCompat() {
    private var myPref: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        myPref = findPreference<SwitchPreference>("theme_preference")
        myPref?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->

                if(preference is SwitchPreference) {
                    val value = newValue as Boolean
                    if(value) {
                        Toast.makeText(activity, "Black theme is ready", Toast.LENGTH_SHORT).show()
                        Log.d("MySettingsFragment", "Black theme is ready")
                    }
                    else {
                        Toast.makeText(activity, "White theme is ready", Toast.LENGTH_SHORT).show()
                        Log.d("MySettingsFragment", "White theme is ready")
                    }
                }
                true
            }
    }
}
