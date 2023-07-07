package com.example.todoapp.datasource.repository

import com.example.todoapp.datasource.network.api.RetrofitInstance
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

class ToDoRepositoryImpl @Inject constructor(
     private val todoDb: ToDoDatabase,
     private val sharedPreferencesHelper: SharedPreferencesHelper) : ToDoRepository {

    private var currentList:MutableList<ToDoItem> = emptyList<ToDoItem>().toMutableList()

    var needToSynchronize=false

    //COLLECTING REVISION
    override fun updateRevision(r:Int){
        sharedPreferencesHelper.putRevision(r)
    }
    override fun getRevision():Int=sharedPreferencesHelper.getRevision()


    //WORKING WITH DATABASE
    override fun getListDb(): Flow<List<ToDoItem>> = todoDb.dao().getListFlow()

    override fun getTaskDb(id: UUID): ToDoItem =todoDb.dao().getTask(id)

    override suspend fun updateListDb(list: List<ToDoItem>) = todoDb.dao().updateList(list)

    override suspend fun deleteTaskByIdDb(id: UUID) {
        todoDb.dao().deleteTaskById(id)
    }

    override suspend fun addTaskDb(item: ToDoItem){
        todoDb.dao().addTask(item)
    }

    override suspend fun updateTaskDb(item: ToDoItem){
        todoDb.dao().updateTask(item)
    }

    override suspend fun restoreItem(item: ToDoItem, position:Int){
        loadList()
        val list = currentList
        list.add(position, item)
        updateListApi(list)
    }

    override fun getPositionById(itemId: UUID):Int{
        for(i in currentList.indices){
            if(currentList[i].id==itemId)
                return i
        }
        return -1
    }


    //WORKING WITH NETWORK
    override suspend fun loadList(): Resource<TaskListResponse> {

        try {

            val response = RetrofitInstance.api.getList()

            if (response.isSuccessful) {

                val resultResponse = response.body()

                if (resultResponse != null) {

                    val currentNetworkList=resultResponse.list.reversed()
                    val currentDatabaseList=todoDb.dao().getList()
                    val mergedList=HashMap<UUID, ToDoItem>()

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

    override suspend fun deleteTaskByIdApi(id: UUID): Resource<TaskResponse> {

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

    override suspend fun addTaskApi(item: ToDoItem): Resource<TaskResponse> {

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

    override suspend fun updateTaskApi(id: UUID, item: ToDoItem): Resource<TaskResponse> {

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