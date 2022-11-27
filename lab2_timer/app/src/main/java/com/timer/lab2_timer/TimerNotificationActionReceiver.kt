package com.timer.lab2_timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.timer.lab2_timer.util.NotificationUtil
import com.timer.lab2_timer.util.PrefUtil

class TimerNotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action){
            AppConstants.ACTION_STOP -> {
                TimerActivity.removeAlarm(context)
                PrefUtil.setTimerState(TimerActivity.TimerState.Stopped, context)
                NotificationUtil.hideTimerNotification(context)
                NotificationUtil.stopService(context)
            }
            AppConstants.ACTION_PAUSE -> {
                var secondsRemaining = PrefUtil.getSecondsRemaining(context)
                val alarmSetTime = PrefUtil.getAlarmSetTime(context)
                val nowSeconds = TimerActivity.nowSeconds

                secondsRemaining -= nowSeconds - alarmSetTime
                PrefUtil.setSecondsRemaining(secondsRemaining, context)

                TimerActivity.removeAlarm(context)
                PrefUtil.setTimerState(TimerActivity.TimerState.Paused, context)
                NotificationUtil.showTimerPaused(context)
            }
            AppConstants.ACTION_RESUME -> {
                val secondsRemaining = PrefUtil.getSecondsRemaining(context)
                val wakeUpTime = TimerActivity.setAlarm(context, TimerActivity.nowSeconds, secondsRemaining)
                PrefUtil.setTimerState(TimerActivity.TimerState.Running, context)
                NotificationUtil.showTimerRunning(context, wakeUpTime)
            }
            AppConstants.ACTION_START -> {
                val minutesRemaining = PrefUtil.getTimerLength(context)
                val secondsRemaining = minutesRemaining!! * 60L
                val wakeUpTime = TimerActivity.setAlarm(context, TimerActivity.nowSeconds, secondsRemaining.toLong())
                PrefUtil.setTimerState(TimerActivity.TimerState.Running, context)
                PrefUtil.setSecondsRemaining(secondsRemaining.toLong(), context)
                NotificationUtil.showTimerRunning(context, wakeUpTime)
            }
        }
    }
}