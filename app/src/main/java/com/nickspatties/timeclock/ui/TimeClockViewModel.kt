package com.nickspatties.timeclock.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.data.TimeClockEventDao
import com.nickspatties.timeclock.util.Chronometer

class TimeClockViewModel (
    val database: TimeClockEventDao,
    application: Application
): AndroidViewModel(application) {

    // all the time clock events
    var timeClockEvents = mutableStateListOf<TimeClockEvent>()
        private set

    // fields modified by the clock view
    var taskName by mutableStateOf("")
    var currEventStartTime by mutableStateOf(0L)
    var currEventEndTime by mutableStateOf(0L)
    var currSeconds by mutableStateOf(0)

    private val chronometer = Chronometer()

    init {
        chronometer.setOnChronometerTickListener {
            currSeconds += 1
        }
    }

    fun isRunning(): Boolean = currEventStartTime > currEventEndTime

    fun startClock() {
        chronometer.start()
        setCurrEventStartTime()
    }

    fun stopClock() {
        chronometer.stop()
        setCurrEventEndTime()
        resetCurrSeconds()
        addEvent(TimeClockEvent(
            taskName,
            currEventStartTime,
            currEventEndTime
        ))
    }

    private fun resetCurrSeconds() {
        currSeconds = 0
    }

    private fun setCurrEventStartTime(st: Long? = null) {
        currEventStartTime = st ?: System.currentTimeMillis()
    }

    private fun setCurrEventEndTime(et: Long? = null) {
        currEventEndTime = et ?: System.currentTimeMillis()
    }

    private fun addEvent(event: TimeClockEvent) {
        timeClockEvents.add(event)
    }
}