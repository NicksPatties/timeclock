package com.nickspatties.timeclock.ui.viewmodel

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import com.nickspatties.timeclock.MainActivity
import com.nickspatties.timeclock.R
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.data.TimeClockEventDao
import com.nickspatties.timeclock.data.UserPreferences
import com.nickspatties.timeclock.data.UserPreferencesRepository
import com.nickspatties.timeclock.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

const val TAG = "ClockPageViewModel"

class ClockPageViewModel (
    private val database: TimeClockEventDao,
    application: Application,
    private val userPreferencesRepository: UserPreferencesRepository
): AndroidViewModel(application) {

    private val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow

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

    private val TIMER_COMPLETE_NOTIFICATION_ID = 1
    private var timerCompleteNotification = NotificationCompat.Builder(
        getApplication(),
        getApplication<Application>().getString(R.string.alarm_channel_id)
    )
        .setSmallIcon(R.drawable.ic_baseline_clock_24)
        .setContentTitle("Timer complete!")
        .setContentText("You have finished your task. Good job!")
        .setContentIntent(pendingMainIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    // countdown specific variables
    var countDownTimerEnabled by mutableStateOf(false)
    var countDownEndTime by mutableStateOf(0L)
    var currCountDownSeconds by mutableStateOf(0)
    private val countDownChronometer = Chronometer()

    init {
        chronometer.setOnChronometerTickListener {
            currSeconds = calculateCurrSeconds(currentTimeClockEvent.value)
        }
        countDownChronometer.setOnChronometerTickListener {
            currCountDownSeconds -= 1
            if (currCountDownSeconds <= 0) {
                stopClock()
            }
        }
        viewModelScope.launch {

            val pooPreferences = userPreferencesFlow.first()
            countDownTimerEnabled = pooPreferences.countDownEnabled
            countDownEndTime = pooPreferences.countDownEndTime

            // initialize the currentEvent in case the app was closed while counting
            val currEvent = getCurrentEventFromDatabase()

            // if there's an event that's already running, populate the UI with that event's data
            if (currEvent != null) {
                currentTimeClockEvent.value = currEvent
                taskTextFieldValue = TextFieldValue(text = currEvent.name)
                clockButtonEnabled = true
                isClockRunning = true
                val startTimeDelay = findEventStartTimeDelay(currEvent.startTime)
                if (countDownTimerEnabled) {
                    currCountDownSeconds = ((countDownEndTime - System.currentTimeMillis()) / 1000).toInt()
                    countDownChronometer.start(startTimeDelay)
                } else {
                    currSeconds = calculateCurrSeconds(currEvent)
                    chronometer.start(startTimeDelay)
                }
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
            currentTimeClockEvent.value = getCurrentEventFromDatabase()
            if (countDownTimerEnabled) {
                // save the countdown time in user preferences
                val upcomingEndTime = System.currentTimeMillis() + currCountDownSeconds * 1000L
                userPreferencesRepository.updateCountDownEndTime(upcomingEndTime)
                countDownChronometer.start()
            } else {
                chronometer.start()
            }
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
            countDownChronometer.stop()
            // saving...
            database.update(finishedEvent)
            // successfully saved!
            val saved = getApplication<Application>().applicationContext
                .getString(R.string.task_saved_toast, taskTextFieldValue.text)
            currentTimeClockEvent.value = null
            isClockRunning = false
            notificationManager.cancelAll()
            if (countDownTimerEnabled) {
                timerCompleteNotification
                    .setContentText("${finishedEvent.name} completed! Good work!")
                notificationManager.notify(
                    TIMER_COMPLETE_NOTIFICATION_ID,
                    timerCompleteNotification.build()
                )
            } else {
                showToast(saved)
            }
        }
    }

    fun resetCurrSeconds() {
        currSeconds = 0
    }

    fun updateCountdownValues(hoursString: String, minutesString: String, secondsString: String) {
        currCountDownSeconds = convertHoursMinutesSecondsToSeconds(
            hoursString.toInt(),
            minutesString.toInt(),
            secondsString.toInt()
        )
    }

    fun switchCountdownTimer() {
        viewModelScope.launch {
            userPreferencesRepository.updateCountDownEnabled(!countDownTimerEnabled)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }
}
