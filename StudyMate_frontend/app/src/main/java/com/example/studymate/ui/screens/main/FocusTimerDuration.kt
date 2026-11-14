package com.example.studymate.ui.screens.main

internal fun timerTotalSeconds(hours: Int, minutes: Int, seconds: Int): Long =
    hours.toLong() * 3600L + minutes.toLong() * 60L + seconds.toLong()
