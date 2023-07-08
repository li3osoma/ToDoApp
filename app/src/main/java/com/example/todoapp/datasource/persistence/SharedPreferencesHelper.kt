package com.example.todoapp.datasource.persistence

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.util.Log
import com.example.todoapp.utils.API_PREFERENCES
import com.example.todoapp.utils.KEY
import javax.inject.Inject

class SharedPreferencesHelper @Inject constructor(context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences(API_PREFERENCES, Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    fun putRevision(revision:Int){
        editor.putInt(KEY, revision)
        editor.apply()
        Log.println(Log.INFO, "REVISION", revision.toString())
    }

    fun getRevision():Int=sharedPreferences.getInt(KEY,0)
}