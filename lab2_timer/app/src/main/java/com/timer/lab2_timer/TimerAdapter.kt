package com.timer.lab2_timer

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TimerAdapter : RecyclerView.Adapter<TimerAdapter.TimerPhaseViewHolder>()  {

    private var list = mutableListOf<Phase>()
    private var phasesColor: String? = null
    var currentPhaseId: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerPhaseViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.card_phase_for_timer_view_holder, parent, false)

        return TimerPhaseViewHolder(view)
    }


    override fun onBindViewHolder(holder: TimerPhaseViewHolder, position: Int) {
        val phasePosition = list[position]

        holder.tvName.text = phasePosition.name
        holder.tvDuration.text = phasePosition.duration.toString()
        holder.tvRest.text = phasePosition.rest.toString()
        holder.tvAttemptCount.text = phasePosition.attempt_count.toString()

        if(currentPhaseId == position) {
            holder.laPhase.setBackgroundColor(Color.parseColor("#FF757575"))
        }
        else {
            holder.laPhase.setBackgroundColor(Color.parseColor(phasesColor))
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setData(bufList: List<Phase>) {
        list.apply {
            clear()
            addAll(bufList)
        }
        // Now we need to inform our Adapter of making changes(changed list)
        notifyDataSetChanged()
    }


    fun setColor(color: String) {
        phasesColor = color
    }


    // All these val's we will be used in onBindViewHolderMethod()
    class TimerPhaseViewHolder(phaseView: View): RecyclerView.ViewHolder(phaseView) {
        val tvName: TextView = phaseView.findViewById(R.id.nameTextView)
        val tvDuration: TextView = phaseView.findViewById(R.id.durationTextView)
        val tvRest: TextView = phaseView.findViewById(R.id.restTextView)
        val tvAttemptCount: TextView = phaseView.findViewById(R.id.attemptCountTextView)
        val laPhase: LinearLayout = phaseView.findViewById(R.id.currentPhaseViewHolderColor)
    }

}