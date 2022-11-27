package com.timer.lab2_timer

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.timer.lab2_timer.databinding.ActivityTimerBinding
import com.timer.lab2_timer.util.NotificationUtil
import com.timer.lab2_timer.util.PrefUtil
import kotlinx.coroutines.launch
import java.util.*

class TimerActivity : AppCompatActivity() {

    // For creating background timer we will use alarms
    // We need two functions: setting and removing alarms
    // We use companion object, because alarms don't have a lot to do with the actual timer activity or with the instance
    // Alarms don't need to have access to anything in instance
    companion object {

        fun setAlarm(context: Context, nowSeconds: Long, secondsRemaining: Long): Long {
            // Alarm uses milliseconds
            val wakeUpTime = (nowSeconds + secondsRemaining) * 1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            // Intent, which is going to be specifies which is going to opened after the alarm goes off
            // BroadcastReceiver is an app component like activity or fragment
            // They can describe to certain events and the an unreceive function in BroadcastReceiver will be called
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
            PrefUtil.setAlarmSetTime(nowSeconds, context)
            return wakeUpTime
        }


        fun removeAlarm(context: Context) {
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmSetTime(0, context)
        }

        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis / 1000
    }

    // We use enum class for the implementation of type-safe enums
    // Each enum constant is an object, which are separated by commas
    enum class TimerState {
        Stopped, Paused, Running
    }

    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds = 0L
    private var timerState = TimerState.Stopped

    private var secondsRemaining = 0L

    lateinit var binding: ActivityTimerBinding
    private var timerActionBar: ActionBar? = null
    private var phaseAdapter: TimerAdapter? = null
    private var item: Item? = null
    private val secDatabase by lazy { MainDatabase.getDatabase(this).getPhaseDao() }

    private var phaseList: List<Phase> = emptyList()
    private var phasePosition: Int? = null
    private var timerColor: String? = null
    private var currentPhase: Phase? = null
    private var pointer = 1
    private var FLAG: Boolean = true

    private var mMediaPlayer: MediaPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.TimerTheme)

        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        timerActionBar = supportActionBar
        timerActionBar?.title = "Timer execution"

        item = intent.getSerializableExtra("ItemData") as Item?

        phasePosition = 1
        PrefUtil.isTimerEnd = true

        binding.startTimer.setOnClickListener { v->
            PrefUtil.isTimerEnd = false
            FLAG = true
            startTimer()
            timerState = TimerState.Running
            updateButtons()
        }

       binding.pauseTimer.setOnClickListener { v->
           timer.cancel()
           timerState = TimerState.Paused
           updateButtons()
       }

        binding.stopTimer.setOnClickListener { v->
            FLAG = true
            timer.cancel()
            timerState = TimerState.Stopped
            phasePosition = 0
            onTimerFinished(false)
            setCurrentPhaseId()
        }

        binding.previousPhase.isEnabled = false
        binding.previousPhase.setColorFilter(getResources().getColor(androidx.appcompat.R.color.material_grey_600))

        binding.previousPhase.setOnClickListener { v->
            FLAG = true
            if(timerState == TimerState.Running) timer.cancel()

            if(phasePosition!! >= 2) phasePosition = phasePosition?.minus(2)
            onTimerFinished(false)
            setCurrentPhaseId()
        }

        binding.nextPhase.setOnClickListener { v->
            FLAG = true
            if(timerState == TimerState.Running) timer.cancel()

            timerState = TimerState.Stopped
            onTimerFinished(false)
            setCurrentPhaseId()
        }
    }


    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            phaseAdapter = TimerAdapter()

            binding.timerRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@TimerActivity)
                adapter = phaseAdapter

                lifecycleScope.launch {
                    Log.d("TimerActivity", "First ${phaseList.toList().size}")
                    phaseList = secDatabase.getAllPhases().filter { it.timer_id == item?.id }
                    Log.d("TimerActivity", "Second ${phaseList.toList().size}")
                    setPhaseAdapter(phaseList)
                    setCurrentPhaseId()

                    if(phaseList.toList().size == 1) {
                        binding.nextPhase.isEnabled = false
                        binding.nextPhase.setColorFilter(getResources().getColor(androidx.appcompat.R.color.material_grey_600))
                    }

                    initTimer()

                    removeAlarm(this@TimerActivity)
                    NotificationUtil.hideTimerNotification(this@TimerActivity)
                    NotificationUtil.stopService(this@TimerActivity)

                }

                timerColor = item!!.color

                setPhaseColorAdapter(timerColor!!)

                timerActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(timerColor!!)))
            }
        }
    }


    override fun onPause() {
        super.onPause()
        NotificationUtil.startService(this, "Hello Matuamod")

        if(timerState == TimerState.Running) {
            timer.cancel()

            val wakeUpTime = setAlarm(this, nowSeconds, secondsRemaining)
            NotificationUtil.showTimerRunning(this, wakeUpTime)
        }
        else if(timerState == TimerState.Paused) {
            NotificationUtil.showTimerPaused(this)
        }

        Log.d("TimerActivity", "setPreviousTimerLengthSeconds ${timerLengthSeconds}")
        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, this)
        Log.d("TimerActivity", "setSecondsRemaining ${secondsRemaining}")
        PrefUtil.setSecondsRemaining(secondsRemaining, this)
        Log.d("TimerActivity", "saved timerState ${timerState}")
        PrefUtil.setTimerState(timerState, this)
    }


    private fun setPhaseAdapter(phaseList: List<Phase>) {
        phaseAdapter?.setData(phaseList)
    }


    private fun setPhaseColorAdapter(phaseColor: String) {
        phaseAdapter?.setColor(phaseColor)
    }


    private fun setCurrentPhaseId(isNotStopped: Boolean = true) {
        if(isNotStopped) phaseAdapter?.currentPhaseId = phasePosition!!.minus(1)
        else phaseAdapter?.currentPhaseId = phasePosition!!

        phaseAdapter?.notifyDataSetChanged()
    }


    private fun initTimer(){
        timerState = PrefUtil.getTimerState(this)

        // We don't want to change the length of the timer which is already running
        // If the length was changed in settings while it was backgrounded
        if (timerState == TimerState.Stopped) {
            setNewTimerLength()
        }
        else {
            setPreviousTimerLength()
        }

        secondsRemaining = if (timerState == TimerState.Running || timerState == TimerState.Paused)
            PrefUtil.getSecondsRemaining(this)
        else
            timerLengthSeconds

        Log.d("TimerActivity", "Seconds remaining: ${secondsRemaining}")

        val alarmSetTime = PrefUtil.getAlarmSetTime(this)

        // If alarmSetTime > 0 means that alarm is actually set
        if(alarmSetTime > 0) secondsRemaining -= nowSeconds - alarmSetTime

        // Resume where we left off
        if (timerState == TimerState.Running)
            startTimer()

        updateButtons()
        updateCountDownUI()
    }


    private fun onPreviousPhaseChanged() {
        if(phasePosition == 0) {
            binding.previousPhase.isEnabled = false
            binding.previousPhase.setColorFilter(getResources().getColor(androidx.appcompat.R.color.material_grey_600))
        }
        else {
            binding.previousPhase.isEnabled = true
            binding.previousPhase.setColorFilter(getResources().getColor(R.color.red))
        }

    }


    private fun onNextPhaseChanged() {
        if(phasePosition!! >= phaseList.toList().size || phaseList.toList().size == 1) {
            binding.nextPhase.isEnabled = false
            binding.nextPhase.setColorFilter(getResources().getColor(androidx.appcompat.R.color.material_grey_600))
        }
        else {
            binding.nextPhase.isEnabled = true
            binding.nextPhase.setColorFilter(getResources().getColor(R.color.red))
        }
    }


    private fun onTimerFinished(isContinue: Boolean = true) {
        pointer = 1

        timerState = TimerState.Stopped

        onPreviousPhaseChanged()

        phasePosition = phasePosition?.plus(1)

        onNextPhaseChanged()
        checkIteration()
        setNewTimerLength()

        binding.progressCountDown.progress = 0

        PrefUtil.setSecondsRemaining(timerLengthSeconds, this)
        secondsRemaining = timerLengthSeconds

        if(isContinue) {
            if(phasePosition!! >= 2) {

                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        startTimer()
                        timerState = TimerState.Running
                        updateButtons()
                    },
                    500 // value in milliseconds
                )
            }
        }

        updateButtons()
        updateCountDownUI()
    }


    private fun onTimerAttemptFinished() {
        timerState = TimerState.Stopped

        setNewTimerLength()

        binding.progressCountDown.progress = 0

        PrefUtil.setSecondsRemaining(timerLengthSeconds, this@TimerActivity)
        secondsRemaining = timerLengthSeconds

        pointer = pointer.plus(1)

        startTimer(false)

        updateButtons()
        updateCountDownUI()
    }


    private fun onTimerRestFinished() {
        timerState = TimerState.Stopped

        setNewRestLength()

        binding.progressCountDown.progress = 0

        PrefUtil.setSecondsRemaining(timerLengthSeconds, this@TimerActivity)
        secondsRemaining = timerLengthSeconds

        startTimer(true)

        updateButtons()
        updateCountDownUI()
    }


    private fun startTimer(isNewAttempt: Boolean = true) {
        setCurrentPhaseId()

        timerState = TimerState.Running

        binding.itemTime.text = "Workout now"

        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() {

                if(isNewAttempt) {

                    if(pointer == currentPhase?.attempt_count!!) {
                        FLAG = true
                        soundCheck()
                        onTimerFinished()
                    }
                    else {

                        if(FLAG) {
                            FLAG = false
                            binding.itemTime.text = "Relax now"
                            onTimerRestFinished()
                        }
                        else {
                            binding.itemTime.text = "Workout now"
                            onTimerAttemptFinished()
                        }
                    }
                }
                else {
                    binding.itemTime.text = "Relax now"
                    onTimerRestFinished()
                }
            }

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = (millisUntilFinished / 1000)
                Log.d("TimerActivity", "startTimer secondsRemaining ${secondsRemaining}")
                updateCountDownUI()
            }
        }.start()
    }


    private fun getCurrentPhase() {
        var pointer = 0

        for (phase in phaseList.toList()) {
            pointer++

            if(pointer == phasePosition) currentPhase = phase
        }
    }


    private fun checkIteration() {
        if(phasePosition!! <= phaseList.toList().size) {
            Log.d("TimerActivity", "Current phase position is: ${phasePosition}")
            PrefUtil.isTimerEnd = false
        }
        else {
            phasePosition = 1
            timerState = TimerState.Stopped
            PrefUtil.isTimerEnd = true

            binding.previousPhase.isEnabled = false
            binding.previousPhase.setColorFilter(getResources().getColor(androidx.appcompat.R.color.material_grey_600))

            binding.nextPhase.isEnabled = true
            binding.nextPhase.setColorFilter(getResources().getColor(R.color.red))
        }
    }


    private fun setNewRestLength() {
        getCurrentPhase()
        PrefUtil.setTimerLength(this, currentPhase?.rest!!.toLong())

        var lengthInMinutes = PrefUtil.getTimerLength(this)

        if (lengthInMinutes != null) {
            timerLengthSeconds = (lengthInMinutes * 60F).toLong()
        }

        binding.progressCountDown.max = timerLengthSeconds.toInt()
    }


    private fun setNewTimerLength() {
        getCurrentPhase()
        PrefUtil.setTimerLength(this, currentPhase?.duration!!.toLong())

        var lengthInMinutes = PrefUtil.getTimerLength(this)

        if (lengthInMinutes != null) {
            timerLengthSeconds = (lengthInMinutes * 60F).toLong()
        }

        binding.progressCountDown.max = timerLengthSeconds.toInt()
    }


    private fun setPreviousTimerLength() {
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(this)
        binding.progressCountDown.max = timerLengthSeconds.toInt()
    }


    private fun updateCountDownUI() {
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinuteUntilFinished.toString()

        binding.phaseTime.text = "$minutesUntilFinished:${
            if(secondsStr.length == 2) secondsStr
            else "0" + secondsStr}"

        PrefUtil.setCurrentSecond(this@TimerActivity, "$minutesUntilFinished:${
            if(secondsStr.length == 2) secondsStr
            else "0" + secondsStr}")

        Log.d("TimerActivity", "$minutesUntilFinished:${
            if(secondsStr.length == 2) secondsStr
            else "0" + secondsStr}")

        binding.progressCountDown.progress = (timerLengthSeconds - secondsRemaining).toInt()
    }


    private fun updateButtons() {
        when(timerState) {
            TimerState.Running -> {
                binding.startTimer.isEnabled = false
                binding.startTimer.setColorFilter(getResources().getColor(androidx.appcompat.R.color.material_grey_600))
                binding.pauseTimer.isEnabled = true
                binding.pauseTimer.setColorFilter(getResources().getColor(R.color.red))
                binding.stopTimer.isEnabled = true
                binding.stopTimer.setColorFilter(getResources().getColor(R.color.red))
            }
            TimerState.Stopped -> {
                binding.startTimer.isEnabled = true
                binding.startTimer.setColorFilter(getResources().getColor(R.color.red))
                binding.pauseTimer.isEnabled = false
                binding.pauseTimer.setColorFilter(getResources().getColor(androidx.appcompat.R.color.material_grey_600))
                binding.stopTimer.isEnabled = false
                binding.stopTimer.setColorFilter(getResources().getColor(androidx.appcompat.R.color.material_grey_600))
            }
            TimerState.Paused -> {
                binding.startTimer.isEnabled = true
                binding.startTimer.setColorFilter(getResources().getColor(R.color.red))
                binding.pauseTimer.isEnabled = false
                binding.pauseTimer.setColorFilter(getResources().getColor(androidx.appcompat.R.color.material_grey_600))
                binding.stopTimer.isEnabled = true
                binding.stopTimer.setColorFilter(getResources().getColor(R.color.red))
            }
        }
    }


    private fun playSound() {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.sport)
            mMediaPlayer!!.isLooping = true
            mMediaPlayer!!.start()
        } else mMediaPlayer!!.start()
    }


    fun stopSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }


    private fun soundCheck() {
        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                playSound()
            }

            override fun onFinish() {
                stopSound()
            }
        }.start()
    }
}
