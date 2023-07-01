package com.example.todoapp.viewmodel
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.todoapp.application.App
import com.example.todoapp.model.ToDoItem
import com.example.todoapp.repository.ToDoRepository
import com.example.todoapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class ToDoItemEditViewModel(private val app: App, private val toDoRepository: ToDoRepository):AndroidViewModel(app) {

    var _currentTask:ToDoItem = createDefaultTask()

    fun createDefaultTask():ToDoItem{
        return ToDoItem(UUID.randomUUID(), "", ToDoItem.Importance.basic,
        Date().time, false, "#000000", Date().time, Date().time)
    }

//    fun getCurrentTask(id: UUID):ToDoItem{
//        getTaskById(id)
//        return currentTask
//    }
    suspend fun getTaskById(id: UUID){
        _currentTask=toDoRepository.getTaskDb(id)
        Log.println(Log.INFO, "_CHECK CURRENT", "${_currentTask}")
        Log.println(Log.INFO, "CHECK CURRENT", "${_currentTask}")
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
                toDoRepository.needToSynchronize=true
                handleNoInternetConnectionError()
            }
        } catch (e: Exception) {
            handleExceptionError(e.localizedMessage ?: "Unknown error")
        }
    }
    fun deleteTaskById(id: UUID) = viewModelScope.launch(Dispatchers.IO) {

        try {
            if (hasInternetConnection()) {

                val response = toDoRepository.deleteTaskByIdApi(id)

                if (response is Resource.Success) {

                    deleteTaskByIdDb(id) // Не знаю, нужно или не нужно

                } else if (response is Resource.Error) {

                    handleDeleteTaskError(response.message)

                }

            } else {

                deleteTaskByIdDb(id) //delete task in local db if no internet connection
                toDoRepository.needToSynchronize=true
                handleNoInternetConnectionError()

            }

        } catch (e: Exception) {

            handleExceptionError(e.localizedMessage ?: "Unknown error")

        }

    }
    fun addTask(item: ToDoItem) = viewModelScope.launch(Dispatchers.IO) {

        try {

            if (hasInternetConnection()) {

                val response = toDoRepository.addTaskApi(item)

                if (response is Resource.Success) {

                    addTaskDb(item) // Не знаю, нужно или не нужно

                } else if (response is Resource.Error) {

                    handleAddTaskError(response.message)

                }

            } else {

                addTaskDb(item) //add task in local db if no internet connection
                toDoRepository.needToSynchronize=true
                handleNoInternetConnectionError()

            }

        } catch (e: Exception) {

            handleExceptionError(e.localizedMessage ?: "Unknown error")

        }

    }

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

//    fun getItemById(id: String):LiveData<ToDoItem>{
//        _itemDetails.value=toDoRepository.getItemById(id)
//        return itemDetails
//    }
//
//    fun deleteItemById(id:String){
//        toDoRepository.deleteItemById(id)
//    }
//
//    fun updateItemById(id:String,
//                       text:String,
//                       importance: ToDoItem.Importance,
//                       dateDeadline:String,
//                       isComplete:Boolean,
//                       color:String,
//                       dateCreation:String,
//                       dateChanging:String){
//        toDoRepository.updateItemByIdDb(id, text, importance, dateDeadline, isComplete, color, dateCreation, dateChanging)
//    }
//
//    fun addItem(text:String,
//                importance:Importance,
//                dateDeadline:String,
//                isComplete:Boolean,
//                dateCreation:String,
//                dateChanging:String){
//        toDoRepository.addItem(text, importance, dateDeadline, isComplete, dateCreation, dateChanging)
//    }



}