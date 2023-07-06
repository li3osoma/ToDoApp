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
import com.example.todoapp.utils.KEY
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
    var doneNum = 0
    var taskNum = 0

    //COLLECTING REVISION
    private fun updateRevision(r:Int){
        editor.putInt(KEY, r)
        editor.apply()
    }
    private fun getRevision():Int{
        return sharedPreferences.getInt(KEY, 0)
    }


    //WORKING WITH DATABASE
    fun getListDb(): Flow<List<ToDoItem>> = todoDb.dao().getListFlow()

    fun getTaskDb(id: UUID):ToDoItem=todoDb.dao().getTask(id)

    suspend fun updateListDb(list: List<ToDoItem>) = todoDb.dao().updateList(list)

    suspend fun deleteTaskByIdDb(id: UUID) {
        val item=getTaskDb(id)
        if(item.done) doneNum--
        taskNum--
        todoDb.dao().deleteTaskById(id)
    }

    suspend fun addTaskDb(item: ToDoItem){
        todoDb.dao().addTask(item)
    }

    suspend fun updateTaskDb(item: ToDoItem){
        todoDb.dao().updateTask(item)
    }

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


    //WORKING WITH NETWORK
    suspend fun loadList(): Resource<TaskListResponse>{

        //emit(Resource.Loading())

        try {

            val response = RetrofitInstance.api.getList()

            if (response.isSuccessful) {

                val resultResponse = response.body()

                if (resultResponse != null) {
//                    todoDb.dao().updateList(resultResponse.list)
                    val currentNetworkList=resultResponse.list.reversed()
                    val currentDatabaseList=todoDb.dao().getList()
                    val mergedList=HashMap<UUID, ToDoItem>()

//                    taskNum=currentList.size
//                    doneNum=currentList.count { it.done }

                    for (item in currentDatabaseList) {
                        mergedList[item.id] = item
                    }
                    for (item in currentNetworkList) {
                        if (mergedList.containsKey(item.id)) {
                            val item1 = mergedList[item.id]
                            if (item.changed_at > item1!!.changed_at) {
                                mergedList[item.id] = item
                            } else {
                                mergedList[item.id] = item1
                            }
                        } else if (resultResponse.revision != getRevision()) {
                            mergedList[item.id] = item
                        }
                    }
                    updateRevision(resultResponse.revision)
                    return updateListApi(mergedList.values.toList())

                } else {

                    return Resource.Error("Empty response body")

                }

            } else {

                return Resource.Error("Request failed with ${response.code()}: ${response.message()}")

            }

        } catch (e: Exception) {

            return Resource.Error("An error occurred: ${e.localizedMessage ?: "Unknown error"}")

        }

    }

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

            val response = RetrofitInstance.api.deleteTask(getRevision(), id)

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

    suspend fun addTaskApi(item: ToDoItem): Resource<TaskResponse> {

        return try {

            val response = RetrofitInstance.api.addTask(getRevision(), TaskRequest("ok", item))

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

    suspend fun updateTaskApi(id: UUID, item: ToDoItem): Resource<TaskResponse> {

        return try {

            val response = RetrofitInstance.api.updateTask(getRevision(), id, TaskRequest("ok", item))

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


}