package com.nickspatties.timeclock.data

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TaskTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    enabled: Boolean,
    onTaskNameChange: (TextFieldValue) -> Unit,
    onDone: () -> Unit = {},
    keyboardController: SoftwareKeyboardController?
) {

    TextField(
        modifier = modifier,
        value = value,
        enabled = enabled,
        onValueChange = onTaskNameChange,
        singleLine = true,
        label = {
            Text("I am going to...")
        },
        placeholder = {
            Text("what are you doing?")
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            onDone()
            keyboardController?.hide()
        })
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview(showBackground = true)
@Composable
fun TaskTextFieldPreviewEnabled() {
    TaskTextField(
        value = TextFieldValue("programming"),
        enabled = true,
        onTaskNameChange = {},
        keyboardController = null
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview(showBackground = true)
@Composable
fun TaskTextFieldPreviewDisabled() {
    TaskTextField(
        value = TextFieldValue("programming"),
        enabled = false,
        onTaskNameChange = {},
        keyboardController = null
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview(showBackground = true)
@Composable
fun TaskTextFieldEnabledEmptyTextField() {
    TaskTextField(
        value = TextFieldValue(""),
        enabled = true,
        onTaskNameChange = {},
        keyboardController = null
    )
}
