package com.example.todoapp.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.todoapp.api.RetrofitInstance
import com.example.todoapp.api.request.TaskListRequest
import com.example.todoapp.api.request.TaskRequest
import com.example.todoapp.api.response.TaskListResponse
import com.example.todoapp.api.response.TaskResponse
import com.example.todoapp.database.ToDoDatabase
import com.example.todoapp.model.ToDoItem
import com.example.todoapp.utils.API_PREFERENCES
import com.example.todoapp.utils.Resource
import com.example.todoapp.utils.TypeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.toList
import retrofit2.Response
import java.util.UUID

typealias TaskListener = (items:List<ToDoItem>) -> Unit

class ToDoRepository(val context: Context, val todoDb:ToDoDatabase, val sharedPreferences: SharedPreferences){

    private var currentList:MutableList<ToDoItem> = emptyList<ToDoItem>().toMutableList()

    var needToSynchronize=false

    private val editor = sharedPreferences.edit()
    private val key="REVISION"
    var doneNum= 0
    var taskNum= 0

    private fun updateRevision(r:Int){
        editor.putInt(key, r)
        editor.apply()
    }
    private fun getRevision():Int{
        return sharedPreferences.getInt(key, 0)
    }

    fun loadList(): Flow<Resource<List<ToDoItem>>> = flow{

        //emit(Resource.Loading())

        try {

            val response = RetrofitInstance.api.getList()

            if (response.isSuccessful) {

                val resultResponse = response.body()

                if (resultResponse != null) {

                    updateRevision(resultResponse.revision)
                    todoDb.dao().updateList(resultResponse.list)
                    currentList=resultResponse.list.reversed().toMutableList()
                    taskNum=currentList.size
                    doneNum=currentList.count { it.done }

                    emit(Resource.Success(resultResponse.list.reversed()))

                } else {

                    emit(Resource.Error("Empty response body"))

                }

            } else {

                emit(Resource.Error("Request failed with ${response.code()}: ${response.message()}"))

            }

        } catch (e: Exception) {

            emit(Resource.Error("An error occurred: ${e.localizedMessage ?: "Unknown error"}"))

        }

    }.flowOn(Dispatchers.IO)

    suspend fun updateListApi(list: List<ToDoItem>):Resource<TaskListResponse>{

        return try {

            val response = RetrofitInstance.api.updateList(getRevision(), TaskListRequest("ok", list))

            if (response.isSuccessful) {

                val resultResponse = response.body()
                if (resultResponse != null) {

                    updateRevision(resultResponse.revision)
                    Resource.Success(resultResponse)

                } else {

                    Resource.Error("Empty response body")

                }

            } else {

                Resource.Error("Request failed with ${response.code()}: ${response.message()}")

            }

        } catch (e: Exception) {

            Resource.Error("An error occurred: ${e.localizedMessage ?: "Unknown error"}")

        }
    }

    suspend fun deleteTaskByIdApi(id: UUID): Resource<TaskResponse> {

        return try {

            val item=getTaskDb(id)
            val response = RetrofitInstance.api.deleteTask(getRevision(), id)

            if (response.isSuccessful) {

                val resultResponse = response.body()

                if (resultResponse != null) {

                    if(item.done) doneNum--
                    taskNum--
                    updateRevision(resultResponse.revision)
                    Resource.Success(resultResponse)

                } else {

                    Resource.Error("Empty response body")

                }

            } else {

                Resource.Error("Request failed with ${response.code()}: ${response.message()}")

            }

        } catch (e: Exception) {

            Resource.Error("An error occurred: ${e.localizedMessage ?: "Unknown error"}")

        }

    }

    suspend fun addTaskApi(item: ToDoItem): Resource<TaskResponse> {

        return try {

            val response = RetrofitInstance.api.addTask(getRevision(), TaskRequest("ok", item))

            if (response.isSuccessful) {

                val resultResponse = response.body()

                if (resultResponse != null) {

                    if(item.done) doneNum++
                    taskNum++

                    updateRevision(resultResponse.revision)
                    Resource.Success(resultResponse)

                } else {

                    Resource.Error("Empty response body")

                }

            } else {

                Resource.Error("Request failed with ${response.code()}: ${response.message()}")

            }
        } catch (e: Exception) {

            Resource.Error("An error occurred: ${e.localizedMessage ?: "Unknown error"}")

        }

    }

    suspend fun updateTaskApi(id: UUID, item: ToDoItem): Resource<TaskResponse> {

        return try {

            val response = RetrofitInstance.api.updateTask(getRevision(), id, TaskRequest("ok", item))

            if (response.isSuccessful) {

                val resultResponse = response.body()

                if (resultResponse != null) {

                    if(getTaskDb(id).done!=item.done){
                        if(item.done) doneNum++
                        else doneNum--
                    }
                    updateRevision(resultResponse.revision)
                    Resource.Success(resultResponse)

                } else {

                    Resource.Error("Empty response body")

                }

            } else {

                Resource.Error("Request failed with ${response.code()}: ${response.message()}")

            }

        } catch (e: Exception) {

            Resource.Error("An error occurred: ${e.localizedMessage ?: "Unknown error"}")

        }

    }

    // Взаимодействия с локальной БД

    suspend fun updateListDb(list: List<ToDoItem>) = todoDb.dao().updateList(list)

    fun getListDb(): Flow<List<ToDoItem>> = todoDb.dao().getList()

    suspend fun deleteTaskByIdDb(id: UUID) = todoDb.dao().deleteTaskById(id)

    suspend fun addTaskDb(item: ToDoItem) = todoDb.dao().addTask(item)

    suspend fun updateTaskDb(item: ToDoItem) = todoDb.dao().updateTask(item)

    suspend fun getTaskDb(id:UUID) = todoDb.dao().getTask(id)

    suspend fun restoreItem(item:ToDoItem, position:Int){
        loadList()
        val list = currentList
        list.add(position, item)
        updateListApi(list)
    }

    fun getPositionById(itemId:UUID):Int{
        for(i in currentList.indices){
            if(currentList[i].id==itemId)
                return i
        }
        return -1
    }

    //    fun listenCurrentList():Flow<List<ToDoItem>> = callbackFlow{
//        val listener:TaskListener = {
//            trySend(it)
//        }
//        listeners.add(listener)
//
//        awaitClose {
//            listeners.remove(listener)
//        }
//    }.buffer(Channel.CONFLATED)


//    fun getListApi(): Flow<Resource<TaskListResponse>> = flow{
//        emit(safeApiCall { RetrofitInstance.api.getList() })
//    }.flowOn(Dispatchers.IO)

}