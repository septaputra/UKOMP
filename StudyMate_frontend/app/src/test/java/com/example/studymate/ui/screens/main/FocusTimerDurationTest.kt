package com.example.studymate.ui.screens.main

import org.junit.Assert.assertEquals
import org.junit.Test

class FocusTimerDurationTest {
    @Test
    fun convertsTimePartsToTotalSeconds() {
        assertEquals(0L, timerTotalSeconds(0, 0, 0))
        assertEquals(1L, timerTotalSeconds(0, 0, 1))
        assertEquals(59L, timerTotalSeconds(0, 0, 59))
        assertEquals(60L, timerTotalSeconds(0, 1, 0))
        assertEquals(728L, timerTotalSeconds(0, 12, 8))
        assertEquals(3600L, timerTotalSeconds(1, 0, 0))
        assertEquals(25928L, timerTotalSeconds(7, 12, 8))
    }

    @Test
    fun preservesEveryMinuteAndSecondValue() {
        for (minutes in 0..59) {
            for (seconds in 0..59) {
                assertEquals(
                    minutes * 60L + seconds,
                    timerTotalSeconds(0, minutes, seconds)
                )
            }
        }
    }
}
