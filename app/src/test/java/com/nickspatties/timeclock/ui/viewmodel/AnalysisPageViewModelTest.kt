package com.nickspatties.timeclock.ui.viewmodel

import com.google.common.truth.Truth.assertThat
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.util.MILLIS_PER_HOUR
import com.nickspatties.timeclock.util.MILLIS_PER_SECOND
import org.junit.Test

class AnalysisPaneTest {
    @Test
    fun resetSelectedRowAndMillis_changeSelectedRowIdToMinusOneAndMillisToTotalMillis() {
        val expectedTotalMillis = 2 * MILLIS_PER_HOUR
        val events = listOf(
            TimeClockEvent(
                startTime = 0L,
                endTime = MILLIS_PER_HOUR,
                name = "task one"
            ),
            TimeClockEvent(
                startTime = MILLIS_PER_HOUR + MILLIS_PER_SECOND,
                endTime = MILLIS_PER_HOUR + MILLIS_PER_HOUR + MILLIS_PER_SECOND,
                name = "task two"
            )
        )
        val mockPane = AnalysisPane(
            events = events,
            rangeName = "Range name"
        )
        mockPane.resetSelectedRowAndMillis()
        assertThat(mockPane.selectedMillis).isEqualTo(expectedTotalMillis)
        assertThat(mockPane.selectedAnalysisRowId).isEqualTo(-1)
    }
}

class AnalysisPageViewModelStateTest {
    // totalMillis of these events = 2 * MILLIS_PER_HOUR
    private val mockEvents = listOf(
        TimeClockEvent(
            startTime = 0L,
            endTime = MILLIS_PER_HOUR,
            name = "task one"
        ),
        TimeClockEvent(
            startTime = MILLIS_PER_HOUR + MILLIS_PER_SECOND,
            endTime = MILLIS_PER_HOUR + MILLIS_PER_HOUR + MILLIS_PER_SECOND,
            name = "task two"
        )
    )

    private val mockEventsTotalMillis = 2 * MILLIS_PER_HOUR

    @Test
    fun onDateRangeEndButtonClick_resetsPreviousPaneAndMovesToNextPane() {
        val mockPanes = listOf(
            AnalysisPane(
                events = mockEvents,
                rangeName = "pane one"
            ),
            AnalysisPane(
                events = mockEvents,
                rangeName = "pane two"
            )
        )
        val previousPane = mockPanes[0]
        val fakeState = AnalysisPageViewModelState(
            analysisPanes = mockPanes
        )
        fakeState.onDateRangeEndButtonClick()
        assertThat(previousPane.selectedMillis).isEqualTo(mockEventsTotalMillis)
        assertThat(previousPane.selectedAnalysisRowId).isEqualTo(-1)
        assertThat(fakeState.currAnalysisPane.rangeName).isEqualTo("pane two")
    }

    @Test
    fun onDateRangeStartButtonClick_resetsPreviousPaneAndMovesToNextPane() {
        val mockPanes = listOf(
            AnalysisPane(
                events = mockEvents,
                rangeName = "pane one"
            ),
            AnalysisPane(
                events = mockEvents,
                rangeName = "pane two"
            )
        )
        val previousPane = mockPanes[1]
        val fakeState = AnalysisPageViewModelState(
            analysisPanes = mockPanes,
            currAnalysisPaneIndex = 1
        )
        fakeState.onDateRangeStartButtonClick()
        assertThat(previousPane.selectedMillis).isEqualTo(mockEventsTotalMillis)
        assertThat(previousPane.selectedAnalysisRowId).isEqualTo(-1)
        assertThat(fakeState.currAnalysisPane.rangeName).isEqualTo("pane one")
    }
}