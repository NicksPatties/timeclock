package com.nickspatties.timeclock.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.nickspatties.timeclock.ui.components.StartTimerButton
import com.nickspatties.timeclock.ui.components.TimerText
import com.nickspatties.timeclock.util.MockAutofillValues

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ClockPage(
    clockEnabled: Boolean,
    isRunning: Boolean,
    dropdownExpanded: Boolean,
    taskTextFieldValue: TextFieldValue,
    autofillTaskNames: Set<String>?,
    currSeconds: Int,
    onTaskNameChange: (TextFieldValue) -> Unit,
    onTaskNameDonePressed: () -> Unit,
    onDismissDropdown: () -> Unit,
    onDropdownMenuItemClick: (String) -> Unit,
    startClock: () -> Unit,
    stopClock: () -> Unit,
    timerAnimationFinishedListener: () -> Unit = {}
) {

    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold() {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column {
                // Task name input
                // todo character limit 120
                TaskTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = taskTextFieldValue,
                    enabled = !isRunning,
                    onTaskNameChange = {
                        onTaskNameChange(it)
                    },
                    onDone = {
                        onTaskNameDonePressed()
                    },
                    keyboardController = keyboardController
                )

                DropdownMenu(
                    modifier = Modifier
                        // 144 = 3 * 48 (the default height of a DropdownMenuItem
                        .requiredSizeIn(maxHeight = 144.dp)
                        .fillMaxWidth(),
                    expanded = dropdownExpanded,
                    properties = PopupProperties(focusable = false),
                    onDismissRequest = {
                        onDismissDropdown()
                    },
                ) {
                    val filteredTaskNames = autofillTaskNames!!.filter {
                        it.contains(taskTextFieldValue.text)
                    }

                    if (filteredTaskNames.isNotEmpty()) {
                        filteredTaskNames.forEach { label ->
                            DropdownMenuItem(onClick = {
                                onDropdownMenuItemClick(label)
                                keyboardController?.hide() // close keyboard
                            }) {
                                Text(text = label)
                            }
                        }
                    }
                }
            }

            // timer clock
            TimerText(
                isRunning = isRunning,
                currSeconds = currSeconds,
                finishedListener = timerAnimationFinishedListener
            )

            StartTimerButton(
                clockEnabled = clockEnabled,
                isRunning = isRunning,
                startClock = startClock,
                stopClock = stopClock
            )
        }
    }
}

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

@Composable
@Preview
fun ClockPageMockUp() {
    ClockPage(
        clockEnabled = true,
        isRunning = false,
        dropdownExpanded = false,
        taskTextFieldValue = TextFieldValue(),
        autofillTaskNames = MockAutofillValues,
        currSeconds = 0,
        onTaskNameChange = { },
        onTaskNameDonePressed = { },
        onDismissDropdown = { },
        onDropdownMenuItemClick = { },
        startClock = { },
        stopClock = { }
    )
}