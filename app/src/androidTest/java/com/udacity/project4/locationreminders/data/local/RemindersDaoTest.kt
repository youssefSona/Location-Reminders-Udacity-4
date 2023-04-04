package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.junit.*
import org.junit.runner.RunWith
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()


    @Test
    fun getReminders() = runBlockingTest {
        // GIVEN - insert a reminder
        val reminder = ReminderDTO(
            "title",
            "description",
            "location",
            (-360..360).random().toDouble(),
            (-360..360).random().toDouble()
        )

        database.reminderDao().saveReminder(reminder)

        // WHEN - Get reminders from the database
        val reminders = database.reminderDao().getReminders()

        // THEN - There is only 1 reminder in the database
        Assert.assertThat(reminders.size, CoreMatchers.`is`(1))
        Assert.assertThat(reminders[0].id, CoreMatchers.`is`(reminder.id))
        Assert.assertThat(reminders[0].title, CoreMatchers.`is`(reminder.title))
        Assert.assertThat(reminders[0].description, CoreMatchers.`is`(reminder.description))
        Assert.assertThat(reminders[0].location, CoreMatchers.`is`(reminder.location))
        Assert.assertThat(reminders[0].latitude, CoreMatchers.`is`(reminder.latitude))
        Assert.assertThat(reminders[0].longitude, CoreMatchers.`is`(reminder.longitude))
    }


    @Test
    fun insertReminder_GetById() = runBlockingTest {
        // GIVEN - Insert a reminder.
        val reminder = ReminderDTO(
            "title",
            "description",
            "location",
            (-360..360).random().toDouble(),
            (-360..360).random().toDouble()
        )
        database.reminderDao().saveReminder(reminder)

        // WHEN - Get the reminder by id from the database.
        val loaded = database.reminderDao().getReminderById(reminder.id)

        // THEN - The loaded data contains the expected values.
        Assert.assertThat<ReminderDTO>(loaded as ReminderDTO, CoreMatchers.notNullValue())
        Assert.assertThat(loaded.id, CoreMatchers.`is`(reminder.id))
        Assert.assertThat(loaded.title, CoreMatchers.`is`(reminder.title))
        Assert.assertThat(loaded.description, CoreMatchers.`is`(reminder.description))
        Assert.assertThat(loaded.location, CoreMatchers.`is`(reminder.location))
        Assert.assertThat(loaded.latitude, CoreMatchers.`is`(reminder.latitude))
        Assert.assertThat(loaded.longitude, CoreMatchers.`is`(reminder.longitude))
    }

    @Test
    fun getReminderByIdNotFound() = runBlockingTest {
        // GIVEN - a random reminder id
        val reminderId = UUID.randomUUID().toString()
        // WHEN - Get the reminder by id from the database.
        val loaded = database.reminderDao().getReminderById(reminderId)
        // THEN - The loaded data should be  null.
        Assert.assertNull(loaded)
    }


    @Test
    fun deleteReminders() = runBlockingTest {
        // Given - reminders inserted
        val remindersList = listOf<ReminderDTO>(
            ReminderDTO(
                "title",
                "description",
                "location",
                (-360..360).random().toDouble(),
                (-360..360).random().toDouble()
            ),
            ReminderDTO(
                "title",
                "description",
                "location",
                (-360..360).random().toDouble(),
                (-360..360).random().toDouble()
            ),
            ReminderDTO(
                "title",
                "description",
                "location",
                (-360..360).random().toDouble(),
                (-360..360).random().toDouble()
            ),
            ReminderDTO(
                "title",
                "description",
                "location",
                (-360..360).random().toDouble(),
                (-360..360).random().toDouble()
            )
        )

        remindersList.forEach {
            database.reminderDao().saveReminder(it)
        }

        // WHEN - deleting all reminders
        database.reminderDao().deleteAllReminders()

        // THEN - The list is empty
        val reminders = database.reminderDao().getReminders()
        Assert.assertThat(reminders.isEmpty(), CoreMatchers.`is`(true))
    }

}
