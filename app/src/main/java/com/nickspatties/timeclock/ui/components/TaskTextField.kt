package com.nickspatties.timeclock.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.nickspatties.timeclock.R

@Composable
fun TaskTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    enabled: Boolean,
    onTaskNameChange: (TextFieldValue) -> Unit,
    onImeAction: () -> Unit = {},
    countdownTimerEnabled: Boolean = false,
    onIconClick: () -> Unit = {},
    imeAction: ImeAction = ImeAction.Done
) {
    TextField(
        modifier = modifier.testTag("TaskTextField"),
        value = value,
        enabled = enabled,
        onValueChange = onTaskNameChange,
        singleLine = true,
        label = {
            Text(stringResource(R.string.task_text_field_label))
        },
        placeholder = {
            Text(stringResource(R.string.task_text_field_placeholder))
        },
        trailingIcon = {
            IconButton(
                modifier = Modifier.testTag("TaskTextField_IconButton"),
                onClick = onIconClick
            ) {
                val tint = if (countdownTimerEnabled)
                    MaterialTheme.colors.primary
                else
                    MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_hourglass_bottom_24),
                    contentDescription = null,
                    tint = tint
                )
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onDone = { onImeAction() },
            onNext = { onImeAction() }
        )
    )
}

@Preview(showBackground = true)
@Composable
fun TaskTextFieldPreviewEnabled() {
    TaskTextField(
        value = TextFieldValue("programming"),
        enabled = true,
        onTaskNameChange = {}
    )
}

@Preview(showBackground = true)
@Composable
fun TaskTextFieldPreviewDisabled() {
    TaskTextField(
        value = TextFieldValue("programming"),
        enabled = false,
        onTaskNameChange = {}
    )
}

@Preview(showBackground = true)
@Composable
fun TaskTextFieldEnabledEmptyTextField() {
    TaskTextField(
        value = TextFieldValue(""),
        enabled = true,
        onTaskNameChange = {}
    )
}
