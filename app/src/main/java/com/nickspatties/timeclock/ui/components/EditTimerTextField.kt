package com.nickspatties.timeclock.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.nickspatties.timeclock.R

const val TAG = "EditTimerTextField"

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditTimerTextField(
    hoursTextFieldValue: TextFieldValue = TextFieldValue("00"),
    minutesTextFieldValue: TextFieldValue = TextFieldValue("00"),
    secondsTextFieldValue: TextFieldValue = TextFieldValue("00"),
    divider: String = stringResource(id = R.string.timer_divider),
    clickable: Boolean = true,
    onHoursValueChanged: (TextFieldValue) -> Unit = { _ -> },
    onMinutesValueChanged: (TextFieldValue) -> Unit = { _ -> },
    onSecondsValueChanged: (TextFieldValue) -> Unit = { _ -> },
    onHoursFocusChanged: (FocusState) -> Unit = { _ -> },
    onMinutesFocusChanged: (FocusState) -> Unit = { _ -> },
    onSecondsFocusChanged: (FocusState) -> Unit = { _ -> },
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Row {
        TimerTextField(
            modifier = Modifier
                .testTag("TimerTextField_Hours")
                .onFocusChanged {
                    onHoursFocusChanged(it)
                },
            textValue = hoursTextFieldValue,
            enabled = clickable,
            keyboardController = keyboardController,
            imeAction = ImeAction.Next,
            focusManager = focusManager,
            onValueChange = onHoursValueChanged
        )
        Text(
            text = divider,
            style = MaterialTheme.typography.h2
        )
        TimerTextField(
            modifier = Modifier
                .testTag("TimerTextField_Minutes")
                .onFocusChanged {
                    onMinutesFocusChanged(it)
                },
            textValue = minutesTextFieldValue,
            enabled = clickable,
            keyboardController = keyboardController,
            imeAction = ImeAction.Next,
            focusManager = focusManager,
            onValueChange = onMinutesValueChanged
        )
        Text(
            text = divider,
            style = MaterialTheme.typography.h2
        )
        TimerTextField(
            modifier = Modifier
                .testTag("TimerTextField_Seconds")
                .onFocusChanged {
                    onSecondsFocusChanged(it)
                },
            textValue = secondsTextFieldValue,
            enabled = clickable,
            keyboardController = keyboardController,
            imeAction = ImeAction.Done,
            focusManager = focusManager,
            onValueChange = onSecondsValueChanged
        )
    }
}

@Preview
@Composable
fun EditTimerTextFieldPreview() {
    EditTimerTextField()
}