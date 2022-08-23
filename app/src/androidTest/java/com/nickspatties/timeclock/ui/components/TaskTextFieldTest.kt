package com.nickspatties.timeclock.ui.components

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import org.junit.Rule
import org.junit.Test

class TaskTextFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun onImeAction_callsWhenImeActionIsDone() {
        var counter = 0
        composeTestRule.setContent {
            TaskTextField(
                value = TextFieldValue(""),
                enabled = true,
                onTaskNameChange = {},
                onImeAction = { counter++ }
            )
        }
        composeTestRule.onNodeWithTag("TaskTextField").assertIsEnabled()
        composeTestRule.onNodeWithTag("TaskTextField").performTextInput("task")
        composeTestRule.onNodeWithTag("TaskTextField").performImeAction()
        assert(counter == 1)
    }

    @Test
    fun onImeAction_callsWhenImeActionIsNext() {
        var counter = 0
        composeTestRule.setContent {
            TaskTextField(
                value = TextFieldValue(""),
                enabled = true,
                onTaskNameChange = {},
                imeAction = ImeAction.Next,
                onImeAction = { counter++ }
            )
        }
        composeTestRule.onNodeWithTag("TaskTextField").assertIsEnabled()
        composeTestRule.onNodeWithTag("TaskTextField").performTextInput("task")
        composeTestRule.onNodeWithTag("TaskTextField").performImeAction()
        assert(counter == 1)
    }
}