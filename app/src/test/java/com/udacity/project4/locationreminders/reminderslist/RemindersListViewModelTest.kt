package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.locationreminders.MainDispatcherRule
import com.udacity.project4.locationreminders.createReminderDTO
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
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

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: RemindersListViewModel
    private lateinit var application: Application
    private lateinit var fakeDataSource: FakeDataSource

    @Before
    fun setup() {
        application = ApplicationProvider.getApplicationContext()
        fakeDataSource = FakeDataSource()

        viewModel = RemindersListViewModel(
            app = application,
            dataSource = fakeDataSource
        )
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun saveReminder_thenLoadReminders_shouldReturnNonEmptyList() = runTest {
        // When
        fakeDataSource.saveReminder(createReminderDTO())
        viewModel.loadReminders()
        // Then
        assertThat(viewModel.remindersList.getOrAwaitValue()).isNotEmpty()
    }

    @Test
    fun loadReminders_shouldReturnError() {
        fakeDataSource.setReturnError(true)
        viewModel.loadReminders()
        assertThat(viewModel.showSnackBar.getOrAwaitValue()).isEqualTo("Reminder not found!")
    }

    @Test
    fun loadReminders_shouldShowLoading() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel.loadReminders()
        assertThat(viewModel.showLoading.getOrAwaitValue()).isTrue()
        advanceUntilIdle()
        assertThat(viewModel.showLoading.getOrAwaitValue()).isFalse()
    }

}