package com.example.todoapp.datasource.persistence.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.todoapp.datasource.persistence.dao.ToDoDao
import com.example.todoapp.datasource.persistence.UUIDConverter
import com.example.todoapp.domain.model.ToDoItem

@Database(entities = [ToDoItem::class], version = 1)
@TypeConverters(UUIDConverter::class)
abstract class ToDoDatabase : RoomDatabase() {
    abstract fun dao(): ToDoDao
}