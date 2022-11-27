package com.timer.lab2_timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.timer.lab2_timer.util.NotificationUtil
import com.timer.lab2_timer.util.PrefUtil

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        PrefUtil.setTimerState(TimerActivity.TimerState.Stopped, context)
        PrefUtil.setAlarmSetTime(0, context)
        NotificationUtil.showTimerExpired(context)

        if(!PrefUtil.isTimerEnd) {
            Log.d("TimerExpiredReceiver", "Timer is running now")
            NotificationUtil.startNewTimer(context)
        }
        else {
            Log.d("TimerExpiredReceiver", "Timer is running now")
            PrefUtil.setTimerState(TimerActivity.TimerState.Stopped, context)
            PrefUtil.setAlarmSetTime(0, context)
            NotificationUtil.showTimerExpired(context)
        }
    }
}
