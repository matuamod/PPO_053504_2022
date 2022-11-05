package com.timer.lab2_timer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ScrollCaptureCallback
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Class, which connect out list with data in MainActivity
class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>()  {

    private var list = mutableListOf<Item>()
    // Define lambda functions for future callbacks
    // The unit return type cannot be omitted(means nothing to return)
    private var actionUpdate: ((Item) -> Unit)? = null
    private var actionDelete: ((Item) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // We need to get view of our card_item_view_holder in MainActivity
        val view = LayoutInflater.from(parent.context).
            inflate(R.layout.card_item_view_holder, parent, false)

        // return our ItemViewHolder
        return ItemViewHolder(view)
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val itemPosition = list[position]

        holder.tvName.text = itemPosition.name
        holder.tvDuration.text = itemPosition.duration.toString()
        holder.tvColor.text = itemPosition.color

        holder.ivActionUpdate.setOnClickListener {
            // When we want to invoke the listener
            actionUpdate?.invoke(itemPosition)
        }

        holder.ivActionDelete.setOnClickListener {
            // When we want to invoke the listener
            actionDelete?.invoke(itemPosition)
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setData(bufList: List<Item>) {
        list.apply {
            clear()
            addAll(bufList)
        }
        // Now we need to inform our Adapter of making changes(changed list)
        notifyDataSetChanged()
    }


    fun setOnActionUpdateListener(callback: (Item) -> Unit) {
        this.actionUpdate = callback
    }


    fun setOnActionDeleteListener(callback: (Item) -> Unit) {
        this.actionDelete = callback
    }


    // Create our ViewHolder for ItemAdapter (val's for connection between our Activity and list items)
    // All these val's we will be used in onBindViewHolderMethod()
    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.nameTextView)
        val tvDuration: TextView = itemView.findViewById(R.id.durationTextView)
        val tvColor: TextView = itemView.findViewById(R.id.colorTextView)
        val ivActionUpdate: ImageView = itemView.findViewById(R.id.action_update)
        val ivActionDelete: ImageView = itemView.findViewById(R.id.action_delete)
    }

}