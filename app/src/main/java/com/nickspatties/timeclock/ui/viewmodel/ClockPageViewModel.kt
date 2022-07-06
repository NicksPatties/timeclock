package com.nickspatties.timeclock.ui.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.app.AlarmManagerCompat
import androidx.lifecycle.*
import com.nickspatties.timeclock.R
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.data.TimeClockEventDao
import com.nickspatties.timeclock.data.UserPreferencesRepository
import com.nickspatties.timeclock.receiver.AlarmReceiver
import com.nickspatties.timeclock.util.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

const val TAG = "ClockPageViewModel"


class ClockPageViewModel (
    private val database: TimeClockEventDao,
    application: Application,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val timeClockEvents: LiveData<List<TimeClockEvent>>
): AndroidViewModel(application) {

    val autofillTaskNames = Transformations.map(timeClockEvents) { events ->
        events.map {
            it.name
        }.toSet()
    }

    private val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow

    private var currentTimeClockEvent = MutableLiveData<TimeClockEvent?>()
    var taskTextFieldValue by mutableStateOf(TextFieldValue(text = ""))
    var clockButtonEnabled by mutableStateOf(false)
    var isClockRunning by mutableStateOf(false)
    var dropdownExpanded by mutableStateOf(false)
    var currSeconds by mutableStateOf(0)
    var filteredEventNames by mutableStateOf(listOf<String>())

    // cheeky var used to prevent onTaskNameChange from being called after onDropdownMenuItemClick
    private var dropdownClicked = false
    private val chronometer = Chronometer()

    // alarm intent, used to notify the AlarmReceiver when an event is done recording
    private val alarmIntent = Intent(getApplication(), AlarmReceiver::class.java)
    private lateinit var pendingAlarmIntent : PendingIntent

    // notifications
    private var notificationManager = getNotificationManager(getApplication())

    // countdown specific variables
    var countDownTimerEnabled by mutableStateOf(false)
    var countDownEndTime by mutableStateOf(0L)
    var currCountDownSeconds by mutableStateOf(0)

    // alarm manager
    private val alarmManager =
        getApplication<Application>().getSystemService(Context.ALARM_SERVICE) as AlarmManager

    init {
        notificationManager.cancelAll()
        setChronometerForCountUp()

        viewModelScope.launch {
            val preferences = userPreferencesFlow.first()
            countDownTimerEnabled = preferences.countDownEnabled
            countDownEndTime = preferences.countDownEndTime

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
                    setChronometerForCountDown()
                    chronometer.start(startTimeDelay)
                } else {
                    currSeconds = calculateCurrSeconds(currEvent)
                    startCountUp()
                }
                notificationManager.sendClockInProgressNotification(
                    application,
                    currEvent.name
                )
            }
            Log.i(TAG, "Finished loading")
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
        updateFilteredEventNames()
        clockButtonEnabled = taskName.isNotBlank()
        dropdownExpanded = taskName.isNotBlank() && filteredEventNames.isNotEmpty()
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
            // create and save the new event
            val newEvent = TimeClockEvent(
                name = taskTextFieldValue.text
            )
            database.insert(newEvent)
            currentTimeClockEvent.value = getCurrentEventFromDatabase()

            if (countDownTimerEnabled) {
                startCountDown(newEvent.name)
            } else {
                startCountUp()
            }

            isClockRunning = true
            notificationManager.sendClockInProgressNotification(
                getApplication(),
                newEvent.name
            )
        }
    }

    /**
     * When I start counting up, I should
     * start the chronometer
     * start the notification
     */
    private fun startCountUp() {
        setChronometerForCountUp()
        chronometer.start()
    }

    private suspend fun startCountDown(taskName: String) {
        alarmIntent.putExtra("taskName", taskName)
        pendingAlarmIntent = PendingIntent.getBroadcast(
            getApplication(),
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val upcomingEndTime = System.currentTimeMillis() + currCountDownSeconds * 1000L
        countDownEndTime = upcomingEndTime
        userPreferencesRepository.updateCountDownEndTime(upcomingEndTime) // save to memory in case app closes
        // start an alarm
        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            upcomingEndTime,
            pendingAlarmIntent
        )
        setChronometerForCountDown()
        chronometer.start()
    }

    fun stopClock() {
        viewModelScope.launch {
            val finishedEvent = currentTimeClockEvent.value ?: return@launch
            finishedEvent.endTime = System.currentTimeMillis()
            chronometer.stop()
            database.update(finishedEvent)
            // successfully saved! reset values to initial
            notificationManager.cancelClockInProgressNotification()
            currentTimeClockEvent.value = null
            isClockRunning = false

            val saved = getApplication<Application>().applicationContext
                .getString(R.string.task_saved_toast, taskTextFieldValue.text)

            if (countDownTimerEnabled) {
                stopCountDown(finishedEvent.endTime, saved)
            } else {
                stopCountUp(saved)
            }
        }
    }

    private fun stopCountUp(message: String) {
        showToast(message)
    }

    private fun stopCountDown(actualEndTime: Long, message: String) {
        if (actualEndTime < countDownEndTime) {
            alarmManager.cancel(pendingAlarmIntent)
            showToast(message)
        }
        countDownEndTime = 0L
        currCountDownSeconds = 0
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
        countDownTimerEnabled = !countDownTimerEnabled
        viewModelScope.launch {
            userPreferencesRepository.updateCountDownEnabled(countDownTimerEnabled)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }

    private fun updateFilteredEventNames() {
        filteredEventNames = if (autofillTaskNames.value == null) {
            listOf()
        } else {
            autofillTaskNames.value!!.filter {
                it.contains(taskTextFieldValue.text)
            }
        }
    }

    private fun setChronometerForCountUp() {
            chronometer.setOnChronometerTickListener { countUp() }
    }

    private fun countUp() {
        currSeconds = calculateCurrSeconds(currentTimeClockEvent.value)
    }

    private fun setChronometerForCountDown() {
        chronometer.setOnChronometerTickListener { countDown() }
    }

    private fun countDown() {
        if (currCountDownSeconds > 0) {
            currCountDownSeconds -= 1
            if (currCountDownSeconds <= 0) {
                stopClock()
            }
        }
    }
}
