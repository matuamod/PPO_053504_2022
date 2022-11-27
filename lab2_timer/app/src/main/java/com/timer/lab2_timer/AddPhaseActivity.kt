package com.timer.lab2_timer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.lifecycleScope
import com.timer.lab2_timer.databinding.ActivityAddPhaseBinding
import kotlinx.coroutines.launch

class AddPhaseActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddPhaseBinding
    private var phase: Phase? = null
    private var item: Item? = null
    private var phaseActionBar: ActionBar? = null
    private val mainDatabase by lazy { MainDatabase.getDatabase(this).getPhaseDao() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_Lab2_timer)

        binding = ActivityAddPhaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phase = intent.getSerializableExtra("PhaseData") as Phase?
        item = intent.getSerializableExtra("ItemData") as Item?
        phaseActionBar = supportActionBar

        if(phase == null) {
            binding.addOrUpdatePhase.text = "Add phase"
            phaseActionBar?.title = "Add phase"
        }
        else {
            binding.addOrUpdatePhase.text = "Update"
            phaseActionBar?.title = "Update phase"
            binding.editName.setText(phase?.name.toString())
            binding.editDuration.setText(phase?.duration.toString())
            binding.editRest.setText(phase?.rest.toString())
            binding.editAttemptCount.setText(phase?.attempt_count.toString())
        }

        binding.addOrUpdatePhase.setOnClickListener { insertPhase() }
    }


    private fun insertPhase() {

        val phaseName = binding.editName.text.toString()
        val phaseDuration = binding.editDuration.text.toString().toInt()
        val phaseRest = binding.editRest.text.toString().toInt()
        val phaseAttemptCount = binding.editAttemptCount.text.toString().toInt()


        lifecycleScope.launch {
            if(phase == null) {
                val new_phase = Phase(
                    timer_id = item?.id,
                    name = phaseName,
                    duration = phaseDuration,
                    rest = phaseRest,
                    attempt_count = phaseAttemptCount
                )
                mainDatabase.insertPhase(new_phase)
                finish()
            }
            else {
                val update_phase = Phase(
                    timer_id = item?.id,
                    name = phaseName,
                    duration = phaseDuration,
                    rest = phaseRest,
                    attempt_count = phaseAttemptCount
                )
                update_phase.id = phase?.id ?: null
                mainDatabase.updatePhase(update_phase)
                finish()
            }
        }
    }
}