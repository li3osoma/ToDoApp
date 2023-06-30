package com.example.todoapp.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {
    companion object {

        @SuppressLint("SimpleDateFormat")
        fun dateToLong(date: Date):Long{
            val format=SimpleDateFormat("MMM d, yyyy")
            return format.parse(dateToString(date))!!.time
        }

        @SuppressLint("SimpleDateFormat")
        fun dateTimeToLong(date:Date):Long{
            val format=SimpleDateFormat("MMM d, yyyy HH:mm")
            return format.parse(dateToString(date))!!.time
        }

        fun longToDate(l:Long):Date{
            return Date(l)
        }


        fun dateToString(date: Date): String {
            val formatter = SimpleDateFormat("MMMM d, y", Locale.getDefault());
            return formatter.format(date)
        }

        fun dateTimeToString(date: Date): String {
            val formatter = SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault());
            return formatter.format(date)
        }

        @SuppressLint("SimpleDateFormat")
        fun stringToDateTime(s:String):Date{
            val format=SimpleDateFormat("MMM d, yyyy HH:mm")
            return format.parse(s)!!
        }

        @SuppressLint("SimpleDateFormat")
        fun stringToDate(s:String):Date{
            val format=SimpleDateFormat("MMM d, yyyy")
            return format.parse(s)!!
        }
        fun getCurrentDateString():String{
            return dateToString(Date())
        }

    }
}