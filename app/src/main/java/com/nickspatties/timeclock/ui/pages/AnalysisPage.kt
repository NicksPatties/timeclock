package com.nickspatties.timeclock.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.util.MockTimeClockEvents
import com.nickspatties.timeclock.util.decorateMillisWithDecimalHours
import com.nickspatties.timeclock.util.sortTotalDurationByName

@Composable
fun AnalysisPage(eventsData: LiveData<List<TimeClockEvent>>) {
    val events = eventsData.value!!
    val verticalScrollState = rememberScrollState()

    val analysisCards = listOf<@Composable () -> Unit>(
        {
            JustNumbersCard(events)
        },
        {
            JustNumbersCard(events)
        },
        {
            JustNumbersCard(events)
        },
        {
            JustNumbersCard(events)
        },
        {
            JustNumbersCard(events)
        }
    )

    Scaffold(modifier = Modifier.padding(horizontal = 10.dp)) {

        // List of analysis cards that are shown
        Column (
            Modifier
                .padding(bottom = 50.dp)
                .verticalScroll(verticalScrollState)
        ){
            analysisCards.forEach { card ->
                card()
            }
        }
    }
}

@Composable
fun AnalysisCardContainer(name: String, content: @Composable () -> Unit) {
    Card (
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        Column (
            modifier = Modifier.padding(5.dp)
        ) {
            Text(name)
            Box (
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primary),
            )
            content()
        }
    }
}

@Composable
fun JustNumbersCard(events: List<TimeClockEvent>) {
    val transformedEvents = sortTotalDurationByName(events)
    AnalysisCardContainer(name = "Just some numbers") {
        FlowRow (
            mainAxisAlignment = FlowMainAxisAlignment.Start,
            crossAxisAlignment = FlowCrossAxisAlignment.Start,
            crossAxisSpacing = 5.dp
        ) {
            transformedEvents.keys.forEach { key ->
                val duration = transformedEvents[key]!!
                JustNumbersCardEntry(millis = duration, taskName = key)
            }
        }
    }
}

@Composable
fun JustNumbersCardEntry(millis: Long, taskName: String) {
    Column (
        modifier = Modifier
            .width(103.dp), // just a guess for now
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = decorateMillisWithDecimalHours(millis),
            style = MaterialTheme.typography.h4,
            color = MaterialTheme.colors.onBackground
        )
        Text(
            modifier = Modifier,
            text = taskName,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onBackground,
            maxLines = 2
        )
    }
}

@Preview
@Composable
fun JustNumbersCardPreview() {
    val events = MockTimeClockEvents
    JustNumbersCard(events)
}

@Preview
@Composable
fun JustNumbersCardEntryPreview() {
    val event = MockTimeClockEvents[0]
    JustNumbersCardEntry(event.endTime - event.startTime, event.name)
}