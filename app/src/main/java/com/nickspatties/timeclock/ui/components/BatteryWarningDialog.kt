package com.nickspatties.timeclock.ui.components

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nickspatties.timeclock.R

@Composable
@Preview
fun BatteryWarningDialog(
    confirmFunction: () -> Unit = {},
    dismissFunction: () -> Unit = {}
) {
    AlertDialog(
        modifier = Modifier,
        onDismissRequest = dismissFunction,
        title = { Text(stringResource(id = R.string.battery_warning_dialog_title)) },
        text = { Text(stringResource(id = R.string.battery_warning_dialog_body)) },
        confirmButton = {
            TextButton(onClick = confirmFunction) {
                Text(text = stringResource(id = R.string.battery_warning_dialog_action).uppercase())
            }
        },
        dismissButton = null
    )
}
