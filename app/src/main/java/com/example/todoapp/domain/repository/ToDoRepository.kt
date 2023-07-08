package com.example.todoapp.domain.repository

import com.example.todoapp.datasource.network.dto.TaskListResponse
import com.example.todoapp.datasource.network.dto.TaskResponse
import com.example.todoapp.domain.model.ToDoItem
import com.example.todoapp.datasource.network.connection.Resource
import kotlinx.coroutines.flow.Flow
import java.util.UUID

typealias TaskListener = (items:List<ToDoItem>) -> Unit

interface ToDoRepository{


    //COLLECTING REVISION
    fun updateRevision(r:Int)
    fun getRevision():Int


    //WORKING WITH DATABASE
    fun getListDb(): Flow<List<ToDoItem>>

    fun getTaskDb(id: UUID): ToDoItem

    suspend fun updateListDb(list: List<ToDoItem>)

    suspend fun deleteTaskByIdDb(id: UUID)

    suspend fun deleteTaskDb(item: ToDoItem)

    suspend fun addTaskDb(item: ToDoItem)

    suspend fun updateTaskDb(item: ToDoItem)

    suspend fun restoreTaskDb(item: ToDoItem, position:Int, list: List<ToDoItem>)

    suspend fun restoreTask(item: ToDoItem, position:Int, list: List<ToDoItem>)



    //WORKING WITH NETWORK
    suspend fun loadList(): Resource<TaskListResponse>

    suspend fun updateListApi(list: List<ToDoItem>): Resource<TaskListResponse>

    suspend fun deleteTaskByIdApi(id: UUID)

    suspend fun addTaskApi(item: ToDoItem): Resource<TaskResponse>

    suspend fun updateTaskApi(id: UUID, item: ToDoItem)

}