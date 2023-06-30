package com.example.todoapp.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.core.graphics.translationMatrix
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.todoapp.api.response.TaskListResponse
import com.example.todoapp.application.App
import com.example.todoapp.model.ToDoItem
import com.example.todoapp.repository.ToDoRepository
import com.example.todoapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.*

class ToDoListViewModel(private val app: App, private val toDoRepository: ToDoRepository):AndroidViewModel(app){
    private val _currentList= MutableStateFlow<Resource<List<ToDoItem>>>(Resource.Loading())
    private val currentList: StateFlow<Resource<List<ToDoItem>>> = _currentList

//    var doneNum=0
//    var taskNum=0
    var _currentTask:ToDoItem = createDefaultTask()

    private val _counterToDo: MutableStateFlow<Int> = MutableStateFlow(0)
    val counterToDo: StateFlow<Int> = _counterToDo
//    private val coroutinesWork = PeriodicWorkRequestBuilder<UpdateWorker>(15, TimeUnit.MINUTES)
//        .setInputData(getData())
//        .build()

    fun getTaskNum()=toDoRepository.taskNum
    fun getCompleteTaskNum()=toDoRepository.doneNum
    private fun createDefaultTask():ToDoItem{
        return ToDoItem(UUID.randomUUID(), "", ToDoItem.Importance.basic,
            Date().time, false, "#000000", Date().time, Date().time)
    }

//    fun updateData(){
//        WorkManager.getInstance().enqueue(coroutinesWork)
//    }

    init {
        loadList()
//        taskNum= currentList.value.data!!.size
//        doneNum= currentList.value.data!!.count { it.done }
    }

//    fun getDoneTaskNum():Int=toDoRepository.doneNum
//    fun getTaskNum()=toDoRepository.taskNum
    //private fun getData() = Data.Builder().putInt("USER_ID", (9999..99999).random()).build()


    suspend fun getTaskById(id: UUID):ToDoItem{
        _currentTask=toDoRepository.getTaskDb(id)
        return _currentTask
    }
    private fun loadList(){
        viewModelScope.launch(Dispatchers.IO){
            //_currentList.emit(Resource.Loading())
            try {
                if (hasInternetConnection()) {

                    val response = toDoRepository.loadList()
                        .onStart { _currentList.emit(Resource.Loading()) }
                        .catch { e -> _currentList.emit(Resource.Error("Error lol")) }
                        .collect{result -> _currentList.emit(Resource.Success(result.data!!))
                            //_counterToDo.emit(Resource.Success(result.data.size).data!!)
                        }

                } else {
                    _currentList.emit(Resource.Error("No Internet connection"))
                }

            } catch (t: Throwable) {

                when (t) {

                    is IOException -> _currentList.emit(Resource.Error("Network Failure"))
                    is HttpException -> _currentList.emit(Resource.Error("Failed: ${t.code()} ${t.message()}"))
                    else -> _currentList.emit(Resource.Error("Conversion Error"))

                }

            }
        }
    }

    fun getList(): Flow<List<ToDoItem>> {

        return toDoRepository.getListDb()

    }

//    fun getTaskById(id: UUID){
//        viewModelScope.launch {
//            _currentTask.emit(toDoRepository.getTaskDb(id))
//        }
//    }
    fun deleteTaskById(id: UUID) = viewModelScope.launch(Dispatchers.IO) {

        try {

            if (hasInternetConnection()) {

                val response = toDoRepository.deleteTaskByIdApi(id)

                if (response is Resource.Success) {

                    deleteTaskByIdDb(id) // Не знаю, нужно или не нужно
                    decrementCounterToDo()

                } else if (response is Resource.Error) {

                    handleDeleteTaskError(response.message)

                }

            } else {

                deleteTaskByIdDb(id)
                toDoRepository.needToSynchronize=true
                handleNoInternetConnectionError()

            }

        } catch (e: Exception) {

            deleteTaskByIdDb(id) //delete task in local db if no internet connection
            handleExceptionError(e.localizedMessage ?: "Unknown error")

        }

    }

    fun updateList(){
//        viewModelScope.launch{
//            getList().collect{
//                _currentList.emit(Resource.Success(it))
//            }
//        }
//        updateListApi(_currentList.value.data!!)
        loadList()
    }

    fun addTask(item: ToDoItem) = viewModelScope.launch(Dispatchers.IO) {

        try {

            if (hasInternetConnection()) {

                val response = toDoRepository.addTaskApi(item)

                if (response is Resource.Success) {

                    addTaskDb(item) // Не знаю, нужно или не нужно
                    incrementCounterToDo()

                } else if (response is Resource.Error) {

                    handleAddTaskError(response.message)

                }


            } else {


                handleNoInternetConnectionError()

            }

        } catch (e: Exception) {

            handleExceptionError(e.localizedMessage ?: "Unknown error")

        }

    }

    fun updateTask(item: ToDoItem) = viewModelScope.launch(Dispatchers.IO) {

        try {

            if (hasInternetConnection()) {

                val response = toDoRepository.updateTaskApi(item.id, item)
                if (response is Resource.Success) {

                    updateTaskDb(item) // Не знаю, нужно или не нужно

                } else if (response is Resource.Error) {

                    handleUpdateTaskError(response.message)

                }

            } else {
                updateTaskDb(item) //update task in local db if no internet connection
                handleNoInternetConnectionError()
            }
        } catch (e: Exception) {
            handleExceptionError(e.localizedMessage ?: "Unknown error")
        }
    }

    fun updateListApi(list:List<ToDoItem>){
        viewModelScope.launch(Dispatchers.IO){
            try {

                if (hasInternetConnection()) {

                    val response = toDoRepository.updateListApi(list)
                    if (response is Resource.Success) {

                        //updateTaskDb(item) // Не знаю, нужно или не нужно
                        Toast.makeText(app, "Data is uploaded", Toast.LENGTH_SHORT).show()

                    } else if (response is Resource.Error) {

                        Toast.makeText(app, "Error", Toast.LENGTH_SHORT).show()
                        handleUpdateTaskError(response.message)

                    }

                } else {
                    Toast.makeText(app, "No Internet connection", Toast.LENGTH_SHORT).show()
                    handleNoInternetConnectionError()
                }
            } catch (e: Exception) {
                handleExceptionError(e.localizedMessage ?: "Unknown error")
            }
        }
    }


    // Обработчики

    private fun handleDeleteTaskError(errorMessage: String?) {



    }

    private fun handleAddTaskError(errorMessage: String?) {



    }

    private fun handleUpdateTaskError(errorMessage: String?) {



    }

    private fun handleNoInternetConnectionError() {



    }

    private fun handleExceptionError(errorMessage: String) {



    }

    // Работа с базой данных

    fun updateListDb(list: List<ToDoItem>) = viewModelScope.launch(Dispatchers.IO) {
        toDoRepository.updateListDb(list)
    }

//    fun getSavedTasks(): Flow<List<ToDoItem>> {
//
//        return toDoRepository.getSavedToDo()
//
//    }

    fun deleteTaskByIdDb(id: UUID) = viewModelScope.launch(Dispatchers.IO) {

        toDoRepository.deleteTaskByIdDb(id)

    }

    fun addTaskDb(item: ToDoItem) = viewModelScope.launch(Dispatchers.IO) {

        toDoRepository.addTaskDb(item)

    }

    fun updateTaskDb(item: ToDoItem) = viewModelScope.launch(Dispatchers.IO) {

        toDoRepository.updateTaskDb(item)

    }

    // Подсчет сделанных дел

    fun incrementCounterToDo() {

        _counterToDo.value += 1

    }

    fun decrementCounterToDo() {

        _counterToDo.value -= 1

    }



    fun setTaskComplete(id: UUID){
        viewModelScope.launch(Dispatchers.IO) {
            val item = getTaskById(id)
            item.done=!item.done
            updateTask(item)
        }
    }


//    fun restoreItem(item: ToDoItem,
//                position: Int){
//        toDoRepository.restoreItem(item,position)
//    }

    fun getPositionById(itemId:UUID):Int{
        return toDoRepository.getPositionById(itemId)
    }

//    fun getCompleteNumber():Int{
//        return toDoRepository.getDoneNumber()
//    }
//
//    fun getAllTaskNumber():Int{
//        return toDoRepository.getTaskNumber()
//    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = app.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }


    //    fun getIncompleteToDoList(){
//        viewModelScope.launch(Dispatchers.IO){
//            toDoRepository.getListApi().map { it.data!!.list.filter { it -> !it.done } }
//        }
//    }

//    fun getItemById(id:UUID){
//        viewModelScope.launch {
//            _currentList.emit(Resource.Loading())
//
//            try {
//
//                if (hasInternetConnection()) {
//
//                    val response = toDoRepository.getTaskDb(id)
//                        .onStart { _currentList.emit(Resource.Loading()) }
//                        .catch { e -> _currentList.emit(Resource.Error("Error lol")) }
//                        .collect{result -> _currentList.emit(Resource.Success(result.data!!))}
//
//                } else {
//                    _currentList.emit(Resource.Error("No Internet connection"))
//                }
//
//            } catch (t: Throwable) {
//
//                when (t) {
//
//                    is IOException -> _currentList.emit(Resource.Error("Network Failure"))
//                    is HttpException -> _currentList.emit(Resource.Error("Failed: ${t.code()} ${t.message()}"))
//                    else -> _currentList.emit(Resource.Error("Conversion Error"))
//
//                }
//
//            }
//        }
//    }


}