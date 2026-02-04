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
import com.example.studymate.viewmodel.ScheduleViewModel

// Simple Activity demonstrating how to observe the ViewModel and show
// schedules in a RecyclerView. This is a plain Android view example (not Compose).
class SchedulesActivity : AppCompatActivity() {
    private lateinit var viewModel: ScheduleViewModel
    private lateinit var adapter: ScheduleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedules)

        val rv: RecyclerView = findViewById(R.id.rvSchedules)
        val progress: ProgressBar = findViewById(R.id.progressBar)
        val tvError: TextView = findViewById(R.id.tvError)

        adapter = ScheduleAdapter(emptyList())
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        viewModel = ViewModelProvider(this).get(ScheduleViewModel::class.java)

        // Observe schedules LiveData and update adapter when data arrives
        viewModel.schedules.observe(this, Observer { list ->
            progress.visibility = View.GONE
            tvError.visibility = View.GONE
            adapter.update(list)
        })

        // Observe error LiveData and show a simple message
        viewModel.error.observe(this, Observer { err ->
            progress.visibility = View.GONE
            if (err != null) {
                tvError.visibility = View.VISIBLE
                tvError.text = err
            } else {
                tvError.visibility = View.GONE
            }
        })

        // Start loading data
        progress.visibility = View.VISIBLE
        viewModel.fetchSchedules()
    }
}
