package com.nickspatties.timeclock

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
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
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nickspatties.timeclock.data.TimeClockEventDatabase
import com.nickspatties.timeclock.data.UserPreferencesRepository
import com.nickspatties.timeclock.ui.pages.AnalysisPage
import com.nickspatties.timeclock.ui.pages.ClockPage
import com.nickspatties.timeclock.ui.pages.ListPage
import com.nickspatties.timeclock.ui.theme.TimeClockTheme
import com.nickspatties.timeclock.ui.viewmodel.Screen
import com.nickspatties.timeclock.ui.viewmodel.TimeClockViewModel
import com.nickspatties.timeclock.ui.viewmodel.TimeClockViewModelFactory
import com.nickspatties.timeclock.util.decorateMillisWithDecimalHours
import com.nickspatties.timeclock.util.getNotificationManager

private val Context.dataStore by preferencesDataStore(
    name = "user_preferences"
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create notification channels
        createNotificationChannels()

        // initialize database and viewmodel
        val application = requireNotNull(this).application
        val dataSource = TimeClockEventDatabase.getInstance(application).timeClockEventDao

        // initialize preferences datastore, used to store
        val userPrefsRepo = UserPreferencesRepository(dataStore)

        // initialize ViewModel from ViewModelProvider.Factory
        val viewModelFactory = TimeClockViewModelFactory(
            dataSource = dataSource,
            userPrefsRepo = userPrefsRepo,
            application = application
        )
        val timeClockViewModel = ViewModelProvider(this, viewModelFactory).get(TimeClockViewModel::class.java)

        setContent {
            TimeClockApp(viewModel = timeClockViewModel)
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val clockChannel =  NotificationChannel(
                getString(R.string.clock_channel_id),
                getString(R.string.clock_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
            val alarmChannel = NotificationChannel(
                getString(R.string.alarm_channel_id),
                getString(R.string.alarm_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getNotificationManager(this)
            notificationManager.createNotificationChannels(listOf(clockChannel, alarmChannel))
        }
    }
}

@Composable
fun TimeClockApp(viewModel: TimeClockViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val clockRoute = stringResource(R.string.route_clock)

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
                                    popUpTo(clockRoute)
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
                    startDestination = clockRoute
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
    val clockPageViewModel = viewModel.clockPage
    val clockEnabled = clockPageViewModel.clockButtonEnabled
    val isRunning = clockPageViewModel.isClockRunning
    val dropdownExpanded = clockPageViewModel.dropdownExpanded
    // observe changes on autofillTaskNames to allow filteredTaskNames to function properly
    clockPageViewModel.autofillTaskNames.observeAsState()
    val filteredTaskNames = clockPageViewModel.filteredEventNames
    val taskTextFieldValue = clockPageViewModel.taskTextFieldValue
    val currSeconds = clockPageViewModel.currSeconds
    val onTaskNameChange = clockPageViewModel::onTaskNameChange
    val onTaskNameDonePressed = clockPageViewModel::onTaskNameDonePressed
    val onDismissDropdown = clockPageViewModel::onDismissDropdown
    val onDropdownMenuItemClick = clockPageViewModel::onDropdownMenuItemClick
    val startClock = clockPageViewModel::startClock
    val stopClock = clockPageViewModel::stopClock
    val onTimerAnimationFinished = clockPageViewModel::resetCurrSeconds
    val countdownEnabled = clockPageViewModel.countDownTimerEnabled
    val onCountdownIconClicked = clockPageViewModel::switchCountDownTimer
    val hoursTextFieldValue = clockPageViewModel.hoursTextFieldValue
    val minutesTextFieldValue = clockPageViewModel.minutesTextFieldValue
    val secondsTextFieldValue = clockPageViewModel.secondsTextFieldValue
    val batteryWarningDialogVisible = clockPageViewModel.batteryWarningDialogVisible

    val listPageViewModel = viewModel.listPage
    val groupedEvents = listPageViewModel.groupedEventsByDate.observeAsState().value
    val editingEventId = listPageViewModel.editingEventId
    val onListItemClick =  listPageViewModel::changeEditId
    val onDeleteButtonClick = listPageViewModel::deleteEvent
    val onCancelButtonClick =  listPageViewModel::changeEditId

    val analysisPageViewModel = viewModel.analysisPage
    val currentSelectionString = analysisPageViewModel.currAnalysisPane.rangeName
    val selectionStartButtonVisible = analysisPageViewModel.isDateRangeStartButtonVisible()
    val selectionEndButtonVisible = analysisPageViewModel.isDateRangeEndButtonVisible()
    val analysisPageRows = analysisPageViewModel.currAnalysisPane.rowData.observeAsState().value
    val openId = analysisPageViewModel.currAnalysisPane.selectedAnalysisRowId
    val changeId = analysisPageViewModel.currAnalysisPane::changeSelectedAnalysisRowId
    val onSelectionStartButtonClick = analysisPageViewModel::onDateRangeStartButtonClick
    val onSelectionEndButtonClick = analysisPageViewModel::onDateRangeEndButtonClick
    val totalSelectedHours =
        decorateMillisWithDecimalHours(analysisPageViewModel.currAnalysisPane.selectedMillis)

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
                viewModel = clockPageViewModel
            )
        }
        composable(listRoute) {
            ListPage(
                groupedRows = groupedEvents,
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


