package com.nickspatties.timeclock.ui

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.*
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.data.TimeClockEventDao
import com.nickspatties.timeclock.util.Chronometer
import kotlinx.coroutines.launch

class TimeClockViewModel (
    private val database: TimeClockEventDao,
    application: Application
): AndroidViewModel(application) {

    /**
     * Common properties
     */
    var timeClockEvents = database.getAllEvents()

    // task names used for autofill dropdown
    val autofillTaskNames = Transformations.map(timeClockEvents) { events ->
        events.map {
            it.name
        }.toSet()
    }

    /**
     * Clock Page properties
     */
    var currentTimeClockEvent = MutableLiveData<TimeClockEvent?>()
    var taskTextFieldValue by mutableStateOf(TextFieldValue(text = ""))
    var clockButtonEnabled by mutableStateOf(false)
    var isClockRunning by mutableStateOf(false)
    var dropdownExpanded by mutableStateOf(false)
    var currSeconds by mutableStateOf(0)

    // cheeky var used to prevent onTaskNameChange from being called after onDropdownMenuItemClick
    private var dropdownClicked = false
    private val chronometer = Chronometer()

    /**
     * List page properties
     */
    var editingEventId by mutableStateOf(-1L)

    val allEvents = Transformations.map(timeClockEvents) {
        it.reversed()
    }

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

    /**
     * Clock page functions
     */
    private suspend fun getCurrentEventFromDatabase(): TimeClockEvent? {
        val event = database.getCurrentEvent()
        if (event?.endTime != event?.startTime) {
            return null // because this event has already been completed
        }
        return event
    }

    fun onTaskNameChange(tfv: TextFieldValue) {
        if (dropdownClicked) {
            dropdownClicked = false
            return
        }
        taskTextFieldValue = tfv
        val taskName = tfv.text
        clockButtonEnabled = taskName.isNotBlank()
        dropdownExpanded = taskName.isNotBlank()
    }

    fun onTaskNameDonePressed() {
        dropdownExpanded = false
    }

    fun onDismissDropdown() {
        dropdownExpanded = false
    }

    fun onDropdownMenuItemClick(label: String) {
        dropdownClicked = true
        taskTextFieldValue = TextFieldValue(
            text = label,
            selection = TextRange(label.length)
        )
        dropdownExpanded = false
    }

    fun startClock() {
        viewModelScope.launch {
            val newEvent = TimeClockEvent(
                name = taskTextFieldValue.text // change this to the taskTextFieldValue.text property
            )
            database.insert(newEvent)
            chronometer.start()
            currentTimeClockEvent.value = getCurrentEventFromDatabase()
            isClockRunning = true
        }
    }

    fun stopClock() {
        viewModelScope.launch {
            val finishedEvent = currentTimeClockEvent.value ?: return@launch
            finishedEvent.endTime = System.currentTimeMillis()
            database.update(finishedEvent)
            chronometer.stop()
            showToast("Task \"${taskTextFieldValue.text}\" saved!")
            currentTimeClockEvent.value = null
            isClockRunning = false
            resetCurrSeconds()
        }
    }

    private fun resetCurrSeconds() {
        currSeconds = 0
    }

    private fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }

    /**
     * List page functions
     */
    fun changeEditId(id: Long) {
        editingEventId = id
    }

    fun deleteEvent(event: TimeClockEvent) {
        viewModelScope.launch {
            database.delete(event)
            editingEventId = -1
        }
    }
}