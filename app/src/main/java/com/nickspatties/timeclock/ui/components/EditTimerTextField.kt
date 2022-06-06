package com.nickspatties.timeclock.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditTimerTextField() {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    Row {
        TimerTextField(
            keyboardController = keyboardController,
            imeAction = ImeAction.Next,
            focusManager = focusManager
        )
        Text(
            text = ":",
            style = MaterialTheme.typography.h2
        )
        TimerTextField(
            keyboardController = keyboardController,
            imeAction = ImeAction.Next,
            focusManager = focusManager
        )
        Text(
            text = ":",
            style = MaterialTheme.typography.h2
        )
        TimerTextField(
            keyboardController = keyboardController,
            imeAction = ImeAction.Done,
            focusManager = focusManager
        )
    }
}

@Preview
@Composable
fun EditTimerTextFieldPreview() {
    EditTimerTextField()
}