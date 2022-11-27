package com.timer.lab2_timer

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.zeugmasolutions.localehelper.Locales
import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity

class MySettingsFragment : PreferenceFragmentCompat() {
    private var myTheme: Preference? = null
    private var myLanguage: Preference? = null
    private var myFontSize: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        myTheme = findPreference<SwitchPreference>("theme_preference")
        myTheme?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->

                if (preference is SwitchPreference) {
                    val value = newValue as Boolean
                    if (value) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        Toast.makeText(activity, "Black theme is ready", Toast.LENGTH_SHORT).show()
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        Toast.makeText(activity, "White theme is ready", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }

        myLanguage = findPreference<ListPreference>("language")
        myLanguage?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->

                if (preference is ListPreference) {
                    val index = preference.findIndexOfValue(newValue.toString())
                    val entry = preference.entries.get(index)
                    val entryvalue = preference.entryValues.get(index)
                    Log.d("SettingsFragment", "selected val: position - $index value - $entry, " +
                            "entryvalue - $entryvalue ")

                    if (index == 0) {
                        Toast.makeText(activity, "Russian language is ready", Toast.LENGTH_SHORT).show()
//                        updateLocale(Locales.Russian)
                    } else {
                        Toast.makeText(activity, "English language is ready", Toast.LENGTH_SHORT).show()
//                        updateLocale(Locales.English)
                    }
                }
                true
            }

        myFontSize = findPreference<ListPreference>("font_size")
        myFontSize?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->

                val scaleCoefficient = TypedValue()


                when (newValue as String) {

                    "small" -> resources.getValue(R.dimen.smallScaleCoefficient, scaleCoefficient, true)
                    "medium" -> resources.getValue(R.dimen.mediumScaleCoefficient, scaleCoefficient, true)
                    "large" -> resources.getValue(R.dimen.largeScaleCoefficient, scaleCoefficient, true)
                }

                resources.configuration.fontScale = scaleCoefficient.float
                resources.displayMetrics.scaledDensity = resources.configuration.fontScale * resources.displayMetrics.density
                resources.configuration.updateFrom(resources.configuration)

                activity?.finish()
                startActivity(Intent(activity, SettingsActivity::class.java))
                true
            }
    }
}
