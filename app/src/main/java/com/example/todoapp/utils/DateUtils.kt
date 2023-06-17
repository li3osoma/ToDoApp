package com.example.todoapp.utils

import java.text.SimpleDateFormat
import java.util.*

class DateUtils {
    companion object {
        fun getDateString(date: Date): String {
            val formatter = SimpleDateFormat("MMMM d, y", Locale.getDefault());
            return formatter.format(date)
        }

        fun getDateFromString(dateText: String): Date {
            val formatter = SimpleDateFormat("MMMM d, y", Locale.getDefault())
            return formatter.parse(dateText) as Date
        }

        fun getCurrentDateString():String{
            return getDateString(Date())
        }

    }
}