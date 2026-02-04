package com.example.studymate.ui

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studymate.R
import com.example.studymate.viewmodel.NotesViewModel

// Activity demonstrating fetching notes via NotesViewModel and showing them
// in a RecyclerView. Pattern mirrors SchedulesActivity for consistency.
class NotesActivity : AppCompatActivity() {
    private lateinit var viewModel: NotesViewModel
    private lateinit var adapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        val rv: RecyclerView = findViewById(R.id.rvNotes)
        val progress: ProgressBar = findViewById(R.id.progressBar)
        val tvError: TextView = findViewById(R.id.tvError)

        adapter = NotesAdapter(emptyList())
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        viewModel = ViewModelProvider(this).get(NotesViewModel::class.java)

        viewModel.notes.observe(this, Observer { list ->
            progress.visibility = View.GONE
            tvError.visibility = View.GONE
            adapter.update(list)
        })

        viewModel.error.observe(this, Observer { err ->
            progress.visibility = View.GONE
            if (err != null) {
                tvError.visibility = View.VISIBLE
                tvError.text = err
            } else {
                tvError.visibility = View.GONE
            }
        })

        progress.visibility = View.VISIBLE
        viewModel.fetchNotes()
    }
}
