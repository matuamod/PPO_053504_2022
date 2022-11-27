package com.timer.lab2_timer.util

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import com.timer.lab2_timer.Phase
import com.timer.lab2_timer.TimerActivity

class PrefUtil {

    // Members, which are defined in companion object are something like static methods on Java or C#
    // It means, that we can call all these methods from companion object by the name of our class
    // without explicit indication of the name of object
    companion object {

        private var timerLength: Float? = 1F
        var isTimerEnd = true
        private var currentTime: String? = null

        fun setCurrentSecond(context: Context, time: String) {
            currentTime = time
        }

        // We use context in methods to get resources from our app(values from preferences)
        fun getTimerLength(context: Context): Float? {
            // placeholder
            return timerLength
        }


        fun setTimerLength(context: Context, seconds: Long) {
            var lengthInMinutes: Long = seconds / 60L
            var partSeconds = seconds - lengthInMinutes * 60L
            var lengthPartInMinutes: Float = partSeconds.toFloat() / 60F
            timerLength = lengthInMinutes.toFloat() + lengthPartInMinutes
        }


        // This is an id, which we are going to use in preferences to identify the values
        // Preferences are like key-value types
        // This id is like a key in preferences
        private const val PREVIOUS_TIMER_LENGTH_SECONDS_ID = "com.timer.previous_timer_length"


        // It's not a good idea to change a timer length of the running timer
        // We just change the length of the future timer, after the current stops
        // So, when the timer length is changed, the current timer length wouldn't be changed
        fun getPreviousTimerLengthSeconds(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, 0)
        }


        // Here we only edit timer length duration by editing node by id and apply changes
        fun setPreviousTimerLengthSeconds(seconds: Long, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, seconds)
            editor.apply()
        }


        // It would be a key of timer state in preferences
        private const val TIMER_STATE_ID = "com.timer.timer_state"


        // This method would be return state of timer with the type of our enum class that we defined earlier
        fun getTimerState(context: Context): TimerActivity.TimerState {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)

            // Enums are basically integer's with names, so we store themas int in preferences
            // ordinal is like a serial
            val ordinal = preferences.getInt(TIMER_STATE_ID, 0)
            return TimerActivity.TimerState.values()[ordinal]
        }


        fun setTimerState(state: TimerActivity.TimerState, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = state.ordinal
            editor.putInt(TIMER_STATE_ID, ordinal)
            editor.apply()
        }


        private const val SECONDS_REMAINING_ID = "com.timer.seconds_remaining"


        fun getSecondsRemaining(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(SECONDS_REMAINING_ID, 0)
        }


        fun setSecondsRemaining(seconds: Long, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS_REMAINING_ID, seconds)
            editor.apply()
        }


        private const val ALARM_SET_TIME_ID = "com.timer.background_time"


        fun getAlarmSetTime(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(ALARM_SET_TIME_ID, 0)
        }


        fun setAlarmSetTime(time: Long, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(ALARM_SET_TIME_ID, time)
            editor.apply()
        }
    }
}
