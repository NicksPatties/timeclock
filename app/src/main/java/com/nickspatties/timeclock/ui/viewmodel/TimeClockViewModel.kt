package com.nickspatties.timeclock.ui.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.*
import com.nickspatties.timeclock.R
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.data.TimeClockEventDao
import com.nickspatties.timeclock.util.*
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
    private var currentTimeClockEvent = MutableLiveData<TimeClockEvent?>()
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
    val groupedEventsByDate = Transformations.map(timeClockEvents) { events ->
        val listRows: List<ListRow> = events.map {
            ListRow(it.name, it.startTime, it.endTime, it.id)
        }
        listRows.groupBy {
            decorateMillisToDateString(it.startTime)
        }
    }
    var editingEventId by mutableStateOf(-1L)

    /**
     * Analysis page properties
     */
    val allTimeAnalysisPane = AnalysisPane(
        eventData = timeClockEvents,
        rowTransformation = ::sortByNamesAndTotalMillis,
        rangeName = "All time"
    )
    val todayAnalysisPane = AnalysisPane(
        eventData = timeClockEvents,
        rowTransformation = ::sortByNamesAndTotalMillis,
        rangeName = "Today",
        daysInRange = 1
    )
    val lastWeekAnalysisPane = AnalysisPane(
        eventData = timeClockEvents,
        rowTransformation = ::sortByNamesAndTotalMillis,
        rangeName = "Last week",
        daysInRange = 7
    )
    val lastMonthAnalysisPane = AnalysisPane(
        eventData = timeClockEvents,
        rowTransformation = ::sortByNamesAndTotalMillis,
        rangeName = "Last month",
        daysInRange = 30
    )
    var analysisPanes = listOf(
        allTimeAnalysisPane,
        todayAnalysisPane,
        lastWeekAnalysisPane,
        lastMonthAnalysisPane
    )
    private var currDateRangeIndex = 0
    var currAnalysisPane by mutableStateOf(analysisPanes[currDateRangeIndex])


    init {
        chronometer.setOnChronometerTickListener {
            currSeconds = calculateCurrSeconds(currentTimeClockEvent.value)
        }
        viewModelScope.launch {
            // initialize the currentEvent in case the app was closed while counting
            val currEvent = getCurrentEventFromDatabase()

            // if there's an event that's already running, populate the UI with that event's data
            if (currEvent != null) {
                currentTimeClockEvent.value = currEvent
                taskTextFieldValue = TextFieldValue(text = currEvent.name)
                currSeconds = calculateCurrSeconds(currEvent)
                clockButtonEnabled = true
                isClockRunning = true
                val startTimeDelay = findEventStartTimeDelay(currEvent.startTime)
                chronometer.start(startTimeDelay)
            }
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
            chronometer.stop()
            // saving...
            database.update(finishedEvent)
            // successfully saved!
            showToast("Task \"${taskTextFieldValue.text}\" saved!")
            currentTimeClockEvent.value = null
            isClockRunning = false
        }
    }

    fun resetCurrSeconds() {
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

    fun deleteEvent(id: Long) {
        viewModelScope.launch {
            val eventToDelete = database.get(id)
            if(eventToDelete != null) {
                database.delete(eventToDelete)
                editingEventId = -1
            } else {
                showToast("Failed to delete event")
            }
        }
    }

    /**
     * Analysis Page functions
     */
    fun onDateRangeStartButtonClick() {
        if (currDateRangeIndex > 0) {
            updateAnalysisPane(currDateRangeIndex - 1)
        }
    }

    fun isDateRangeStartButtonVisible(): Boolean {
        return currDateRangeIndex > 0
    }

    fun onDateRangeEndButtonClick() {
        if (currDateRangeIndex < analysisPanes.size - 1) {
            updateAnalysisPane(currDateRangeIndex + 1)
        }
    }

    fun isDateRangeEndButtonVisible(): Boolean {
        return currDateRangeIndex < analysisPanes.size - 1
    }

    fun updateAnalysisPane(index: Int) {
        // reset selected row for curr pane
        analysisPanes[currDateRangeIndex].selectedAnalysisRowId = -1
        // change the current index
        currDateRangeIndex = index
        currAnalysisPane = analysisPanes[currDateRangeIndex]
    }
}

sealed class Screen(
    @StringRes val routeResourceId: Int,
    @StringRes val labelResourceId: Int,
    @DrawableRes val iconResourceId: Int
) {
    object Clock :
        Screen(R.string.route_clock, R.string.label_clock, R.drawable.ic_baseline_clock_24)

    object List :
        Screen(R.string.route_list, R.string.label_list, R.drawable.ic_baseline_list_24)

    object Metrics :
        Screen(R.string.route_metrics, R.string.label_metrics, R.drawable.ic_baseline_pie_chart_24)
}
