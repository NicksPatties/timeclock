package com.nickspatties.timeclock.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSizeIn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.nickspatties.timeclock.ui.TimeClockViewModel
import com.nickspatties.timeclock.util.getTimerString

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ClockPage(viewModel: TimeClockViewModel) {

    val clockEnabled = viewModel.clockButtonEnabled
    val isRunning = viewModel.isClockRunning
    val dropdownExpanded = viewModel.dropdownExpanded
    val taskTextFieldValue = viewModel.taskTextFieldValue
    val autofillTaskNames = viewModel.autofillTaskNames.observeAsState()

    val onTaskNameChange = viewModel::onTaskNameChange
    val onTaskNameDonePressed = viewModel::onTaskNameDonePressed
    val onDismissDropdown = viewModel::onDismissDropdown
    val onDropdownMenuItemClick = viewModel::onDropdownMenuItemClick
    val startClock = viewModel::startClock
    val stopClock = viewModel::stopClock

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
            TimerText(currSeconds = viewModel.currSeconds)

            // button for starting time
            Button(
                enabled = clockEnabled,
                onClick = {
                    if (isRunning) {
                        stopClock()
                    } else {
                        startClock()
                    }
                    //setIsRunning(!isRunning) // update isRunning in the viewModel instead
                }
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