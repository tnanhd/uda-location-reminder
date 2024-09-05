package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test
import java.util.UUID

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertReminder_thenLoadReminders_shouldReturnNonEmptyList() = runTest {
        val id = UUID.randomUUID().toString()
        val title = "title"
        val description = "description"
        val location = "location"
        val latitude = 0.0
        val longitude = 0.0

        val reminder = ReminderDTO(title, description, location, latitude, longitude, id)
        database.reminderDao().saveReminder(reminder)

        val reminders = database.reminderDao().getReminders()
        assertThat(reminders, notNullValue())
        assertThat(reminders.size, `is`(1))
        assertThat(reminders[0].id, `is`(id))
        assertThat(reminders[0].title, `is`(title))
        assertThat(reminders[0].description, `is`(description))
        assertThat(reminders[0].location, `is`(location))
        assertThat(reminders[0].latitude, `is`(latitude))
        assertThat(reminders[0].longitude, `is`(longitude))

        val remindersById = database.reminderDao().getReminderById(id)
        assertThat(remindersById, notNullValue())
        assertThat(remindersById?.id, `is`(id))
        assertThat(remindersById?.title, `is`(title))
        assertThat(remindersById?.description, `is`(description))
        assertThat(remindersById?.location, `is`(location))
        assertThat(remindersById?.latitude, `is`(latitude))
        assertThat(remindersById?.longitude, `is`(longitude))

        database.reminderDao().deleteAllReminders()
        assertThat(database.reminderDao().getReminders(), `is`(emptyList()))
    }
}