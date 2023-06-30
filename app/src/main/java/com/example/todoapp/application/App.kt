package com.example.todoapp.application

import android.app.Application
import android.content.SharedPreferences
import androidx.room.Room
import com.example.todoapp.database.ToDoDatabase
import com.example.todoapp.repository.ToDoRepository
import com.example.todoapp.utils.API_PREFERENCES
//import com.example.todoapp.repository.ToDoRepository
import com.example.todoapp.utils.DATABASE_NAME
import java.util.concurrent.TimeUnit

class App: Application() {

    lateinit var db: ToDoDatabase
    lateinit var toDoRepository: ToDoRepository
    lateinit var revisionSettings:SharedPreferences
    //lateinit var workManager: WorkManager

    override fun onCreate() {
        super.onCreate()
        db=Room.databaseBuilder(this, ToDoDatabase::class.java, DATABASE_NAME).build()
        revisionSettings=getSharedPreferences(API_PREFERENCES, MODE_PRIVATE)
        toDoRepository = ToDoRepository(this, db, revisionSettings)

    }
}