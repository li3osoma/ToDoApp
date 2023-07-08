package com.example.todoapp.datasource.repository

import android.util.Log
import androidx.lifecycle.asLiveData
import com.example.todoapp.datasource.network.api.ToDoApi
import com.example.todoapp.datasource.network.connection.Resource
import com.example.todoapp.datasource.network.dto.TaskListRequest
import com.example.todoapp.datasource.network.dto.TaskRequest
import com.example.todoapp.datasource.network.dto.TaskListResponse
import com.example.todoapp.datasource.network.dto.TaskResponse
import com.example.todoapp.datasource.persistence.SharedPreferencesHelper
import com.example.todoapp.datasource.persistence.database.ToDoDatabase
import com.example.todoapp.domain.model.ToDoItem
import com.example.todoapp.domain.repository.ToDoRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

/*

Work with remote and local data sources

 */
class ToDoRepositoryImpl @Inject constructor(
     private val todoDb: ToDoDatabase,
     private val api:ToDoApi,
     private val sharedPreferencesHelper: SharedPreferencesHelper) : ToDoRepository {


    //COLLECTING REVISION
    override fun updateRevision(r: Int) {
        sharedPreferencesHelper.putRevision(r)
    }
    override fun getRevision(): Int = sharedPreferencesHelper.getRevision()


    //WORKING WITH DATABASE
    override fun getListDb(): Flow<List<ToDoItem>> = todoDb.dao().getListFlow()

    override fun getTaskDb(id: UUID): ToDoItem = todoDb.dao().getTask(id)

    override suspend fun updateListDb(list: List<ToDoItem>) = todoDb.dao().updateList(list)

    override suspend fun deleteTaskByIdDb(id: UUID) {
        todoDb.dao().deleteTaskById(id)
    }

    override suspend fun deleteTaskDb(item: ToDoItem) {
        todoDb.dao().deleteTask(item)
        Log.println(Log.INFO, "DELETE DB", item.text)
    }

    override suspend fun addTaskDb(item: ToDoItem) {
        todoDb.dao().addTask(item)
    }

    override suspend fun updateTaskDb(item: ToDoItem) {
        todoDb.dao().updateTask(item)
    }

    override suspend fun restoreTaskDb(item: ToDoItem, position: Int, list: List<ToDoItem>) {
        val currentList=list.toMutableList()
        currentList.add(position, item)
        updateListDb(currentList)
    }

    override suspend fun restoreTask(item: ToDoItem, position: Int, list: List<ToDoItem>) {
        val currentList=list.toMutableList()
        currentList.add(position, item)
        updateListApi(currentList)
    }

    //WORKING WITH NETWORK
    override suspend fun loadList(): Resource<TaskListResponse> {

        try {
            val response = api.getList()

            if (response.isSuccessful) {

                val resultResponse = response.body()

                if (resultResponse != null) {

                    val currentNetworkList = resultResponse.list.reversed()
                    Log.println(Log.INFO, "NETWORK LIST", resultResponse.list.toString())
                    val currentDatabaseList = todoDb.dao().getList()
                    val mergedList = HashMap<UUID, ToDoItem>()

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

    override suspend fun updateListApi(list: List<ToDoItem>): Resource<TaskListResponse> {

        return try {
            val response = api.updateList(getRevision(), TaskListRequest("ok", list))

            if (response.isSuccessful) {

                val resultResponse = response.body()
                if (resultResponse != null) {

                    updateListDb(resultResponse.list)
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

    override suspend fun deleteTaskByIdApi(id: UUID) {
        lateinit var res: Resource<TaskResponse>
        //Log.println(Log.INFO, "DELETE API 1", getTaskDb(id).text.toString())
        try {
            Log.println(Log.INFO, "DELETE API 2", getRevision().toString())
            val response = api.deleteTask(id, getRevision())
            if (response.isSuccessful) {

                Log.println(Log.INFO, "DELETE API 3", getTaskDb(id).text.toString())
                val resultResponse = response.body()

                if (resultResponse != null) {

                    Log.println(Log.INFO, "DELETE API 4", getTaskDb(id).text.toString())
                    updateRevision(resultResponse.revision)
                    res = Resource.Success(resultResponse)

                } else {

                    res = Resource.Error("Empty response body")

                }

            } else {

                res =
                    Resource.Error("Request failed with ${response.code()}: ${response.message()}")

            }

        } catch (e: Exception) {

            res = Resource.Error("An error occurred: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    override suspend fun addTaskApi(item: ToDoItem): Resource<TaskResponse> {

        return try {
            val response = api.addTask(getRevision(), TaskRequest("ok", item))

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

    override suspend fun updateTaskApi(id: UUID, item: ToDoItem){
        try {
            Log.println(Log.INFO, "DELETE API 2", getRevision().toString())
            val response = api.updateTask(id, getRevision(),TaskRequest("ok",item))
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
            Log.println(Log.INFO, "AAAAAAAA", getRevision().toString())
            Resource.Error("An error occurred: ${e.localizedMessage ?: "Unknown error"}")

        }
    }

}