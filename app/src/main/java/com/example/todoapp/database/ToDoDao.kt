package com.example.todoapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.R
import com.example.todoapp.model.ToDoItem
import com.example.todoapp.utils.BASE_URL
import com.example.todoapp.utils.DATABASE_NAME
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface ToDoDao {
    @Query("SELECT * FROM $DATABASE_NAME")
    fun getList():Flow<List<ToDoItem>>

    @Query("SELECT * FROM $DATABASE_NAME WHERE id = :id")
    suspend fun getTask(id:UUID): ToDoItem

    @Insert
    suspend fun addTask(toDoItem: ToDoItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateList(list : List<ToDoItem>)

    @Update
    suspend fun updateTask(item: ToDoItem)

    @Query("DELETE FROM $DATABASE_NAME WHERE id = :id")
    suspend fun deleteTaskById(id: UUID)

    @Delete
    suspend fun deleteList(list: List<ToDoItem>)

}