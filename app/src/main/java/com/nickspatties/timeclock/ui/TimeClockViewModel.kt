package com.nickspatties.timeclock.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.data.TimeClockEventDao
import com.nickspatties.timeclock.util.Chronometer
import kotlinx.coroutines.launch

class TimeClockViewModel (
    val database: TimeClockEventDao,
    application: Application
): AndroidViewModel(application) {

    // all the time clock events
    var timeClockEvents = database.getAllEvents()

    // current time clock event that's being recorded
    var currentTimeClockEvent = MutableLiveData<TimeClockEvent?>()

    // fields modified by the clock view
    var taskName by mutableStateOf("")
    var currEventStartTime by mutableStateOf(0L)
    var currEventEndTime by mutableStateOf(0L)
    var currSeconds by mutableStateOf(0)

    // TODO: how about things like the currentElapsedTime that's based on a transformation of the current TimeClockEvent

    private val chronometer = Chronometer()

    init {
        chronometer.setOnChronometerTickListener {
            currSeconds += 1
        }
    }

    fun isRunning(): Boolean = currEventStartTime > currEventEndTime

    suspend fun getCurrentEventFromDatabase(): TimeClockEvent? {
        val event = database.getCurrentEvent()
        if (event?.endTime != event?.startTime) {
            return null // because this event has already been completed
        }
        return event
    }

    fun startClock() {
        chronometer.start()
        // create current model
        viewModelScope.launch {
            val newEvent = TimeClockEvent(
                name = taskName
            )
            database.insert(newEvent)
            currentTimeClockEvent.value = getCurrentEventFromDatabase()

            // TODO: don't use this value, but use a transformation of the currentTimeClockEvent
            currEventStartTime = currentTimeClockEvent.value!!.startTime
        }
    }

    fun stopClock() {
        chronometer.stop()
        // update current model
        viewModelScope.launch {
            val finishedEvent = currentTimeClockEvent.value ?: return@launch
            finishedEvent.endTime = System.currentTimeMillis()
            database.update(finishedEvent)
            currentTimeClockEvent.value = null

            // TODO: don't use this value, but use a transformation of the currentTimeClockEvent
            currEventEndTime = finishedEvent.endTime
        }
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
        //timeClockEvents.add(event)
    }
}