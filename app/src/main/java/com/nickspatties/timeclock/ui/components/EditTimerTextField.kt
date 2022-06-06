package com.nickspatties.timeclock.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditTimerTextField() {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var hoursTextValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = "00",
                selection = TextRange(1)
            )
        )
    }
    var minutesTextValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = "00",
                selection = TextRange(1)
            )
        )
    }

    var secondsTextValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = "00",
                selection = TextRange(1)
            )
        )
    }
    Row {
        TimerTextField(
            modifier = Modifier.onFocusChanged {
                hoursTextValue = onFocusChanged(it, hoursTextValue)
            },
            textValue = hoursTextValue,
            keyboardController = keyboardController,
            imeAction = ImeAction.Next,
            focusManager = focusManager,
            onValueChange = { hoursTextValue = onHourValueChange(it) },
            onKeyboardDone = {
                hoursTextValue = textFieldValueNoSelection(hoursTextValue)
            }
        )
        Text(
            text = ":",
            style = MaterialTheme.typography.h2
        )
        TimerTextField(
            modifier = Modifier.onFocusChanged {
                minutesTextValue = onFocusChanged(it, minutesTextValue)
            },
            textValue = minutesTextValue,
            keyboardController = keyboardController,
            imeAction = ImeAction.Next,
            focusManager = focusManager,
            onValueChange = {
                minutesTextValue = onMinuteAndSecondValueChange(it)
            },
            onKeyboardDone = {
                minutesTextValue = textFieldValueNoSelection(minutesTextValue)
            }
        )
        Text(
            text = ":",
            style = MaterialTheme.typography.h2
        )
        TimerTextField(
            modifier = Modifier.onFocusChanged {
                secondsTextValue = onFocusChanged(it, secondsTextValue)
            },
            textValue = secondsTextValue,
            keyboardController = keyboardController,
            imeAction = ImeAction.Done,
            focusManager = focusManager,
            onValueChange = {
                secondsTextValue = onMinuteAndSecondValueChange(it)
            },
            onKeyboardDone = {
                secondsTextValue = textFieldValueNoSelection(secondsTextValue)
            }
        )
    }
}

fun onMinuteAndSecondValueChange(value: TextFieldValue): TextFieldValue {
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

fun onHourValueChange(value: TextFieldValue): TextFieldValue {
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
        1 -> cursorAtEnd
        2 -> selectAllValue
        else -> TextFieldValue()
    }
}

fun onFocusChanged(it: FocusState, textValue: TextFieldValue): TextFieldValue {
    return if (it.isFocused) {
        // select all when focused
        TextFieldValue(
            text = textValue.text,
            selection = TextRange(start = 0, end = textValue.text.length)
        )
    } else {
        val newText = formatDigitsAfterLeavingFocus(textValue.text)
        TextFieldValue(
            text = newText,
            selection = TextRange(0, 0)
        )
    }
}

fun textFieldValueNoSelection(textField: TextFieldValue): TextFieldValue {
    return TextFieldValue(
        text = textField.text,
        selection = TextRange(0)
    )
}

@Preview
@Composable
fun EditTimerTextFieldPreview() {
    EditTimerTextField()
}