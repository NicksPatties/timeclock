package com.nickspatties.timeclock.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.nickspatties.timeclock.ui.pages.ClockPage
import com.nickspatties.timeclock.ui.theme.TimeClockTheme
import com.nickspatties.timeclock.ui.viewmodel.ClockPageViewModelState
import org.junit.Rule
import org.junit.Test

class ClockPageTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun clockPage_defaultConfiguration() {
        composeTestRule.setContent {
            TimeClockTheme {
                val fakeViewModelState = ClockPageViewModelState()
                ClockPage(viewModelState = fakeViewModelState)
            }
        }
        composeTestRule.onNodeWithText("Start").assertExists()
    }
}