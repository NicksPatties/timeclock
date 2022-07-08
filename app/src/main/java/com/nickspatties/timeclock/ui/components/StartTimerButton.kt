package com.nickspatties.timeclock.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StartTimerButton(
    clockEnabled: Boolean,
    isRunning: Boolean,
    startClock: () -> Unit,
    stopClock: (Boolean) -> Unit
) {
    Button(
        modifier = Modifier
            .width(150.dp)
            .height(100.dp),
        shape = RoundedCornerShape(50.dp),
        enabled = clockEnabled,
        onClick = { if (isRunning) stopClock(true) else startClock() }
    ) {
        Text(
            text = if (isRunning) "Stop" else "Start",
            fontSize = 24.sp
        )
    }
}

@Preview
@Composable
fun NotEnabledNotRunning() {
    StartTimerButton(
        clockEnabled = false,
        isRunning = false,
        startClock = {},
        stopClock = {}
    )
}

@Preview
@Composable
fun EnabledNotRunning() {
    StartTimerButton(
        clockEnabled = true,
        isRunning = false,
        startClock = {},
        stopClock = {}
    )
}

@Preview
@Composable
fun EnabledAndRunning() {
    StartTimerButton(
        clockEnabled = true,
        isRunning = true,
        startClock = {},
        stopClock = {}
    )
}
