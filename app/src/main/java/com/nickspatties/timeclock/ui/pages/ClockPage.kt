package com.nickspatties.timeclock.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import com.nickspatties.timeclock.ui.TimeClockViewModel
import com.nickspatties.timeclock.util.getTimerString

@Composable
fun ClockPage(viewModel: TimeClockViewModel) {

    // initial state variables
    val (clockEnabled, setClockEnabled) = remember {
        mutableStateOf(viewModel.taskName.isNotBlank())
    }

    // TODO: this should be determined by the currentTimeClockEvent of the ViewModel instead of in here
    val (isRunning, setIsRunning) = remember {
        mutableStateOf(viewModel.isRunning())
    }

    Scaffold() {
        Column(
            modifier = Modifier.fillMaxSize(1f),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Task name input
            // todo character limit 120
            TaskTextField(
                taskName = viewModel.taskName,
                onTaskNameChange = {
                    viewModel.taskName = it
                    setClockEnabled(viewModel.taskName.isNotBlank())
                }
            )

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
    taskName: String,
    onTaskNameChange: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    TextField(
        value = taskName,
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