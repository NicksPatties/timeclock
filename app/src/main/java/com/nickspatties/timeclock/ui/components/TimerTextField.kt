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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimerTextField(
    modifier: Modifier = Modifier,
    textValue: TextFieldValue = TextFieldValue(),
    keyboardController: SoftwareKeyboardController? = null,
    imeAction: ImeAction = ImeAction.Done,
    focusManager: FocusManager,
    handleFocusChange : () -> Unit = {},
    shouldMoveFocus: Boolean = false,
    onValueChange: (TextFieldValue) -> Unit = {},
    onKeyboardDone: () -> Unit = {}
) {
    // another cheeky way to skip onValueChange when focus happens the first time
    var skipOnValueChange = false

    BasicTextField(
        modifier = modifier
            .width(70.dp)
            .onFocusChanged {
                handleFocusChange()
                skipOnValueChange = true
            },
        value = textValue,
        onValueChange = {
            if (skipOnValueChange) {
                skipOnValueChange = false
            } else {
                onValueChange(it)
            }
        },
        textStyle = MaterialTheme.typography.h2,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.NumberPassword
        ),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
            onKeyboardDone()
            if (shouldMoveFocus)
                focusManager.moveFocus(FocusDirection.Next)
            else
                focusManager.clearFocus()
        }),
        singleLine = true
    )
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
        //TimerTextField()
    }
}