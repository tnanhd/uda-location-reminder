package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainDispatcherRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: SaveReminderViewModel
    private lateinit var application: Application

    @Before
    fun setup() {
        application = ApplicationProvider.getApplicationContext()
        val fakeDataSource: ReminderDataSource = FakeDataSource()

        viewModel = SaveReminderViewModel(
            app = application,
            dataSource = fakeDataSource
        )
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun validateAndSaveReminder_withEmptyTitle_shouldReturnError() {
        // When
        viewModel.validateAndSaveReminder(
            createReminder(emptyField = "title")
        )
        // Then
        assertThat(viewModel.showSnackBarInt.getOrAwaitValue()).isEqualTo(R.string.err_enter_title)
    }

    @Test
    fun validateAndSaveReminder_withEmptyLocation_shouldReturnError() {
        // When
        viewModel.validateAndSaveReminder(
            createReminder(emptyField = "location")
        )
        // Then
        assertThat(viewModel.showSnackBarInt.getOrAwaitValue()).isEqualTo(R.string.err_select_location)
    }

    @Test
    fun validateAndSaveReminder_withValidData_shouldReturnSuccess() {
        // When
        viewModel.validateAndSaveReminder(
            createReminder()
        )
        // Then
        assertThat(viewModel.showToast.getOrAwaitValue()).isEqualTo(application.getString(R.string.reminder_saved))
    }

    @Test
    fun validateAndSaveReminder_withValidData_shouldShowLoading() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel.validateAndSaveReminder(
            createReminder()
        )
        assertThat(viewModel.showLoading.getOrAwaitValue()).isTrue()
        advanceUntilIdle()
        assertThat(viewModel.showLoading.getOrAwaitValue()).isFalse()
    }


    private fun createReminder(emptyField: String = "") = ReminderDataItem(
        "title",
        "description",
        "location",
        0.0,
        0.0
    ).apply {
        when (emptyField) {
            "title" -> {
                title = ""
            }

            "location" -> {
                location = ""
            }

            else -> {}
        }
    }
}