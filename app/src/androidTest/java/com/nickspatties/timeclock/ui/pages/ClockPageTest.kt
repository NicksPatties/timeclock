package com.nickspatties.timeclock.ui.pages

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.nickspatties.timeclock.R
import com.nickspatties.timeclock.ui.theme.TimeClockTheme
import com.nickspatties.timeclock.ui.viewmodel.ClockPageViewModelState
import org.junit.Rule
import org.junit.Test

class ClockPageTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun countUp_defaultConfiguration() {
        composeTestRule.setContent {
            TimeClockTheme {
                val fakeViewModelState = ClockPageViewModelState()
                ClockPage(viewModelState = fakeViewModelState)
            }
        }
        composeTestRule.onNodeWithTag("TaskTextField").assertIsEnabled()
        composeTestRule.onNodeWithTag("StartTimerButton")
            .assertIsNotEnabled()
            .assertTextEquals(context.getString(R.string.start))
    }

    @Test
    fun countUp_StartTimerButtonEnabledWhenTextIsInTaskTextField() {
        val testString = context.getString(R.string.start)
        composeTestRule.setContent {
            TimeClockTheme {
                val fakeViewModelState = ClockPageViewModelState()
                ClockPage(viewModelState = fakeViewModelState)
            }
        }
        composeTestRule.onNodeWithTag("TaskTextField").assertIsEnabled()
        composeTestRule.onNodeWithTag("TaskTextField").performTextInput("programming")
        composeTestRule.onNodeWithTag("StartTimerButton")
            .assertIsEnabled()
            .assertTextEquals(testString)
    }

    @Test
    fun taskNameDropdown_dropdownAppearsAndTaskFillsInWhenLabelIsClicked() {
        composeTestRule.setContent {
            TimeClockTheme {
                ClockPage(viewModelState =
                    ClockPageViewModelState(
                        autofillTaskNames = setOf(
                            "programming",
                            "reading"
                        )
                    )
                )
            }
        }
        composeTestRule.onNodeWithTag("TaskTextField").performTextInput("pro")
        composeTestRule.onNodeWithTag("DropdownMenuItem_programming").performClick()
        composeTestRule.onNodeWithTag("TaskTextField", useUnmergedTree = true)
            .assertTextEquals("programming")
    }

    @Test
    fun countDown_defaultConfiguration() {
        composeTestRule.setContent {
            TimeClockTheme {
                ClockPage(viewModelState = ClockPageViewModelState(
                    countDownTimerEnabled = true
                ))
            }
        }
        composeTestRule.onNodeWithTag("TaskTextField").assertIsEnabled()
        composeTestRule.onNodeWithTag("TimerTextField_Hours").assertIsEnabled()
        composeTestRule.onNodeWithTag("TimerTextField_Minutes").assertIsEnabled()
        composeTestRule.onNodeWithTag("TimerTextField_Seconds").assertIsEnabled()
        composeTestRule.onNodeWithTag("StartTimerButton")
            .assertIsNotEnabled()
            .assertTextEquals(context.getString(R.string.start))
    }

    @Test
    fun countDown_StartTimerButtonStillDisabledIfTaskNameIsNotEmptyAndTimeIsZero() {
        composeTestRule.setContent {
            TimeClockTheme {
                ClockPage(viewModelState = ClockPageViewModelState(
                    countDownTimerEnabled = true
                ))
            }
        }
        composeTestRule.onNodeWithTag("TaskTextField").performTextInput("programming")
        composeTestRule.onNodeWithTag("StartTimerButton").assertIsNotEnabled()
    }

    @Test
    fun countDown_StartTimerButtonStillDisabledIfTaskNameIsEmptyTimeIsNotZero() {
        composeTestRule.setContent {
            TimeClockTheme {
                ClockPage(viewModelState = ClockPageViewModelState(
                    countDownTimerEnabled = true
                ))
            }
        }
        composeTestRule.onNodeWithTag("TimerTextField_Minutes").performTextInput("1")
        composeTestRule.onNodeWithTag("StartTimerButton").assertIsNotEnabled()
    }

    @Test
    fun countDown_StartTimerButtonEnabledIfTaskNameIsNotEmptyAndTimeIsNonZero() {
        composeTestRule.setContent {
            TimeClockTheme {
                ClockPage(viewModelState = ClockPageViewModelState(
                    countDownTimerEnabled = true
                ))
            }
        }
        composeTestRule.onNodeWithTag("TaskTextField").performTextInput("programming")
        composeTestRule.onNodeWithTag("TimerTextField_Minutes").performTextInput("1")
        composeTestRule.onNodeWithTag("StartTimerButton").assertIsEnabled()
    }
}