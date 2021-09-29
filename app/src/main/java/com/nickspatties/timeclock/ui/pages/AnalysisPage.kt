package com.nickspatties.timeclock.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val analysisCards = listOf<@Composable () -> Unit>(
    {
        Text("This is some sample analysis")
    },
    {
        Text("This is some more analysis yeah!")
    }
)

@Preview(showBackground = true)
@Composable
fun AnalysisPage() {
    Scaffold(modifier = Modifier.padding(horizontal = 10.dp)) {

        // List of analysis cards that are shown
        Column {
            analysisCards.forEach { card ->
                AnalysisCard {
                    card()
                }
            }
        }
    }
}

@Composable
fun AnalysisCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        content()
    }
}