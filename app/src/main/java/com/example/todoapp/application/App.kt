package com.example.todoapp.application

import android.app.Application
import android.content.Context
import com.example.todoapp.repository.ToDoItemRepository

class App: Application() {
    val toDoItemRepository=ToDoItemRepository()
}