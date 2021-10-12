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

    val allEvents = Transformations.map(timeClockEvents) {
        it.reversed()
    }

    // current time clock event that's being recorded
    var currentTimeClockEvent = MutableLiveData<TimeClockEvent?>()

    // fields modified by the clock view
    var taskName by mutableStateOf("")
    var currSeconds by mutableStateOf(0)

    private val chronometer = Chronometer()

    init {
        chronometer.setOnChronometerTickListener {
            currSeconds += 1
        }
        viewModelScope.launch {
            // initialize the currentEvent in case the app was closed while counting
            val currEvent = getCurrentEventFromDatabase()
            currentTimeClockEvent.value = currEvent
        }
    }

    fun isRunning(): Boolean {
        val currEvent = currentTimeClockEvent.value
        return currEvent != null && currEvent.startTime == currEvent.endTime
    }

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
            resetCurrSeconds()
        }
    }

    private fun resetCurrSeconds() {
        currSeconds = 0
    }
}