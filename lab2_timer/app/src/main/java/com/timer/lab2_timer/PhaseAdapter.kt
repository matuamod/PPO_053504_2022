package com.timer.lab2_timer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PhaseAdapter : RecyclerView.Adapter<PhaseAdapter.PhaseViewHolder>()  {

    private var list = mutableListOf<Phase>()
    // Define lambda functions for future callbacks
    // The unit return type cannot be omitted(means nothing to return)
    private var actionUpdate: ((Phase) -> Unit)? = null
    private var actionDelete: ((Phase) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhaseViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.card_phase_view_holder, parent, false)

        return PhaseViewHolder(view)
    }


    override fun onBindViewHolder(holder: PhaseViewHolder, position: Int) {
        val phasePosition = list[position]

        holder.tvName.text = phasePosition.name
        holder.tvDuration.text = phasePosition.duration.toString()
        holder.tvAttemptCount.text = phasePosition.attempt_count.toString()

        holder.ivActionUpdate.setOnClickListener {
            // When we want to invoke the listener
            actionUpdate?.invoke(phasePosition)
        }

        holder.ivActionDelete.setOnClickListener {
            // When we want to invoke the listener
            actionDelete?.invoke(phasePosition)
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


    fun setOnActionUpdateListener(callback: (Phase) -> Unit) {
        this.actionUpdate = callback
    }


    fun setOnActionDeleteListener(callback: (Phase) -> Unit) {
        this.actionDelete = callback
    }


    // All these val's we will be used in onBindViewHolderMethod()
    class PhaseViewHolder(phaseView: View): RecyclerView.ViewHolder(phaseView) {
        val tvName: TextView = phaseView.findViewById(R.id.nameTextView)
        val tvDuration: TextView = phaseView.findViewById(R.id.durationTextView)
        val tvAttemptCount: TextView = phaseView.findViewById(R.id.attemptCountTextView)
        val ivActionUpdate: ImageView = phaseView.findViewById(R.id.action_update)
        val ivActionDelete: ImageView = phaseView.findViewById(R.id.action_delete)
    }

}