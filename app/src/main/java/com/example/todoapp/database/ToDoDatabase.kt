package com.example.todoapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.todoapp.model.ToDoItem

@Database(entities = [ToDoItem::class], version = 1)
@TypeConverters(UUIDConverter::class)
abstract class ToDoDatabase : RoomDatabase() {
    abstract fun dao(): ToDoDao
}