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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.nickspatties.timeclock.ui.TimeClockViewModel
import com.nickspatties.timeclock.util.getTimerString

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ClockPage(viewModel: TimeClockViewModel) {

    // initial state variables
    val (clockEnabled, setClockEnabled) = remember {
        mutableStateOf(viewModel.taskName.isNotBlank())
    }

    val (isRunning, setIsRunning) = remember {
        mutableStateOf(viewModel.isRunning())
    }

    val (dropdownExpanded, setDropdownExpanded) = remember { mutableStateOf(false) }

    val taskNames = viewModel.taskNames.observeAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    val (taskTextFieldValue, setTaskTextFieldValue) = remember { mutableStateOf(TextFieldValue(text = viewModel.taskName))}

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
                        viewModel.taskName = it.text
                        setTaskTextFieldValue(it)
                        setClockEnabled(viewModel.taskName.isNotBlank())
                        setDropdownExpanded(viewModel.taskName.isNotBlank())
                    },
                    onDone = {
                        setDropdownExpanded(false)
                    },
                    keyboardController = keyboardController
                )
                // dropdown menu
                DropdownMenu(
                    modifier = Modifier
                        .requiredSizeIn(maxHeight = 144.dp), // 144 = 3 * 48 (the default height of a DropdownMenuItem
                    expanded = dropdownExpanded,
                    properties = PopupProperties(focusable = false),
                    onDismissRequest = { setDropdownExpanded(false) },
                ) {
                    val filteredTaskNames = taskNames.value!!.filter {
                        it.contains(viewModel.taskName)
                    }

                    if (filteredTaskNames.isNotEmpty()) {
                        filteredTaskNames.forEach { label ->
                            DropdownMenuItem(onClick = {
                                viewModel.taskName = label

                                // move cursor for text field to end of string
                                setTaskTextFieldValue(
                                    TextFieldValue(
                                        text = label,
                                        selection = TextRange(label.length)
                                    )
                                )

                                setDropdownExpanded(false)

                                // close keyboard
                                keyboardController?.hide()
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
                        viewModel.stopClock()
                    } else {
                        viewModel.startClock()
                    }
                    setIsRunning(!isRunning)
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
            keyboardController?.hide()
            onDone()
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