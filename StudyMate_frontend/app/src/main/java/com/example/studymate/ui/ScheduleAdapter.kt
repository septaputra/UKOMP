package com.example.studymate.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studymate.R
import com.example.studymate.model.Schedule

// RecyclerView adapter for showing a list of Schedule items.
class ScheduleAdapter(private var items: List<Schedule>) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    fun update(newItems: List<Schedule>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSubject: TextView = itemView.findViewById(R.id.tvSubject)
        private val tvDay: TextView = itemView.findViewById(R.id.tvDay)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val tvNote: TextView = itemView.findViewById(R.id.tvNote)

        fun bind(item: Schedule) {
            tvSubject.text = item.subject
            tvDay.text = item.day
            tvTime.text = item.time
            tvNote.text = item.note
        }
    }
}
