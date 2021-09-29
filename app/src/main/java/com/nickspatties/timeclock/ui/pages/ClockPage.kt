package com.nickspatties.timeclock.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true)
@Composable
fun ClockPage() {
    Scaffold() {
        Column(
            modifier = Modifier.fillMaxSize(1f),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // text input
            TextField(
                value = "",
                onValueChange = {},
                label = {
                    Text("I am going to...")
                },
                placeholder = {
                    Text("what are you doing?")
                }
            )

            // timer clock
            Text(
                text = "00:00:00",
                style = MaterialTheme.typography.h2
            )

            // button for starting time
            Button(onClick = { /*TODO*/ }) {
                Text(
                    text = "Push me"
                )
            }
        }
    }
}