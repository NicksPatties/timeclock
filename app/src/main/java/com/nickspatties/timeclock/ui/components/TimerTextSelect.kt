package com.nickspatties.timeclock.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimerTextSelect() {
    var textValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = "00",
                selection = TextRange(1)
            )
        )
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    // another cheeky way to skip onValueChange when focus happens the first time
    var skipOnValueChange = false

    BasicTextField(
        modifier = Modifier
            .width(70.dp)
            .onFocusChanged {
                if (it.isFocused) {
                    skipOnValueChange = true
                    // select all when focused
                    textValue = TextFieldValue(
                        text = textValue.text,
                        selection = TextRange(start = 0, end = textValue.text.length)
                    )
                } else {
                    skipOnValueChange = true
                    val newText = formatDigitsAfterLeavingFocus(textValue.text)
                    textValue = TextFieldValue(
                        text = newText,
                        selection = TextRange(0, 0)
                    )
                }
            },
        value = textValue,
        onValueChange = {
            if (skipOnValueChange) {
                skipOnValueChange = false
            } else {
                textValue = onTimerTextSelectValueChange(it)
            }
        },
        textStyle = MaterialTheme.typography.h2,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.NumberPassword
        ),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
            textValue = TextFieldValue(
                text = textValue.text,
                selection = TextRange(0)
            )
            focusManager.clearFocus()
        }),
        singleLine = true
    )
}

fun onTimerTextSelectValueChange(value: TextFieldValue): TextFieldValue {
    val selectAllValue = TextFieldValue(
        text = value.text,
        selection = TextRange(0, value.text.length)
    )
    val cursorAtEnd = TextFieldValue(
        text = value.text,
        selection = TextRange(value.text.length)
    )
    return when(value.text.length) {
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

fun formatDigitsAfterLeavingFocus(digits: String): String {
    if (digits.isEmpty()) return "00"
    if (digits.length > 1) return digits
    return "0$digits"
}

@Preview
@Composable
fun TimerTextSelectPreview() {
    Box(
        modifier = Modifier.padding(50.dp)
    ) {
        TimerTextSelect()
    }
}