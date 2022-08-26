package com.nickspatties.timeclock.ui.pages

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.platform.app.InstrumentationRegistry
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.ui.theme.TimeClockTheme
import com.nickspatties.timeclock.ui.viewmodel.AnalysisPageViewModelState
import com.nickspatties.timeclock.ui.viewmodel.AnalysisPane
import com.nickspatties.timeclock.util.MILLIS_PER_HOUR
import com.nickspatties.timeclock.util.MILLIS_PER_SECOND
import com.nickspatties.timeclock.util.decorateMillisWithDecimalHours
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AnalysisPageTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @get:Rule
    val composeTestRule = createComposeRule()

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
    lateinit var fakePanes: List<AnalysisPane>

    @Before
    fun initViewModelState() {
        fakePanes = listOf(
            AnalysisPane(
                events = mockEvents,
                rangeName = "pane one"
            ),
            AnalysisPane(
                events = mockEvents,
                rangeName = "pane two"
            )
        )
    }


    @Test
    fun timeRangeSelector_leavingAnAnalysisPaneResetsSelectedRowAndHoursCount() {
        val fakeState = AnalysisPageViewModelState(
            analysisPanes = fakePanes
        )
        composeTestRule.setContent {
            TimeClockTheme {
                AnalysisPage(
                    viewModelState = fakeState
                )
            }
        }

        composeTestRule.onNodeWithTag("TimeClockListItem_task one").performClick()
        composeTestRule.onNodeWithTag("TimeRangeSelector_End").performClick()
        composeTestRule.onNodeWithTag("TimeRangeSelector_Start").performClick()
        composeTestRule.onNodeWithTag("PieChart_CenterText_HoursValue").assertTextEquals(
            decorateMillisWithDecimalHours(mockEventsTotalMillis)
        )
        // assert the first page doesn't have a selected row
        assert(fakeState.analysisPanes[0].selectedAnalysisRowId < 0)
    }
}