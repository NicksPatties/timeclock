package com.nickspatties.timeclock.ui.viewmodel

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nickspatties.timeclock.ClockService
import com.nickspatties.timeclock.MainActivity
import com.nickspatties.timeclock.R
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.data.TimeClockEventDao
import com.nickspatties.timeclock.util.Chronometer
import com.nickspatties.timeclock.util.calculateCurrSeconds
import com.nickspatties.timeclock.util.findEventStartTimeDelay
import com.nickspatties.timeclock.util.getNotificationManager
import kotlinx.coroutines.launch

class ClockPageViewModel (
    private val database: TimeClockEventDao,
    application: Application
): AndroidViewModel(application) {

    private var currentTimeClockEvent = MutableLiveData<TimeClockEvent?>()
    var taskTextFieldValue by mutableStateOf(TextFieldValue(text = ""))
    var clockButtonEnabled by mutableStateOf(false)
    var isClockRunning by mutableStateOf(false)
    var dropdownExpanded by mutableStateOf(false)
    var currSeconds by mutableStateOf(0)

    // cheeky var used to prevent onTaskNameChange from being called after onDropdownMenuItemClick
    private var dropdownClicked = false
    private val chronometer = Chronometer()

    // main activity to return the user to the application on click
    val mainIntent = Intent(getApplication(), MainActivity::class.java)
    val pendingMainIntent = PendingIntent.getActivity(
        getApplication(),
        0,
        mainIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // notifications
    private var notificationManager = getNotificationManager(getApplication())
    private val CLOCK_IN_PROGRESS_NOTIFICATION_ID = 0
    private var clockInProgressNotification = NotificationCompat.Builder(
        getApplication(),
        getApplication<Application>().getString(R.string.clock_channel_id)
    )
        .setSmallIcon(R.drawable.ic_baseline_clock_24)
        .setContentTitle("Recording in progress")
        .setContentIntent(pendingMainIntent)
        .setOngoing(true)
        .setPriority(NotificationCompat.PRIORITY_LOW)

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
                clockInProgressNotification.setContentText(currEvent.name)
                notificationManager.notify(
                    CLOCK_IN_PROGRESS_NOTIFICATION_ID,
                    clockInProgressNotification.build()
                )
            }
        }
    }

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
            clockInProgressNotification.setContentText(newEvent.name)
            notificationManager.notify(
                CLOCK_IN_PROGRESS_NOTIFICATION_ID,
                clockInProgressNotification.build()
            )
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
            val saved = getApplication<Application>().applicationContext
                .getString(R.string.task_saved_toast, taskTextFieldValue.text)
            showToast(saved)
            currentTimeClockEvent.value = null
            isClockRunning = false
            notificationManager.cancelAll()
        }
    }

    fun resetCurrSeconds() {
        currSeconds = 0
    }

    private fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }
}
