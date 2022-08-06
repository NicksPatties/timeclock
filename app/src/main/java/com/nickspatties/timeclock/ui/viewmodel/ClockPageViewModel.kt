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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

const val TAG = "ClockPageViewModel"

class ClockPageViewModel (
    application: Application,
    private val database: TimeClockEventDao,
    private val timeClockEvents: LiveData<List<TimeClockEvent>>,
    private val userPreferencesRepository: UserPreferencesRepository
): AndroidViewModel(application) {

    val state: ClockPageViewModelState = ClockPageViewModelState()

    val autofillTaskNames = Transformations.map(timeClockEvents) { events ->
        events.map {
            it.name
        }.toSet()
    }

    private val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow

    private var currentTimeClockEvent : TimeClockEvent? = null

    // cheeky var used to prevent onTaskNameChange from being called after onDropdownMenuItemClick
    private var dropdownClicked = false
    private val chronometer = Chronometer().apply {
        setOnChronometerTickListener { countUp() }
    }

    // alarm intent, used to notify the AlarmReceiver when an event is done recording
    private val alarmIntent = Intent(getApplication(), AlarmReceiver::class.java)
    private var pendingAlarmIntent = PendingIntent.getBroadcast(
        getApplication(),
        0,
        alarmIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // notifications
    private var notificationManager = getNotificationManager(getApplication())

    // countdown specific variables

    private val countDownChronometer = Chronometer().apply {
        setOnChronometerTickListener { countDown() }
    }

    // Android system managers
    private val alarmManager =
        getApplication<Application>().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val powerManager =
        getApplication<Application>().getSystemService(Context.POWER_SERVICE) as PowerManager

    // only occurs when the class is created, not when moving from view to view
    init {
        // state function declarations
        state.batteryWarningConfirmFunction = this::goToBatterySettings
        state.batteryWarningDismissFunction = this::hideBatteryWarningModal
        state.onTaskNameChange = this::onTaskNameChange
        state.onTaskNameDone = this::onTaskNameDonePressed
        state.onTaskNameIconClick = this::switchCountDownTimer
        state.onDismissDropdown = this::onDismissDropdown
        state.onDropdownMenuItemClick = this::onDropdownMenuItemClick
        state.onTimerAnimationFinish = this::resetCurrSeconds
        state.onClockStart = this::startClock
        state.onClockStop = this::stopClock
        state.onHoursValueChanged = this::onHourValueChange
        state.onMinutesValueChanged = this::onMinuteValueChange
        state.onSecondsValueChanged = this::onSecondValueChange
        state.onHoursFocusChanged = this::onHoursFocusChanged
        state.onMinutesFocusChanged = this::onMinutesFocusChanged
        state.onSecondsFocusChanged = this::onSecondsFocusChanged

        notificationManager.cancelAll()

        viewModelScope.launch {
            val preferences = userPreferencesFlow.first()
            state.countDownTimerEnabled = preferences.countDownEnabled
            state.countDownEndTime = preferences.countDownEndTime

            // initialize the currentEvent in case the app was closed while counting
            val currEvent = getCurrentEventFromDatabase()

            // if there's an event that's already running, populate the UI with that event's data
            if (currEvent != null) {
                currentTimeClockEvent = currEvent
                state.taskTextFieldValue = TextFieldValue(text = currEvent.name)
                state.clockButtonEnabled = true
                state.isClockRunning = true
                val startTimeDelay = findEventStartTimeDelay(currEvent.startTime)
                if (state.countDownTimerEnabled) {
                    // if the countDown end has already passed, save the event and reset clock
                    if (state.countDownEndTime < System.currentTimeMillis()) {
                        currEvent.endTime = state.countDownEndTime
                        database.update(currEvent)
                        currentTimeClockEvent = null
                        state.clockButtonEnabled = false
                    } else { // init countdown
                        state.currCountDownSeconds = calculateCurrCountDownSeconds(state.countDownEndTime)
                        updateCountDownTextFieldValues(state.currCountDownSeconds)
                        countDownChronometer.start(startTimeDelay)
                    }
                } else {
                    state.currSeconds = calculateCurrSeconds(currEvent)
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

    private fun checkClockButtonEnabled(): Boolean {
        val countDownClockIsZero =
            state.hoursTextFieldValue.text.toInt() == 0 &&
                    state.minutesTextFieldValue.text.toInt() == 0 &&
                    state.secondsTextFieldValue.text.toInt() == 0
        val enabled = if (state.countDownTimerEnabled) {
            state.taskTextFieldValue.text.isNotBlank() && !countDownClockIsZero
        } else {
            state.taskTextFieldValue.text.isNotBlank()
        }
        return enabled
    }

    fun onTaskNameChange(tfv: TextFieldValue) {
        if (dropdownClicked) {
            dropdownClicked = false
            return
        }
        state.taskTextFieldValue = tfv
        val taskName = tfv.text
        updateFilteredEventNames()
        state.clockButtonEnabled = checkClockButtonEnabled()
        state.dropdownExpanded = taskName.isNotBlank() && state.filteredEventNames.isNotEmpty()
    }

    fun onTaskNameDonePressed() {
        state.dropdownExpanded = false
    }

    private fun updateFilteredEventNames() {
        state.filteredEventNames = if (autofillTaskNames.value == null) {
            listOf()
        } else {
            autofillTaskNames.value!!.filter {
                it.contains(state.taskTextFieldValue.text)
            }
        }
    }

    fun onDismissDropdown() {
        state.dropdownExpanded = false
    }

    fun onDropdownMenuItemClick(label: String) {
        dropdownClicked = true
        state.taskTextFieldValue = TextFieldValue(
            text = label,
            selection = TextRange(label.length)
        )
        state.dropdownExpanded = false
    }

    fun switchCountDownTimer() {
        val shouldWarn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // can just check if the permission is set
            !powerManager.isIgnoringBatteryOptimizations(getApplication<Application?>().packageName)
        } else {
            // is unable to change the battery permission for versions below M, so only false
            false
        }

        if (!state.countDownTimerEnabled && shouldWarn) {
            state.batteryWarningDialogVisible = true
        } else {
            state.countDownTimerEnabled = !state.countDownTimerEnabled
            state.clockButtonEnabled = checkClockButtonEnabled()
            viewModelScope.launch {
                userPreferencesRepository.updateCountDownEnabled(state.countDownTimerEnabled)
            }
        }
    }

    fun hideBatteryWarningModal() {
        state.batteryWarningDialogVisible = false
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
        hideBatteryWarningModal()
    }

    fun onMinuteValueChange(value: TextFieldValue) {
        state.minutesTextFieldValue = onMinuteAndSecondValueChange(value)
    }

    fun onSecondValueChange(value: TextFieldValue) {
        state.secondsTextFieldValue = onMinuteAndSecondValueChange(value)
    }

    private fun onMinuteAndSecondValueChange(value: TextFieldValue): TextFieldValue {
        val selectAllValue = TextFieldValue(
            text = value.text,
            selection = TextRange(0, value.text.length)
        )
        val cursorAtEnd = TextFieldValue(
            text = value.text,
            selection = TextRange(value.text.length)
        )
        return when (value.text.length) {
            0 -> cursorAtEnd
            1 -> {
                if (value.text.toInt() >= 6) {
                    selectAllValue
                } else {
                    cursorAtEnd
                }
            }
            2 -> selectAllValue
            else -> TextFieldValue()
        }
    }

    fun onHourValueChange(value: TextFieldValue) {
        val selectAllValue = TextFieldValue(
            text = value.text,
            selection = TextRange(0, value.text.length)
        )
        val cursorAtEnd = TextFieldValue(
            text = value.text,
            selection = TextRange(value.text.length)
        )
        state.hoursTextFieldValue = when (value.text.length) {
            0 -> cursorAtEnd
            1 -> cursorAtEnd
            2 -> selectAllValue
            else -> TextFieldValue()
        }
    }

    private fun onTimerStringFocusChanged(
        focusState: FocusState,
        textFieldValue: TextFieldValue
    ) : TextFieldValue {
        state.clockButtonEnabled = checkClockButtonEnabled()
        return if(focusState.isFocused) {
            // select all text when focusing
            TextFieldValue(
                text = textFieldValue.text,
                selection = TextRange(0, 2)
            )
        } else {
            // format the digits when leaving focus and remove selection
            TextFieldValue(
                text = formatDigitsAfterLeavingFocus(textFieldValue.text),
                selection = TextRange(0)
            )
        }
    }

    fun onHoursFocusChanged(focusState: FocusState) {
        state.hoursTextFieldValue = onTimerStringFocusChanged(focusState, state.hoursTextFieldValue)
    }

    fun onMinutesFocusChanged(focusState: FocusState) {
        state.minutesTextFieldValue = onTimerStringFocusChanged(focusState, state.minutesTextFieldValue)
    }

    fun onSecondsFocusChanged(focusState: FocusState) {
        state.secondsTextFieldValue = onTimerStringFocusChanged(focusState, state.secondsTextFieldValue)
    }

    private fun formatDigitsAfterLeavingFocus(digits: String): String {
        if (digits.isEmpty()) return "00"
        if (digits.length > 1) return digits
        return "0$digits"
    }

    private fun updateCountDownTextFieldValues(currSeconds: Int) {
        val hms = convertSecondsToHoursMinutesSeconds(currSeconds)
        state.hoursTextFieldValue = TextFieldValue(
            text = formatDigitsAfterLeavingFocus(hms.first.toString())
        )
        state.minutesTextFieldValue = TextFieldValue(
            text = formatDigitsAfterLeavingFocus(hms.second.toString())
        )
        state.secondsTextFieldValue = TextFieldValue(
            text = formatDigitsAfterLeavingFocus(hms.third.toString())
        )
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
            state.isClockRunning = true
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
        state.currCountDownSeconds = timerTextFieldValuesToSeconds()
        val upcomingEndTime = actualStartTime + state.currCountDownSeconds * MILLIS_PER_SECOND
        state.countDownEndTime = upcomingEndTime
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
            state.isClockRunning = false
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
        state.countDownEndTime = 0L
        userPreferencesRepository.updateCountDownEndTime(state.countDownEndTime)
        state.currCountDownSeconds = 0
        state.clockButtonEnabled = checkClockButtonEnabled()
    }

    private fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Executed when count up timer finishes the fadeOut animation
     */
    fun resetCurrSeconds() {
        state.currSeconds = 0
    }

    private fun countUp() {
        state.currSeconds = calculateCurrSeconds(currentTimeClockEvent)
    }

    private fun countDown() {
        state.currCountDownSeconds = getCountDownSeconds(
            countDownEndTime = state.countDownEndTime,
            stopClockFunc = this::stopClock,
            updateFields = this::updateCountDownTextFieldValues
        )
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
    stopClockFunc: (Boolean) -> Unit = {},
    updateFields: (Int) -> Unit = {}
): Int {
    var currSeconds = calculateCurrCountDownSeconds(countDownEndTime)
    if(currSeconds <= 0) {
        stopClockFunc(false)
        currSeconds = 0
    }
    updateFields(currSeconds)
    return currSeconds
}

/**
 * These are the things that are passed directly into the ClockPage
 * component. This doesn't contain things like activities, service
 * managers, and so on. This allows us to test ClockPage component
 * without needing to build the entire ViewModel
 *
 * @param clockButtonEnabled The clock button is able to be clicked and is highlighted
 * @param taskTextFieldValue The text field value that appears in the TaskTextField component
 * @param isClockRunning Determines if the clock button should say Start or Stop
 * @param dropdownExpanded The dropdown below the TaskTextField component is open or closed
 * @param currSeconds The current amount of seconds that have been counted up, displayed in the
 * TimerText component
 * @param filteredEventNames The filtered task names that appear in the TaskTextField dropdown
 * @param batteryWarningDialogVisible Determines if the battery warning dialog is visible
 * @param countDownTimerEnabled Determines if the count down timer should be visible, allowing
 * the user to set a time that an event will last.
 * @param countDownEndTime Not sure if it'll be here in the future...
 * @param currCountDownSeconds Not sure if this'll be here either...
 * @param hoursTextFieldValue The text in the hours section of the EditTimerTextField
 * @param minutesTextFieldValue The text in the minutes section of the EditTimerTextField. Should
 * not exceed 59
 * @param secondsTextFieldValue The text in the minutes section of the EditTimerTextField. Should
 * not exceed 59
 * @param batteryWarningConfirmFunction Fires when tapping confirm button in BatteryWarningDialog
 * @param batteryWarningDismissFunction Fires when tapping outside the BatteryWarningDialog
 * @param onTaskNameChange Fires when the user makes changes to text in the TaskTextField
 * @param onTaskNameDone Fires when the user taps "Done" on the keyboard when focused on TaskTextField
 * @param onTaskNameIconClick Fires when the icon in the TaskTextField is pushed. Changes from
 * count up to count down mode.
 * @param onDismissDropdown Fires when the dropdown in the TaskTextField is dismissed by tapping outside it
 * @param onDropdownMenuItemClick Fires when an item in the dropdown is selected. Fills in the taskTextField with selected string
 * @param onTimerAnimationFinish Fires when the count up timer fades in and out when starting recording
 * @param onClockStart Fires when the start button is pressed
 * @param onClockStop Fires when the stop button is pressed
 * @param onHoursValueChanged Fires when the value of the hours text in the EditTimerTextField is changed
 * @param onMinutesValueChanged Fires when the value of the minutes text in the EditTimerTextField is changed
 * @param onSecondsValueChanged Fires when the value of the seconds text in the EditTimerTextField is changed
 * @param onHoursFocusChanged Fires when the focus of the hours text in the EditTimerTextField is changed
 * @param onMinutesFocusChanged Fires when the value of the minutes text in the EditTimerTextField is changed
 * @param onSecondsFocusChanged Fires when the value of the seconds text in the EditTimerTextField is changed
 */
class ClockPageViewModelState(
    clockButtonEnabled: Boolean = false,
    taskTextFieldValue: TextFieldValue = TextFieldValue(""),
    isClockRunning: Boolean = false,
    dropdownExpanded: Boolean = false,
    currSeconds: Int = 0,
    filteredEventNames: List<String> = listOf(),
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
    var batteryWarningConfirmFunction: () -> Unit = {},
    var batteryWarningDismissFunction: () -> Unit = {},
    var onTaskNameChange: (TextFieldValue) -> Unit = { _ -> },
    var onTaskNameDone: () -> Unit = {},
    var onTaskNameIconClick: () -> Unit = {},
    var onDismissDropdown: () -> Unit = {},
    var onDropdownMenuItemClick: (String) -> Unit = { _ -> },
    var onTimerAnimationFinish: () -> Unit = {},
    var onClockStart: () -> Unit = {},
    var onClockStop: (Boolean) -> Unit = { _ ->},
    var onHoursValueChanged: (TextFieldValue) -> Unit = { _ -> },
    var onMinutesValueChanged: (TextFieldValue) -> Unit = { _ -> },
    var onSecondsValueChanged: (TextFieldValue) -> Unit = { _ -> },
    var onHoursFocusChanged: (FocusState) -> Unit = { _ -> },
    var onMinutesFocusChanged: (FocusState) -> Unit = { _ -> },
    var onSecondsFocusChanged: (FocusState) -> Unit = { _ -> },

    countDownEndTime: Long = 0L,
    currCountDownSeconds: Int = 0,
) {
    var clockButtonEnabled by mutableStateOf(clockButtonEnabled)
    var taskTextFieldValue by mutableStateOf(taskTextFieldValue)
    var isClockRunning by mutableStateOf(isClockRunning)
    var dropdownExpanded by mutableStateOf(dropdownExpanded)
    var currSeconds by mutableStateOf(currSeconds)
    var filteredEventNames by mutableStateOf(filteredEventNames)
    var batteryWarningDialogVisible by mutableStateOf(batteryWarningDialogVisible)
    var countDownTimerEnabled by mutableStateOf(countDownTimerEnabled)
    var hoursTextFieldValue by mutableStateOf(hoursTextFieldValue)
    var minutesTextFieldValue by mutableStateOf(minutesTextFieldValue)
    var secondsTextFieldValue by mutableStateOf(secondsTextFieldValue)

    // TODO not in ClockPage... should it be here?
    var countDownEndTime by mutableStateOf(countDownEndTime)
    // TODO not in ClockPage... should it be here?
    var currCountDownSeconds by mutableStateOf(currCountDownSeconds)
}
