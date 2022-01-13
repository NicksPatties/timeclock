package com.nickspatties.timeclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.nickspatties.timeclock.data.TimeClockEventDatabase
import com.nickspatties.timeclock.ui.TimeClockViewModel
import com.nickspatties.timeclock.ui.TimeClockViewModelFactory
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
@OptIn(ExperimentalPagerApi::class)
@Composable
fun TimeClockApp(initialPage: Int = 0, viewModel: TimeClockViewModel) {
    TimeClockTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            val pagerState = rememberPagerState(
                pageCount = 3,
                initialPage = initialPage // temporary, but just for testing
            )

            HorizontalPager(
                state = pagerState
            ) { page ->
                PageSelector(page, viewModel)
            }

            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier.layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(placeable.width, placeable.height) {
                        // place x at half of the screen size minus half the width of
                        // the horizontal pager indicator
                        val xPos = (constraints.maxWidth / 2) - (placeable.width / 2)

                        // place y at the bottom of the screen, minus some padding
                        val yPos = constraints.maxHeight - 100
                        placeable.placeRelative(xPos, yPos)
                    }
                }
            )
        }
    }
}

@Composable
fun PageSelector(pageId: Int, viewModel: TimeClockViewModel) {
    // inputs for ClockPage
    val clockEnabled = viewModel.clockButtonEnabled
    val isRunning = viewModel.isClockRunning
    val dropdownExpanded = viewModel.dropdownExpanded
    val taskTextFieldValue = viewModel.taskTextFieldValue
    val autofillTaskNames = viewModel.autofillTaskNames
    val currSeconds = viewModel.currSeconds

    val onTaskNameChange = viewModel::onTaskNameChange
    val onTaskNameDonePressed = viewModel::onTaskNameDonePressed
    val onDismissDropdown = viewModel::onDismissDropdown
    val onDropdownMenuItemClick = viewModel::onDropdownMenuItemClick
    val startClock = viewModel::startClock
    val stopClock = viewModel::stopClock

    // inputs for list page
    val groupedEvents = viewModel.groupedEventsByDate.observeAsState()
    val editingEventId = viewModel.editingEventId
    val onListItemClick =  viewModel::changeEditId
    val onDeleteButtonClick = viewModel::deleteEvent
    val onCancelButtonClick =  viewModel::changeEditId

    when(pageId) {
        0 -> ClockPage(
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
        1 -> ListPage(
            groupedEvents = groupedEvents.value,
            editingEventId = editingEventId,
            onListItemClick = onListItemClick,
            onDeleteButtonClick = onDeleteButtonClick,
            onCancelButtonClick = onCancelButtonClick
        )
        2 -> AnalysisPage(viewModel.timeClockEvents)
    }
}
