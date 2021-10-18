package com.nickspatties.timeclock.ui

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
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
    var taskName by mutableStateOf("") // should be replaced with text field value
    var taskTextFieldValue by mutableStateOf(TextFieldValue(text = "")) // replace task name with text field value
    var clockButtonEnabled by mutableStateOf(false)
    var isClockRunning by mutableStateOf(false) // should replace isRunning function with this
    var dropdownExpanded by mutableStateOf(false)
    var currSeconds by mutableStateOf(0)
    private val chronometer = Chronometer()

    /**
     * List page properties
     */
    // save current editing index in here
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
    fun isRunning(): Boolean {
        val currEvent = currentTimeClockEvent.value
        return currEvent != null && currEvent.isRunning
    }

    private suspend fun getCurrentEventFromDatabase(): TimeClockEvent? {
        val event = database.getCurrentEvent()
        if (event?.endTime != event?.startTime) {
            return null // because this event has already been completed
        }
        return event
    }

    fun onTaskNameChange(taskName: String) {
        taskTextFieldValue = TextFieldValue(text = taskName)
        clockButtonEnabled = taskName.isNotBlank()
        dropdownExpanded = taskName.isNotBlank()
        Log.i("ViewModel", """
            onTaskNameExecuted
            input: $taskName
            taskTextFieldValue.text: ${taskTextFieldValue.text}
            clockButtonEnabled: $clockButtonEnabled
            dropdownExpanded: $dropdownExpanded
        """.trimIndent())
    }

    fun onTaskNameDonePressed() {
        dropdownExpanded = false
        Log.i("ViewModel", """
            onTaskNameDonePressed executed
            dropdownExpanded: $dropdownExpanded
        """.trimIndent())
    }

    fun onDismissDropdown() {
        dropdownExpanded = false
        Log.i("ViewModel", """
            onDismissDropdown executed
            dropdownExpanded: $dropdownExpanded
        """.trimIndent())
    }

    fun onDropdownMenuItemClick(label: String) {
        taskTextFieldValue = TextFieldValue(
            text = label,
            selection = TextRange(label.length)
        )
        dropdownExpanded = false
        Log.i("ViewModel", """
            onDropdownMenuItemClick executed
            input: $label
            taskTextFieldValue.text: ${taskTextFieldValue.text}
            taskTextFieldValue.selection: ${taskTextFieldValue.selection}
            dropdownExpanded: $dropdownExpanded
        """.trimIndent())
    }

    fun startClock() {
        Log.i("ViewModel", "onStartClock executed")
        chronometer.start()
        // create current model
        viewModelScope.launch {
            val newEvent = TimeClockEvent(
                name = taskName // change this to the taskTextFieldValue.text property
            )
            database.insert(newEvent)
            currentTimeClockEvent.value = getCurrentEventFromDatabase()
        }
    }

    fun stopClock() {
        Log.i("ViewModel", "onStopClock executed")
        chronometer.stop()
        // update current model
        viewModelScope.launch {
            val finishedEvent = currentTimeClockEvent.value ?: return@launch
            finishedEvent.endTime = System.currentTimeMillis()
            database.update(finishedEvent)
            showToast("Task \"$taskName\" saved!")
            currentTimeClockEvent.value = null
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