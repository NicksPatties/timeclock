package com.nickspatties.timeclock.ui.pages

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.input.ImeAction
import androidx.test.platform.app.InstrumentationRegistry
import com.nickspatties.timeclock.R
import com.nickspatties.timeclock.ui.theme.TimeClockTheme
import com.nickspatties.timeclock.ui.viewmodel.ClockPageViewModelState
import org.junit.Rule
import org.junit.Test

class ClockPageTest {

    /**
     * Util function to get the specific ImeAction of a node. Used to verify the correct ImeAction
     * is being used.
     */
    private fun SemanticsNodeInteraction.getImeAction(): ImeAction {
        val errorOnFail = "Failed to perform IME action."
        val node = fetchSemanticsNode(errorOnFail)
        assert(hasSetTextAction()) { errorOnFail }
        return node.config.getOrElse(SemanticsProperties.ImeAction) {
            ImeAction.Default
        }
    }

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
    fun taskNameIcon_countDownTimerAppears() {
        composeTestRule.setContent {
            TimeClockTheme {
                ClockPage(
                    viewModelState = ClockPageViewModelState()
                )
            }
        }
        composeTestRule.onNodeWithTag("TaskTextField_IconButton").performClick()
        composeTestRule.onNodeWithTag("TimerTextField_Hours")
            .assertIsDisplayed()
            .assertIsEnabled()
        composeTestRule.onNodeWithTag("TimerTextField_Minutes")
            .assertIsDisplayed()
            .assertIsEnabled()
        composeTestRule.onNodeWithTag("TimerTextField_Seconds")
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun taskNameIcon_countDownTimerDisappearsIfAlreadyEnabled() {
        composeTestRule.setContent {
            TimeClockTheme {
                ClockPage(
                    viewModelState = ClockPageViewModelState(
                        countDownTimerEnabled = true
                    )
                )
            }
        }
        composeTestRule.onNodeWithTag("TaskTextField_IconButton").performClick()
        composeTestRule.onNodeWithTag("TimerTextField_Hours").assertDoesNotExist()
        composeTestRule.onNodeWithTag("TimerTextField_Minutes").assertDoesNotExist()
        composeTestRule.onNodeWithTag("TimerTextField_Seconds").assertDoesNotExist()
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

    @Test
    fun countUp_imeDoneActionHidesDropdownMenuAndRemovesFocus() {
        composeTestRule.setContent {
            TimeClockTheme {
                ClockPage(
                    viewModelState = ClockPageViewModelState(
                        autofillTaskNames = setOf(
                            "programming"
                        )
                    )
                )
            }
        }
        composeTestRule.onNodeWithTag("TaskTextField").performTextInput("p")
        val imeAction = composeTestRule.onNodeWithTag("TaskTextField").getImeAction()
        assert(imeAction == ImeAction.Done)
        composeTestRule.onNodeWithTag("TaskTextField").performImeAction()
        composeTestRule.onNodeWithTag("DropdownMenuItem_programming").assertDoesNotExist()
        composeTestRule.onNodeWithTag("TaskTextField").assertIsNotFocused()
    }

    @Test
    fun countDown_imeActionHidesDropdownMenuAndFocusTimer() {
        composeTestRule.setContent {
            TimeClockTheme {
                ClockPage(
                    viewModelState = ClockPageViewModelState(
                        countDownTimerEnabled = true,
                        autofillTaskNames = setOf(
                            "programming"
                        )
                    )
                )
            }
        }
        composeTestRule.onNodeWithTag("TaskTextField").performTextInput("p")
        val imeAction = composeTestRule.onNodeWithTag("TaskTextField").getImeAction()
        assert(imeAction == ImeAction.Next)
        composeTestRule.onNodeWithTag("TaskTextField").performImeAction()
        composeTestRule.onNodeWithTag("DropdownMenuItem_programming").assertDoesNotExist()
        composeTestRule.onNodeWithTag("TimerTextField_Hours").assertIsFocused()
    }
}
