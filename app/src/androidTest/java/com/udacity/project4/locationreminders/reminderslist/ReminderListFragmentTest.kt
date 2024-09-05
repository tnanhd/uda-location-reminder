package com.udacity.project4.locationreminders.reminderslist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.util.TreeIterables
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matcher
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

    @Test
    fun clickAddReminderFAB_shouldNavigateToSaveReminderFragment() {
        val fragmentScenario =
            launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        fragmentScenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        onView(withId(R.id.addReminderFAB)).perform(click())
        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
        fragmentScenario.close()
    }

    @Test
    fun noData_shouldDisplayNoDataOnRemindersListFragment() {
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        waitForView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
    }
}

fun waitForView(viewMatcher: Matcher<View>): ViewInteraction {
    return onView(isRoot()).perform(object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return isRoot()
        }

        override fun getDescription(): String {
            return "Wait until view is visible."
        }

        override fun perform(uiController: UiController?, view: View?) {
            val timeout = System.currentTimeMillis() + 3000  // 3-second timeout
            do {
                for (child in TreeIterables.breadthFirstViewTraversal(view)) {
                    if (viewMatcher.matches(child)) return
                }
                uiController?.loopMainThreadForAtLeast(50)
            } while (System.currentTimeMillis() < timeout)
            throw AssertionError("View is not visible after 3 seconds.")
        }
    })
}