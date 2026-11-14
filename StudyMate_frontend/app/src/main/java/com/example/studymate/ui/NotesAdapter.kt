package com.example.studymate.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studymate.R
import com.example.studymate.model.Note

// RecyclerView adapter for Note items.
class NotesAdapter(private var items: List<Note>) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    fun update(newItems: List<Note>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvSubject: TextView = itemView.findViewById(R.id.tvSubject)
        private val tvCreated: TextView = itemView.findViewById(R.id.tvCreated)
        private val tvContent: TextView = itemView.findViewById(R.id.tvContent)

        fun bind(item: Note) {
            tvTitle.text = item.title
            tvSubject.text = item.subject
            tvCreated.text = item.created_at
            tvContent.text = item.content
        }
    }
}
