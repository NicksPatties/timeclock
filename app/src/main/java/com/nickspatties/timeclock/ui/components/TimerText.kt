package com.nickspatties.timeclock.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nickspatties.timeclock.util.getTimerString

@Composable
fun TimerText(
    modifier: Modifier = Modifier,
    isRunning: Boolean,
    currSeconds: Int,
    finishedListener: () -> Unit = { }
) {
    val alpha: Float by animateFloatAsState(
        targetValue = if (isRunning) 1f else 0f,
        finishedListener = { finishedListener() }
    )
    Text(
        modifier = modifier.graphicsLayer(alpha = alpha),
        text = getTimerString(currSeconds),
        style = MaterialTheme.typography.h2
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimerTextSelect() {
    var textValue by remember { mutableStateOf(TextFieldValue(
        text = "00",
        selection = TextRange(1)
    )) }
    val keyboardController = LocalSoftwareKeyboardController.current

    BasicTextField(
        modifier = Modifier.width(70.dp),
        value = textValue,
        onValueChange = {
            /**
             * select all if
             *   single character is 6 or above
             *   there are two characters
             */
            val oneCharacter = it.text.length == 1
            val twoCharacters = it.text.length >= 2

            if (oneCharacter) {
                textValue = if (it.text.toInt() >= 6) {
                    val newTextFieldValue = TextFieldValue(
                        text = it.text,
                        selection = TextRange(0, 3) // select all
                    )
                    newTextFieldValue
                } else {
                    it
                }
            } else if (twoCharacters) {
                val newTextFieldValue = TextFieldValue(
                    text = it.text,
                    selection = TextRange(0, 3) // select all
                )
                textValue = newTextFieldValue
            } else {
                textValue = it
            }
        },
        textStyle = MaterialTheme.typography.h2,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.NumberPassword
        ),
        keyboardActions = KeyboardActions(onNext = {
            keyboardController?.hide()
        }),
        singleLine = true
    )
}

@Preview
@Composable
fun TimerTextPreview() {
    TimerText(
        isRunning = false,
        currSeconds = 100
    )
}

@Preview
@Composable
fun TimerTextSelectPreview() {
    TimerTextSelect()
}