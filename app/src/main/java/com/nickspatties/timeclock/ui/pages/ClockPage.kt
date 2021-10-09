package com.nickspatties.timeclock.ui.pages

import android.util.Log
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
import androidx.compose.ui.tooling.preview.Preview
import com.nickspatties.timeclock.util.Chronometer

val TAG = "ClockPage"

@Composable
fun ClockPage(chronometer: Chronometer) {

    // initial state variables
    val (clockEnabled, setClockEnabled) = remember { mutableStateOf(false) }
    val (isRunning, setIsRunning) = remember { mutableStateOf(false) }
    var (taskName, setTaskName) = remember { mutableStateOf("") }
    val (startTime, setStartTime) = remember { mutableStateOf(0L) }
    val (endTime, setEndTime) = remember { mutableStateOf(0L) }
    val (currSeconds, setCurrSeconds) = remember { mutableStateOf(0) }

    chronometer.setOnChronometerTickListener {
        setCurrSeconds(currSeconds+1)
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
                taskName = taskName,
                onTaskNameChange = {
                    setTaskName(it)
                    setClockEnabled(it.isNotBlank())
                }
            )

            // timer clock
            TimerText(currSeconds = currSeconds)

            // button for starting time
            Button(
                enabled = clockEnabled,
                onClick = {
                    if (isRunning) {
                        setEndTime(System.currentTimeMillis())
                        chronometer.stop()
                        Log.i(
                            TAG,
                            "startTime: $startTime endTime: $endTime"
                        )
                        setIsRunning(false)
                    } else {
                        setStartTime(System.currentTimeMillis())
                        chronometer.start()
                        setIsRunning(true)
                    }
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

fun getTimerString(currSeconds: Int) : String {
    return currSeconds.toString()
}