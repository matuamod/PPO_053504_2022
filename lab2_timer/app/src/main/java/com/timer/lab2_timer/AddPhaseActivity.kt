package com.timer.lab2_timer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.timer.lab2_timer.databinding.ActivityAddPhaseBinding
import kotlinx.coroutines.launch

class AddPhaseActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddPhaseBinding
    private var phase: Phase? = null
    private var item: Item? = null
    private val mainDatabase by lazy { MainDatabase.getDatabase(this).getPhaseDao() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPhaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phase = intent.getSerializableExtra("PhaseData") as Phase?
        item = intent.getSerializableExtra("ItemData") as Item?

        if(phase == null) {
            binding.addOrUpdatePhase.text = "Add phase"
            Log.d("AddPhaseActivity", "phase is null -> add phase to db")
        }
        else {
            binding.addOrUpdatePhase.text = "Update"
            binding.editName.setText(phase?.name.toString())
            binding.editDuration.setText(phase?.duration.toString())
            binding.editAttemptCount.setText(phase?.attempt_count.toString())
        }

        binding.addOrUpdatePhase.setOnClickListener { insertPhase() }
    }


    private fun insertPhase() {

        val phaseName = binding.editName.text.toString()
        val phaseDuration = binding.editDuration.text.toString().toInt()
        val phaseAttemptCount = binding.editAttemptCount.text.toString().toInt()


        lifecycleScope.launch {
            if(phase == null) {
                Log.d("AddPhaseActivity", "new_phase waiting for creating")
                val new_phase = Phase(
                    timer_id = item?.id,
                    name = phaseName,
                    duration = phaseDuration,
                    attempt_count = phaseAttemptCount
                )
                Log.d("AddPhaseActivity", "new_phase is ready, before inserting to db")
                mainDatabase.insertPhase(new_phase)
                Log.d("AddPhaseActivity", "new_phase is ready, after inserting to db")
                finish()
            }
            else {
                val update_phase = Phase(
                    timer_id = item?.id,
                    name = phaseName,
                    duration = phaseDuration,
                    attempt_count = phaseAttemptCount
                )
                update_phase.id = phase?.id ?: null
                mainDatabase.updatePhase(update_phase)
                finish()
            }
        }
    }
}