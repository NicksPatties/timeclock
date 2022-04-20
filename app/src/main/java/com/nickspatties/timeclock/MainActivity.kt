package com.nickspatties.timeclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.ViewModelProvider
import com.nickspatties.timeclock.data.TimeClockEventDatabase
import com.nickspatties.timeclock.ui.TimeClockViewModel
import com.nickspatties.timeclock.ui.TimeClockViewModel.Companion.clockPath
import com.nickspatties.timeclock.ui.TimeClockViewModel.Companion.listPath
import com.nickspatties.timeclock.ui.TimeClockViewModel.Companion.metricsPath
import com.nickspatties.timeclock.ui.TimeClockViewModelFactory
import com.nickspatties.timeclock.ui.components.BottomBar
import com.nickspatties.timeclock.ui.pages.AnalysisPage
import com.nickspatties.timeclock.ui.pages.ClockPage
import com.nickspatties.timeclock.ui.pages.ListPage
import com.nickspatties.timeclock.ui.theme.TimeClockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize database and viewmodel
        val application = requireNotNull(this).application
        val dataSource = TimeClockEventDatabase.getInstance(application).timeClockEventDao

        // initialize ViewModel from ViewModelProvider.Factory
        val viewModelFactory = TimeClockViewModelFactory(dataSource, application)
        val timeClockViewModel = ViewModelProvider(this, viewModelFactory).get(TimeClockViewModel::class.java)

        setContent {
            TimeClockApp(viewModel = timeClockViewModel)
        }
    }
}

/**
 * initialPage - index of the initial page to test. 0 for clock page,
 * 1 for ListPage, 2 for AnalysisPage
 */
@Composable
fun TimeClockApp(viewModel: TimeClockViewModel) {
    TimeClockTheme {
        Scaffold(
            bottomBar = {
                BottomBar(
                    currPage = viewModel.currPage.value,
                    onBottomNavButtonPressed = viewModel::onBottomNavBarButtonPressed
                )
            },
            content = {
                PageSelector(viewModel.currPage.value, viewModel)
            }
        )
    }
}

@Composable
fun PageSelector(pageId: String, viewModel: TimeClockViewModel) {
    // inputs for ClockPage
    val clockEnabled = viewModel.clockButtonEnabled
    val isRunning = viewModel.isClockRunning
    val dropdownExpanded = viewModel.dropdownExpanded
    val taskTextFieldValue = viewModel.taskTextFieldValue
    val autofillTaskNames = viewModel.autofillTaskNames.observeAsState().value
    val currSeconds = viewModel.currSeconds

    val onTaskNameChange = viewModel::onTaskNameChange
    val onTaskNameDonePressed = viewModel::onTaskNameDonePressed
    val onDismissDropdown = viewModel::onDismissDropdown
    val onDropdownMenuItemClick = viewModel::onDropdownMenuItemClick
    val startClock = viewModel::startClock
    val stopClock = viewModel::stopClock

    // inputs for list page
    val groupedEvents = viewModel.groupedEventsByDate.observeAsState().value
    val editingEventId = viewModel.editingEventId
    val onListItemClick =  viewModel::changeEditId
    val onDeleteButtonClick = viewModel::deleteEvent
    val onCancelButtonClick =  viewModel::changeEditId

    when(pageId) {
        clockPath -> ClockPage(
            clockEnabled = clockEnabled,
            isRunning = isRunning,
            dropdownExpanded = dropdownExpanded,
            taskTextFieldValue = taskTextFieldValue,
            autofillTaskNames = autofillTaskNames,
            currSeconds = currSeconds,
            onTaskNameChange = onTaskNameChange,
            onTaskNameDonePressed = onTaskNameDonePressed,
            onDismissDropdown = onDismissDropdown,
            onDropdownMenuItemClick = onDropdownMenuItemClick,
            startClock = startClock,
            stopClock = stopClock
        )
        listPath -> ListPage(
            groupedEvents = groupedEvents,
            editingEventId = editingEventId,
            onListItemClick = onListItemClick,
            onDeleteButtonClick = onDeleteButtonClick,
            onCancelButtonClick = onCancelButtonClick
        )
        metricsPath -> AnalysisPage(viewModel.timeClockEvents)
    }
}
