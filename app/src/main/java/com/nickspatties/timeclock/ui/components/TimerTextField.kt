package com.nickspatties.timeclock.ui.components

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimerTextField(
    modifier: Modifier = Modifier,
    textValue: TextFieldValue = TextFieldValue(),
    enabled: Boolean = true,
    keyboardController: SoftwareKeyboardController? = null,
    imeAction: ImeAction = ImeAction.Done,
    focusManager: FocusManager,
    onValueChange: (TextFieldValue) -> Unit = {},
    onImeAction: () -> Unit = {}
) {
    // another cheeky way to skip onValueChange when focus happens the first time
    var skipOnValueChange = false
    BasicTextField(
        modifier = modifier
            .width(70.dp)
            .onFocusChanged {
                skipOnValueChange = true
            },
        value = textValue,
        enabled = enabled,
        onValueChange = {
            if (skipOnValueChange) {
                skipOnValueChange = false
            } else {
                onValueChange(it)
            }
        },
        textStyle = MaterialTheme.typography.h2.merge(
            TextStyle(color = MaterialTheme.colors.onBackground)
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.NumberPassword
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                onImeAction()
                focusManager.clearFocus()
            },
            onNext = {
                onImeAction()
                focusManager.moveFocus(FocusDirection.Next)
            }
        ),
        singleLine = true
    )
}
