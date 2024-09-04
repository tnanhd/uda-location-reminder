package com.udacity.project4.locationreminders

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

fun createReminderDataItem(emptyField: String = "") = ReminderDataItem(
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

fun createReminderDTO() = ReminderDTO(
    "title",
    "description",
    "location",
    0.0,
    0.0
)