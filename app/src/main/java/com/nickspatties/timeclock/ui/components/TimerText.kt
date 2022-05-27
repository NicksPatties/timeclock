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
            textValue = onTimerTextSelectValueChange(it)
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

fun onTimerTextSelectValueChange(value: TextFieldValue): TextFieldValue {
    val selectAllValue = TextFieldValue(
        text = value.text,
        selection = TextRange(0, 3)
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