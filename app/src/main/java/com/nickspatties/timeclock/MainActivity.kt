package com.nickspatties.timeclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nickspatties.timeclock.data.TimeClockEventDatabase
import com.nickspatties.timeclock.ui.Screen
import com.nickspatties.timeclock.ui.TimeClockViewModel
import com.nickspatties.timeclock.ui.TimeClockViewModelFactory
import com.nickspatties.timeclock.ui.pages.AnalysisPage
import com.nickspatties.timeclock.ui.pages.ClockPage
import com.nickspatties.timeclock.ui.pages.ListPage
import com.nickspatties.timeclock.ui.theme.TimeClockTheme
import com.nickspatties.timeclock.util.decorateMillisWithDecimalHours

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

@Composable
fun TimeClockApp(viewModel: TimeClockViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    TimeClockTheme {
        Scaffold(
            bottomBar = {
                BottomNavigation {
                    val screens = listOf(Screen.Clock, Screen.List, Screen.Metrics)
                    for (i in screens) {
                        val route = stringResource(i.routeResourceId)
                        val label = stringResource(i.labelResourceId)
                        val icon = i.iconResourceId
                        BottomNavigationItem(
                            selected = currentDestination?.hierarchy?.any { it.route == route } == true,
                            onClick = {
                                navController.navigate(route) {
                                    launchSingleTop = true
                                }
                             },
                            label = { Text(label) },
                            icon = {
                                Icon(
                                    painter = painterResource(id = icon),
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            },
            content = {
                NavigationComponent(
                    modifier = Modifier.padding(it),
                    viewModel = viewModel,
                    navController = navController,
                    startDestination = stringResource(R.string.route_clock)
                )
            }
        )
    }
}

@Composable
fun NavigationComponent(
    modifier: Modifier = Modifier,
    viewModel: TimeClockViewModel,
    navController: NavHostController,
    startDestination: String
) {
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
    val onTimerAnimationFinished = viewModel::resetCurrSeconds

    val groupedEvents = viewModel.groupedEventsByDate.observeAsState().value
    val editingEventId = viewModel.editingEventId
    val onListItemClick =  viewModel::changeEditId
    val onDeleteButtonClick = viewModel::deleteEvent
    val onCancelButtonClick =  viewModel::changeEditId

    val currentSelectionString = viewModel.currentDateRangeString()
    val selectionStartButtonVisible = false
    val selectionEndButtonVisible = false
    val analysisPageRows = viewModel.groupedEventsByNameAndMillis.observeAsState().value
    val openId = viewModel.selectedAnalysisRowId
    val changeId = viewModel::changeSelectedAnalysisRowId
    val onSelectionStartButtonClick = viewModel::onDateRangeStartButtonClick
    val onSelectionEndButtonClick = viewModel::onDateRangeEndButtonClick
    val totalSelectedHours = decorateMillisWithDecimalHours(viewModel.selectedMillis)

    val clockRoute = stringResource(id = R.string.route_clock)
    val listRoute = stringResource(id = R.string.route_list)
    val metricsRoute = stringResource(id = R.string.route_metrics)

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(clockRoute) {
            ClockPage(
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
                stopClock = stopClock,
                timerAnimationFinishedListener = onTimerAnimationFinished
            )
        }
        composable(listRoute) {
            ListPage(
                groupedEvents = groupedEvents,
                editingEventId = editingEventId,
                onListItemClick = onListItemClick,
                onDeleteButtonClick = onDeleteButtonClick,
                onCancelButtonClick = onCancelButtonClick
            )
        }
        composable(metricsRoute) {
            AnalysisPage(
                currentSelectionString = currentSelectionString,
                selectionStartButtonVisible = selectionStartButtonVisible,
                selectionEndButtonVisible = selectionEndButtonVisible,
                onSelectionStartButtonClick = onSelectionStartButtonClick,
                onSelectionEndButtonClick = onSelectionEndButtonClick,
                analysisPageRows = analysisPageRows,
                totalSelectedHours = totalSelectedHours,
                openId = openId,
                changeRowId = changeId
            )
        }
    }
}
