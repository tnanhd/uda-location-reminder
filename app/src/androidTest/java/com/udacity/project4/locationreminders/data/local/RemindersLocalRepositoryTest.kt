package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var remindersLocalRepository: RemindersLocalRepository
    private lateinit var remindersDatabase: RemindersDatabase

    @Before
    fun initDb() {
        remindersDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        remindersLocalRepository = RemindersLocalRepository(
            remindersDatabase.reminderDao(),
            Dispatchers.Main
        )
    }

    @After
    fun closeDb() = remindersDatabase.close()

    @Test
    fun insertReminder_thenLoadReminders_shouldReturnNonEmptyList() = runTest {
        val id = UUID.randomUUID().toString()
        val title = "title"
        val description = "description"
        val location = "location"
        val latitude = 0.0
        val longitude = 0.0

        val reminder = ReminderDTO(title, description, location, latitude, longitude, id)
        remindersLocalRepository.saveReminder(reminder)

        val reminders = remindersLocalRepository.getReminders()

        assertThat(reminders, notNullValue())
        assertThat(reminders is Result.Success, `is`(true))
        assertThat((reminders as Result.Success).data, `is`(not(emptyList())))
        reminders.data[0].let {
            assertThat(it.id, `is`(id))
            assertThat(it.title, `is`(title))
            assertThat(it.description, `is`(description))
            assertThat(it.location, `is`(location))
            assertThat(it.latitude, `is`(latitude))
            assertThat(it.longitude, `is`(longitude))
        }

        val remindersById = remindersLocalRepository.getReminder(id)
        assertThat(remindersById, notNullValue())
        assertThat(remindersById is Result.Success, `is`(true))
        assertThat((remindersById as Result.Success).data, `is`(instanceOf(ReminderDTO::class.java)))
        remindersById.data.let {
            assertThat(it.id, `is`(id))
            assertThat(it.title, `is`(title))
            assertThat(it.description, `is`(description))
            assertThat(it.location, `is`(location))
            assertThat(it.latitude, `is`(latitude))
            assertThat(it.longitude, `is`(longitude))
        }

        remindersLocalRepository.deleteAllReminders()
        val emptyReminders = remindersLocalRepository.getReminders()
        assertThat(emptyReminders is Result.Success, `is`(true))
        assertThat((emptyReminders as Result.Success).data, `is`(emptyList()))
    }

    @Test
    fun findNonExistingReminder_shouldReturnEmptyList() = runTest {
        val reminder = remindersLocalRepository.getReminder("non-existing-id")
        assertThat(reminder is Result.Error, `is`(true))
        assertThat((reminder as Result.Error).message, `is`("Reminder not found!"))
    }

}