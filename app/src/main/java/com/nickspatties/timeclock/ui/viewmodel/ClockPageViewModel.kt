package com.nickspatties.timeclock.ui.viewmodel

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.nickspatties.timeclock.R
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.data.TimeClockEventDao
import com.nickspatties.timeclock.data.UserPreferencesRepository
import com.nickspatties.timeclock.receiver.AlarmReceiver
import com.nickspatties.timeclock.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

const val TAG = "ClockPageViewModel"

class ClockPageViewModel (
    application: Application,
    private val database: TimeClockEventDao,
    private val timeClockEvents: LiveData<List<TimeClockEvent>>,
    private val userPreferencesRepository: UserPreferencesRepository
): AndroidViewModel(application) {

    val state: ClockPageViewModelState = ClockPageViewModelState()

    val autofillTaskNames = Transformations.map(timeClockEvents) { events ->
        state.autofillTaskNames = events.map {
            it.name
        }.toSet()
        state.autofillTaskNames
    }

    private val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow
    private var currentTimeClockEvent : TimeClockEvent? = null
    private var currSeconds: Int = 0
    private var countDownEndTime: Long = 0L
    private var currCountDownSeconds: Int = 0

    private val chronometer = Chronometer().apply {
        setOnChronometerTickListener { countUp() }
    }
    private val countDownChronometer = Chronometer().apply {
        setOnChronometerTickListener { countDown() }
    }

    // alarm intent, used to notify the AlarmReceiver when an event is done recording
    private val alarmIntent = Intent(getApplication(), AlarmReceiver::class.java)
    private var pendingAlarmIntent = PendingIntent.getBroadcast(
        getApplication(),
        0,
        alarmIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Android system managers
    private var notificationManager = getNotificationManager(getApplication())
    private val alarmManager =
        getApplication<Application>().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val powerManager =
        getApplication<Application>().getSystemService(Context.POWER_SERVICE) as PowerManager

    // only occurs when the class is created, not when moving from view to view
    init {
        // state function declarations
        state.checkBatteryOptimizationSettings = this::checkBatteryOptimizationSettings
        state.startBatteryManagementActivity = this::goToBatterySettings
        state.saveCountDownTimerEnabledValue = this::saveCountDownTimerEnabled
        state.saveEventDataOnStart = this::startClock
        state.saveEventDataOnStop = this::stopClock

        notificationManager.cancelAll()

        viewModelScope.launch {
            val preferences = userPreferencesFlow.first()
            state.countDownTimerEnabled = preferences.countDownEnabled
            countDownEndTime = preferences.countDownEndTime

            // initialize the currentEvent in case the app was closed while counting
            val currEvent = getCurrentEventFromDatabase()

            // if there's an event that's already running, populate the UI with that event's data
            if (currEvent != null) {
                currentTimeClockEvent = currEvent
                state.taskTextFieldValue = TextFieldValue(text = currEvent.name)
                state.isClockRunning = true
                val startTimeDelay = findEventStartTimeDelay(currEvent.startTime)
                if (state.countDownTimerEnabled) {
                    // if the countDown end has already passed, save the event and reset clock
                    if (countDownEndTime < System.currentTimeMillis()) {
                        currEvent.endTime = countDownEndTime
                        database.update(currEvent)
                        currentTimeClockEvent = null
                    } else { // init countdown
                        currCountDownSeconds = calculateCurrCountDownSeconds(countDownEndTime)
                        state.updateCountDownTextFieldValues(currCountDownSeconds)
                        countDownChronometer.start(startTimeDelay)
                    }
                } else {
                    currSeconds = calculateCurrSeconds(currEvent)
                    chronometer.start(startTimeDelay)
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
        return if (event == null || !event.isRunning) {
            null
        } else {
            event
        }
    }

    private fun checkBatteryOptimizationSettings() : Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // can just check if the permission is set
            !powerManager.isIgnoringBatteryOptimizations(getApplication<Application?>().packageName)
        } else {
            // is unable to change the battery permission for versions below M, so only false
            false
        }
    }

    private fun saveCountDownTimerEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateCountDownEnabled(enabled)
        }
    }

    /**
     * Allow user to give TimeClock permission to run in the background unrestricted, which
     * is necessary for countdowns and exact alarms to run correctly. Pre-Marshmallow devices
     * will not open the battery warning modal, so this function will just be a no-op in those cases
     */
    @SuppressLint("BatteryLife")
    fun goToBatterySettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // modal appears asking user to ignore battery optimizations, but may violate Google Play Requirements
            val intentBatteryUsage = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intentBatteryUsage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intentBatteryUsage.data =
                Uri.parse("package:" + getApplication<Application?>().packageName)
            startActivity(getApplication(), intentBatteryUsage, null)
        }
    }

    private fun timerTextFieldValuesToSeconds(): Int {
        return convertHoursMinutesSecondsToSeconds(
            state.hoursTextFieldValue.text.toInt(),
            state.minutesTextFieldValue.text.toInt(),
            state.secondsTextFieldValue.text.toInt()
        )
    }

    fun startClock() {
        viewModelScope.launch {
            // create and save the new event
            val newEvent = TimeClockEvent(
                name = state.taskTextFieldValue.text
            )
            database.insert(newEvent)
            currentTimeClockEvent = getCurrentEventFromDatabase()
            notificationManager.sendClockInProgressNotification(
                getApplication(),
                newEvent.name
            )
            if (state.countDownTimerEnabled) {
                startCountDown(newEvent.name, newEvent.startTime)
            } else {
                chronometer.start()
            }
        }
    }

    private suspend fun startCountDown(taskName: String, actualStartTime: Long) {
        alarmIntent.putExtra("taskName", taskName)
        // update the pendingAlarmIntent to capture the updated alarmIntent with extras
        pendingAlarmIntent = PendingIntent.getBroadcast(
            getApplication(),
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        currCountDownSeconds = timerTextFieldValuesToSeconds()
        val upcomingEndTime = actualStartTime + currCountDownSeconds * MILLIS_PER_SECOND
        countDownEndTime = upcomingEndTime
        userPreferencesRepository.updateCountDownEndTime(upcomingEndTime) // save to memory in case app closes
        // start an alarm
        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            upcomingEndTime,
            pendingAlarmIntent
        )
        countDownChronometer.start()
    }

    fun stopClock(tappedStopButton: Boolean) {
        viewModelScope.launch {
            val finishedEvent = currentTimeClockEvent ?: return@launch
            finishedEvent.endTime = System.currentTimeMillis()
            chronometer.stop()
            countDownChronometer.stop()
            database.update(finishedEvent)
            // successfully saved! reset values to initial
            notificationManager.cancelClockInProgressNotification()
            currentTimeClockEvent = null
            val saved = getApplication<Application>().applicationContext
                .getString(R.string.task_saved_toast, state.taskTextFieldValue.text)
            if (state.countDownTimerEnabled) {
                stopCountDown(tappedStopButton, saved)
            } else {
                showToast(saved)
            }
        }
    }

    private suspend fun stopCountDown(tappedStopButton: Boolean, message: String) {
        if (tappedStopButton) {
            alarmManager.cancel(pendingAlarmIntent)
            showToast(message)
        }
        countDownEndTime = 0L
        userPreferencesRepository.updateCountDownEndTime(countDownEndTime)
        currCountDownSeconds = 0
        state.isClockRunning = false
    }

    private fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }

    private fun countUp() {
        currSeconds = calculateCurrSeconds(currentTimeClockEvent)
        state.updateCurrSeconds(currSeconds)
    }

    private fun countDown() {
        currCountDownSeconds = getCountDownSeconds(
            countDownEndTime = countDownEndTime,
            stopClockFunc = this::stopClock
        )
        state.updateCountDownTextFieldValues(currCountDownSeconds)
    }
}

/**
 * Gets the current count down seconds based on an end time. If the end time has
 * already passed, then call the stopClock function. Updates the text with the
 * updateFields function.
 *
 * This really allows me to quickly test the fix for this bug:
 * https://github.com/NicksPatties/timeclock/issues/12
 */
fun getCountDownSeconds(
    countDownEndTime: Long = 0L,
    stopClockFunc: (Boolean) -> Unit = {}
): Int {
    var currSeconds = calculateCurrCountDownSeconds(countDownEndTime)
    if(currSeconds <= 0) {
        stopClockFunc(false)
        currSeconds = 0
    }
    return currSeconds
}

/**
 * These are the things that are passed directly into the ClockPage
 * component. This doesn't contain things like activities, service
 * managers, and so on. This allows us to test ClockPage component
 * without needing to build the entire ViewModel
 *
 * @param taskTextFieldValue The text field value that appears in the TaskTextField component
 * @param isClockRunning Determines if the clock button should say Start or Stop
 * @param dropdownExpanded The dropdown below the TaskTextField component is open or closed
 * @param currSeconds The current amount of seconds that have been counted up, displayed in the
 * TimerText component
 * @param batteryWarningDialogVisible Determines if the battery warning dialog is visible
 * @param countDownTimerEnabled Determines if the count down timer should be visible, allowing
 * the user to set a time that an event will last.
 * @param hoursTextFieldValue The text in the hours section of the EditTimerTextField
 * @param minutesTextFieldValue The text in the minutes section of the EditTimerTextField. Should
 * not exceed 59
 * @param secondsTextFieldValue The text in the minutes section of the EditTimerTextField. Should
 * not exceed 59
 * @param checkBatteryOptimizationSettings Verifies whether or not the TimeClock has unrestricted
 * battery permission. If it doesn't, then the battery warning dialog should appear.
 * @param startBatteryManagementActivity Starts battery management activity
 * @param saveCountDownTimerEnabledValue Saves the countDownTimerEnabled UserPreference variable, so
 * the count down timer's visibility persists on activity recreation
 * @param onDismissDropdown Fires when the dropdown in the TaskTextField is dismissed by
 * tapping outside it
 * @param onTimerAnimationFinish Fires when the count up timer fades in and out when starting
 * recording
 * @param saveEventDataOnStart Handles saving of event and repo data when the event starts
 * @param saveEventDataOnStop Handles saving of event and repo data when the event ends
 */
class ClockPageViewModelState(
    taskTextFieldValue: TextFieldValue = TextFieldValue(""),
    autofillTaskNames: Set<String> = setOf(),
    isClockRunning: Boolean = false,
    dropdownExpanded: Boolean = false,
    currSeconds: Int = 0,
    batteryWarningDialogVisible: Boolean = false,
    countDownTimerEnabled: Boolean = false,
    hoursTextFieldValue: TextFieldValue = TextFieldValue(
        text = "00",
        selection = TextRange(0)
    ),
    minutesTextFieldValue: TextFieldValue = TextFieldValue(
        text = "00",
        selection = TextRange(0)
    ),
    secondsTextFieldValue: TextFieldValue = TextFieldValue(
        text = "00",
        selection = TextRange(0)
    ),
    var checkBatteryOptimizationSettings: () -> Boolean = {false},
    var startBatteryManagementActivity: () -> Unit = {},
    var saveCountDownTimerEnabledValue: (Boolean) -> Unit = {_ -> },
    var onDismissDropdown: () -> Unit = {},
    var onTimerAnimationFinish: () -> Unit = {},
    var saveEventDataOnStart: () -> Unit = { },
    var saveEventDataOnStop: (Boolean) -> Unit = { _ -> },
) {
    val clockButtonEnabled: Boolean
        get() {
            return if (countDownTimerEnabled) {
                val countDownClockIsZero =
                    hoursTextFieldValue.text.toInt() == 0 &&
                            minutesTextFieldValue.text.toInt() == 0 &&
                            secondsTextFieldValue.text.toInt() == 0
                taskTextFieldValue.text.isNotBlank() && !countDownClockIsZero
            } else {
                taskTextFieldValue.text.isNotBlank()
            }
        }
    val filteredEventNames: List<String>
        get() {
            return autofillTaskNames.filter {
                it.contains(taskTextFieldValue.text)
            }
        }
    var taskTextFieldValue by mutableStateOf(taskTextFieldValue)
    var autofillTaskNames by mutableStateOf(autofillTaskNames)
    var isClockRunning by mutableStateOf(isClockRunning)
    var dropdownExpanded by mutableStateOf(dropdownExpanded)
    // could use a TextFieldValue here, but it's easier to update the timer component with seconds
    var currSeconds by mutableStateOf(currSeconds)
    var batteryWarningDialogVisible by mutableStateOf(batteryWarningDialogVisible)
    var countDownTimerEnabled by mutableStateOf(countDownTimerEnabled)
    var hoursTextFieldValue by mutableStateOf(hoursTextFieldValue)
    var minutesTextFieldValue by mutableStateOf(minutesTextFieldValue)
    var secondsTextFieldValue by mutableStateOf(secondsTextFieldValue)
    // cheeky var used to prevent onTaskNameChange from being called after onDropdownMenuItemClick
    private var dropdownClicked = false

    fun dismissBatteryWarningDialog() {
        batteryWarningDialogVisible = false
    }

    fun confirmBatteryWarningDialog() {
        startBatteryManagementActivity()
        batteryWarningDialogVisible = false
    }

    fun onTaskTextFieldIconClick() {
        val shouldWarn = checkBatteryOptimizationSettings()
        if (!countDownTimerEnabled && shouldWarn) {
            batteryWarningDialogVisible = true
        } else {
            countDownTimerEnabled = !countDownTimerEnabled
            saveCountDownTimerEnabledValue(countDownTimerEnabled)
        }
    }

    fun onTaskNameChange(tfv: TextFieldValue) {
        if (dropdownClicked) {
            dropdownClicked = false
            return
        }
        taskTextFieldValue = tfv
        val taskName = tfv.text
        dropdownExpanded = taskName.isNotBlank() && filteredEventNames.isNotEmpty()
    }

    fun dismissDropdown() {
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

    fun onMinutesValueChanged(value: TextFieldValue) {
        minutesTextFieldValue = onMinutesOrSecondsValueChanged(value)
    }

    fun onSecondsValueChanged(value: TextFieldValue) {
        secondsTextFieldValue = onMinutesOrSecondsValueChanged(value)
    }

    private fun onMinutesOrSecondsValueChanged(value: TextFieldValue): TextFieldValue {
        return when (value.text.length) {
            0 -> cursorAtEnd(value)
            1 -> {
                if (value.text.toInt() >= 6) {
                    selectAllValue(value)
                } else {
                    cursorAtEnd(value)
                }
            }
            2 -> selectAllValue(value)
            else -> TextFieldValue()
        }
    }

    fun onHoursValueChanged(value: TextFieldValue) {
        hoursTextFieldValue = when (value.text.length) {
            0 -> cursorAtEnd(value)
            1 -> cursorAtEnd(value)
            2 -> selectAllValue(value)
            else -> TextFieldValue()
        }
    }

    private fun onTimerStringFocusChanged(
        focusState: FocusState,
        textFieldValue: TextFieldValue
    ) : TextFieldValue {
        return if(focusState.isFocused) {
            // select all text when focusing
            selectAllValue(textFieldValue)
        } else {
            // format the digits when leaving focus and remove selection
            TextFieldValue(
                text = formatDigitsAfterLeavingFocus(textFieldValue.text),
                selection = TextRange(0)
            )
        }
    }

    fun onHoursFocusChanged(focusState: FocusState) {
        hoursTextFieldValue = onTimerStringFocusChanged(focusState, hoursTextFieldValue)
    }

    fun onMinutesFocusChanged(focusState: FocusState) {
        minutesTextFieldValue = onTimerStringFocusChanged(focusState, minutesTextFieldValue)
    }

    fun onSecondsFocusChanged(focusState: FocusState) {
        secondsTextFieldValue = onTimerStringFocusChanged(focusState, secondsTextFieldValue)
    }

    fun updateCurrSeconds(seconds: Int) {
        currSeconds = seconds
    }

    fun updateCountDownTextFieldValues(currSeconds: Int) {
        val hms = convertSecondsToHoursMinutesSeconds(currSeconds)
        hoursTextFieldValue = TextFieldValue(
            text = formatDigitsAfterLeavingFocus(hms.first.toString())
        )
        minutesTextFieldValue = TextFieldValue(
            text = formatDigitsAfterLeavingFocus(hms.second.toString())
        )
        secondsTextFieldValue = TextFieldValue(
            text = formatDigitsAfterLeavingFocus(hms.third.toString())
        )
    }

    private fun formatDigitsAfterLeavingFocus(digits: String): String {
        if (digits.isEmpty()) return "00"
        if (digits.length > 1) return digits
        return "0$digits"
    }

    // TODO may need to make onClockStart and onClockStop suspend functions so they can properly
    // respond to the output of saveEventDataOnStart and saveEventDataOnStop
    fun onClockStart() {
        saveEventDataOnStart()
        currSeconds = 0
        isClockRunning = true
    }

    fun onClockStop() {
        saveEventDataOnStop(true)
        isClockRunning = false
    }
}
