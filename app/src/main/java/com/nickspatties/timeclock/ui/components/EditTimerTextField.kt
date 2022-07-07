package com.nickspatties.timeclock.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.nickspatties.timeclock.ui.viewmodel.ClockPageViewModel

const val TAG = "EditTimerTextField"

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditTimerTextField(
    // TODO: just pass TextFieldValues into this component. No need to pass the current seconds down
    viewModel: ClockPageViewModel,
    hoursTextFieldValue: TextFieldValue = TextFieldValue(),
    minutesTextFieldValue: TextFieldValue = TextFieldValue(),
    secondsTextFieldValue: TextFieldValue = TextFieldValue(),
    clickable: Boolean = true
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Row {
        TimerTextField(
            modifier = Modifier.onFocusChanged {
                viewModel.onHoursFocusChanged(it)
            },
            textValue = hoursTextFieldValue,
            enabled = clickable,
            keyboardController = keyboardController,
            imeAction = ImeAction.Next,
            focusManager = focusManager,
            onValueChange = { viewModel.onHourValueChange(it) }
        )
        Text(
            text = ":",
            style = MaterialTheme.typography.h2
        )
        TimerTextField(
            modifier = Modifier.onFocusChanged {
                viewModel.onMinutesFocusChanged(it)
            },
            textValue = minutesTextFieldValue,
            enabled = clickable,
            keyboardController = keyboardController,
            imeAction = ImeAction.Next,
            focusManager = focusManager,
            onValueChange = { viewModel.onMinuteValueChange(it)}
        )
        Text(
            text = ":",
            style = MaterialTheme.typography.h2
        )
        TimerTextField(
            modifier = Modifier.onFocusChanged {
                viewModel.onSecondsFocusChanged(it)
            },
            textValue = secondsTextFieldValue,
            enabled = clickable,
            keyboardController = keyboardController,
            imeAction = ImeAction.Done,
            focusManager = focusManager,
            onValueChange = { viewModel.onSecondValueChange(it) }
        )
    }
}

@Preview
@Composable
fun EditTimerTextFieldPreview() {
    //EditTimerTextField()
}