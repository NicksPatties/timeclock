package com.nickspatties.timeclock.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nickspatties.timeclock.util.getTimerString

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ClockPage(
    clockEnabled: Boolean,
    isRunning: Boolean,
    dropdownExpanded: Boolean,
    taskTextFieldValue: TextFieldValue,
    autofillTaskNames: LiveData<Set<String>>,
    currSeconds: Int,
    onTaskNameChange: (TextFieldValue) -> Unit,
    onTaskNameDonePressed: () -> Unit,
    onDismissDropdown: () -> Unit,
    onDropdownMenuItemClick: (String) -> Unit,
    startClock: () -> Unit,
    stopClock: () -> Unit
) {

    autofillTaskNames.observeAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold() {
        Column(
            modifier = Modifier.fillMaxSize(1f),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column {
                // Task name input
                // todo character limit 120
                TaskTextField(
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
                        .requiredSizeIn(maxHeight = 144.dp), // 144 = 3 * 48 (the default height of a DropdownMenuItem
                    expanded = dropdownExpanded,
                    properties = PopupProperties(focusable = false),
                    onDismissRequest = {
                        onDismissDropdown()
                    },
                ) {
                    val filteredTaskNames = autofillTaskNames.value!!.filter {
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
            TimerText(currSeconds = currSeconds)

            // button for starting time
            Button(
                enabled = clockEnabled,
                onClick = { if (isRunning) stopClock() else startClock() }
            ) {
                Text(
                    text = if (isRunning) "Stop" else "Start"
                )
            }
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
fun TimerText(
    modifier: Modifier = Modifier,
    currSeconds: Int
) {
    // timer clock
    Text(
        modifier = modifier,
        text = getTimerString(currSeconds),
        style = MaterialTheme.typography.h2
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
        autofillTaskNames = MutableLiveData(),
        currSeconds = 0,
        onTaskNameChange = { },
        onTaskNameDonePressed = { },
        onDismissDropdown = { },
        onDropdownMenuItemClick = { },
        startClock = { },
        stopClock = { }
    )
}