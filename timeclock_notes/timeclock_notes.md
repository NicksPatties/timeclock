# Design stuff
## Purpose
This app allows you to keep track of different activities that you do during the day. Over time, as use continues, you can analyze how much time you spend during the days, weeks, and months doing different things.

## User Stories
* Bob wants to have better time management skills. When he starts doing a particular activity, he uses the app to start recording the time spent doing an activity. When done, he returns to the app and stops recording. Over a period of time doing this for multiple activities, he returns to the app to analyze how he spends his time during the day. Knowing this information, he can make adjustments accordingly to make better use of his time.
* Anne would like to improve her estimates for completing certain tasks. In Timeclock, she indicates the task that she'd like to take care of. Once completed, she stops recording time for her activity. Taking a look at the amount of time spent doing similar tasks, she now has a better idea of the amount of time it should take to accomplish those tasks. This gives her confidence to share these estimates with teammates.
* Rob never knows where his time goes during the day, and never feels like he has enough time in the day to do anything. Using Timeclock, he keeps track of what tasks he's been doing and how long they take. After taking a look at the analysis, he realizes that he spends most of his time playing video games instead of his responsibilities! Using this information, he vows to make changes to his habits and reduce his time gaming.
* Jeane wants to learn a new skill. To do so, she vows to spend about half an hour a day improving her skill. Some days are busier than others, so she can't get as much time some days than others. As long as she spends X hours during the week, then she'll be happy. She takes a look at the analysis in her app, and notices that she's not spending as much time during the week than she would've liked. Armed with this information, she can make adjustments to her schedule to increase the amount of time working on her skill.

## Screens
Timeclock is separated into three different screens. Users can move from one screen to the other by swiping left and right to view the next screen.

### Clock screen
The primary screen of the app. This screen allows users to define the task they are currently working on, and use the clock. The main components are:
* [MVP] Task TextField
  * [MVP] A text field that the user can use to type in the task they are working on. Required to start running the clock, since it would be useless to record a time block without knowing what it is.
  * [Upgrade] The app remembers what tasks have been done previously, and displays a dropdown of task names for ease of entry.
* [MVP] Clock display
  * [MVP] Displays the amount of time that is currently elapsed in this current session
  * Can be optionally displayed, in case viewing the clock serves as a distraction
  * [Upgrade] Can optionally be configured to act as a timer, allowing the user to set the hours, minutes, and seconds that the timer should count down before automatically ending recording of completing a task
* [MVP] Start/Stop Button
  * [MVP] Pressing the button starts the clock. Pressing it again while starting stops the timer and saves the work time that has been completed to the data store
  * [MVP] It is disabled when there is no task in the Task TextField

### List screen
Lists the sessions that the user has recorded for ease of viewing. Also allows the user to edit a given span of time in case an error has taken place.
* ListEntry
  * Displays the task name, and the amount of time that was spent working on that task
  * When clicked, allows the user to edit the entry from within that field. The editable fields are as follows:
    * Task name (This should use a component similar to the TaskTextField)
    * Start date
    * Start time (start date and time should be coerced to the end time of the preivous task)
    * End date
    * End time (should be coerced to the current time)

Some thoughts about how the information should be displayed, since there are mutliple ways to do it
* Simple List format
  * Easiest to see the tasks that were completed in a list. Most recent (and most likely to edit since it's the most recent one)
* Calendar day view format
  * Think of the day view in a calendar app, where each task takes up a certain amount of space and the user scrolls down to see more time

### Analysis screen
Shows an analysis of the time that has been spent in modular cards that do different things. The list of cards is scrollable, and a card can contain data visualization tools such as bar graphs, pie charts, human readable time breakdowns, and so on. Some sample cards include:
* [MVP] Pie chart
* Daily Bar graph

### Settings
Each screen in the app can have configurable settings that are relevant to its screen alone. They can control things such as whether elements are visible, how lists are displayed, and so on.

Actually, it may be easier to have a settings page that is sorted into app wide options, and then options for each individual page listed below in its own section.

## Handling time zones
Since this app has to display times to the user, a discussion of handling time zones cannot be ignored. Below are a couple proposals to properly handle time zones:

1. Display all time related things in the time zone at a user's current location (I think this option is the best)
2. Allow user to set a timezone of their choice
3. Pick one time zone and never change it, regardless of phone's location

## Premium Features
* Premium features
  * Before thinking about that, something to consider, what is the minimum I need to still have a useful product?
    * keeping track of time spent on different activities
    * viewing the amount of time spent on different activities
    * analyzing back to a certain period of time trends of time spent
  * Saving more data, more tasks that can be saved
  * Tagging features, grouping features?

# Technical stuff
## Setup
* Gradle
  * Which version of the JDK should I use with Gradle?
    * Just pick whichever one works
* .gitignore
  * What should be included in the .gitignore file

## Implementation
* Which should I use, Jetpack and Views, or Jetpack Compose?
  * ~~Jetpack and Views~~
    * Pros
      * Clean separation of view related code and activity, fragment related code
        * Actually that's not entirely true with the use of ViewModels and view binding, which are useful tools to update your UI based on data. You still write some lambdas in different view properties
      * XML is easy to understand
    * Cons
      * Very verbose view properties per element, lots of properties per component, especially in constraint views
      * Requires use of Kotlin and XML
  * **Jetpack Compose**
    * Pros
      * According to testimonials, it makes creating views faster and easier!
      * Possible to upgrade to using compose should I start the project with Jetpack and Views
    * Cons
      * Cluttering Activity and Fragment view code with more functions specific to UI
* Managing state
  * State should be located in the ViewModel, since multiple screens will be dependent on the state of the ViewModel (Timer, and List screen)
* Navigation
  * check out the findings from the Navigation course
* Saving data
  * Room db should save data onto the device
