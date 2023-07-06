package com.example.todoapp.viewmodel
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.ConnectionObserver
import com.example.todoapp.NetworkConnectionObserver
import com.example.todoapp.api.response.TaskListResponse
import com.example.todoapp.application.App
import com.example.todoapp.model.ToDoItem
import com.example.todoapp.repository.ToDoRepository
import com.example.todoapp.utils.DateUtils
import com.example.todoapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.*

class ToDoViewModel(private val app: App,
                    private val toDoRepository: ToDoRepository,
                    private val connection: NetworkConnectionObserver
):AndroidViewModel(app) {

    var modeVisibility: Boolean = true
    private var job: Job? = null

    private val _status = MutableStateFlow(ConnectionObserver.Status.Unavailable)
    val status = _status.asStateFlow()

    private val _list = MutableSharedFlow<List<ToDoItem>>()
    val list: SharedFlow<List<ToDoItem>> = _list.asSharedFlow()

    private val _loading = MutableStateFlow<Resource<TaskListResponse>>(Resource.Loading())
    val loading: StateFlow<Resource<TaskListResponse>> = _loading.asStateFlow()


    private var _item = MutableStateFlow(createDefaultTask())
    var item = _item.asStateFlow()

//    private val _currentList= MutableStateFlow<Resource<List<ToDoItem>>>(Resource.Loading())
//    private val currentList: StateFlow<Resource<List<ToDoItem>>> = _currentList

    //    var doneNum = 0
//    var taskNum = 0
    //var _currentTask:ToDoItem = createDefaultTask()

//    private val _counterToDo: MutableStateFlow<Int> = MutableStateFlow(0)
//    val counterToDo: StateFlow<Int> = _counterToDo
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
        observeNetwork()
        getList()
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            connection.observe().collectLatest{
                _status.emit(it)
            }
        }
    }

    fun changeMode() {
        modeVisibility = !modeVisibility
        job?.cancel()
        getList()
    }

    fun getList(){
        job = viewModelScope.launch(Dispatchers.IO) {
            _list.emitAll(toDoRepository.getListDb())
        }
    }

    fun getTaskById(id: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            _item.value = toDoRepository.getTaskDb(id)
        }
    }

    fun loadList() {
        if (status.value == ConnectionObserver.Status.Available) {
            _loading.value = Resource.Loading()
            viewModelScope.launch(Dispatchers.IO) {
                _loading.emit(toDoRepository.loadList())
            }
        }
    }

    fun addTaskDb(item: ToDoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            toDoRepository.addTaskDb(item)
        }
    }

    fun deleteTaskDb(id: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            toDoRepository.deleteTaskByIdDb(id)
        }
    }

    fun updateTaskDb(item: ToDoItem) {
        item.changed_at = Date().time
        viewModelScope.launch(Dispatchers.IO) {
            toDoRepository.updateTaskDb(item)
        }
        Log.println(Log.INFO, "UPDATE DB", item.done.toString())
    }


//    fun changeCompleteDb(id: UUID) {
//        getTaskById(id)
//        val item1=item.value.copy(done = !item.value.done)
//        viewModelScope.launch(Dispatchers.IO) {
//            toDoRepository.updateTaskDb(item1)
//        }
//    }

    fun addTaskApi(item: ToDoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            toDoRepository.addTaskApi(item)
        }
    }

    fun deleteTaskByIdApi(id: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            toDoRepository.deleteTaskByIdApi(id)
        }
    }

    fun updateTaskApi(item: ToDoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            toDoRepository.updateTaskApi(item.id, item)
        }
        Log.println(Log.INFO, "UPDATE API", item.done.toString())
    }


    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    fun getPositionById(id:UUID):Int{
        var ind=0
        viewModelScope.launch {
            val toDoList=list.toList()[0]
            for(i in 0..toDoList.size){
                if(toDoList[i].id==id) {
                    ind = i
                    break
                }
            }
        }
        return ind
    }




//    private fun loadList(){
//        viewModelScope.launch(Dispatchers.IO){
//            //_currentList.emit(Resource.Loading())
//            try {
//                if (hasInternetConnection()) {
//
//                    val response = toDoRepository.loadList()
//                        .onStart { _currentList.emit(Resource.Loading()) }
//                        .catch { e -> _currentList.emit(Resource.Error("Error lol")) }
//                        .collect{result -> _currentList.emit(Resource.Success(result.data!!))
//                            //_counterToDo.emit(Resource.Success(result.data.size).data!!)
//                        }
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


    //    fun getTaskById(id: UUID){
//        viewModelScope.launch {
//            _currentTask.emit(toDoRepository.getTaskDb(id))
//        }
//    }
//    fun deleteTaskById(id: UUID) = viewModelScope.launch(Dispatchers.IO) {
//
//        try {
//
//            if (hasInternetConnection()) {
//
//                val response = toDoRepository.deleteTaskByIdApi(id)
//
//                if (response is Resource.Success) {
//
//                    deleteTaskByIdDb(id) // Не знаю, нужно или не нужно
//                    decrementCounterToDo()
//
//                } else if (response is Resource.Error) {
//
//                    handleDeleteTaskError(response.message)
//
//                }
//
//            } else {
//
//                deleteTaskByIdDb(id)
//                toDoRepository.needToSynchronize=true
//                handleNoInternetConnectionError()
//
//            }
//
//        } catch (e: Exception) {
//
//            deleteTaskByIdDb(id) //delete task in local db if no internet connection
//            handleExceptionError(e.localizedMessage ?: "Unknown error")
//
//        }
//
//    }

    fun updateList(){
//        viewModelScope.launch{
//            getList().collect{
//                _currentList.emit(Resource.Success(it))
//            }
//        }
//        updateListApi(_currentList.value.data!!)
        loadList()
    }

//    fun addTask(item: ToDoItem) = viewModelScope.launch(Dispatchers.IO) {
//
//        try {
//
//            if (hasInternetConnection()) {
//
//                val response = toDoRepository.addTaskApi(item)
//
//                if (response is Resource.Success) {
//
//                    addTaskDb(item) // Не знаю, нужно или не нужно
//                    incrementCounterToDo()
//
//                } else if (response is Resource.Error) {
//
//                    handleAddTaskError(response.message)
//
//                }
//
//
//            } else {
//
//
//                handleNoInternetConnectionError()
//
//            }
//
//        } catch (e: Exception) {
//
//            handleExceptionError(e.localizedMessage ?: "Unknown error")
//
//        }
//
//    }
//
//    fun updateTask(item: ToDoItem) = viewModelScope.launch(Dispatchers.IO) {
//
//        try {
//
//            if (hasInternetConnection()) {
//
//                val response = toDoRepository.updateTaskApi(item.id, item)
//                if (response is Resource.Success) {
//
//                    updateTaskDb(item) // Не знаю, нужно или не нужно
//
//                } else if (response is Resource.Error) {
//
//                    handleUpdateTaskError(response.message)
//
//                }
//
//            } else {
//                updateTaskDb(item) //update task in local db if no internet connection
//                handleNoInternetConnectionError()
//            }
//        } catch (e: Exception) {
//            handleExceptionError(e.localizedMessage ?: "Unknown error")
//        }
//    }
//
//    fun updateListApi(list:List<ToDoItem>){
//        viewModelScope.launch(Dispatchers.IO){
//            try {
//
//                if (hasInternetConnection()) {
//
//                    val response = toDoRepository.updateListApi(list)
//                    if (response is Resource.Success) {
//
//                        //updateTaskDb(item) // Не знаю, нужно или не нужно
//                        Toast.makeText(app, "Data is uploaded", Toast.LENGTH_SHORT).show()
//
//                    } else if (response is Resource.Error) {
//
//                        Toast.makeText(app, "Error", Toast.LENGTH_SHORT).show()
//                        handleUpdateTaskError(response.message)
//
//                    }
//
//                } else {
//                    Toast.makeText(app, "No Internet connection", Toast.LENGTH_SHORT).show()
//                    handleNoInternetConnectionError()
//                }
//            } catch (e: Exception) {
//                handleExceptionError(e.localizedMessage ?: "Unknown error")
//            }
//        }
//    }
//
//
//    // Обработчики
//
//    private fun handleDeleteTaskError(errorMessage: String?) {
//
//
//
//    }
//
//    private fun handleAddTaskError(errorMessage: String?) {
//
//
//
//    }
//
//    private fun handleUpdateTaskError(errorMessage: String?) {
//
//
//
//    }
//
//    private fun handleNoInternetConnectionError() {
//
//
//
//    }
//
//    private fun handleExceptionError(errorMessage: String) {
//
//
//
//    }
//
//    // Работа с базой данных
//
//    fun updateListDb(list: List<ToDoItem>) = viewModelScope.launch(Dispatchers.IO) {
//        toDoRepository.updateListDb(list)
//    }
//
////    fun getSavedTasks(): Flow<List<ToDoItem>> {
////
////        return toDoRepository.getSavedToDo()
////
////    }
//
//    fun deleteTaskByIdDb(id: UUID) = viewModelScope.launch(Dispatchers.IO) {
//
//        toDoRepository.deleteTaskByIdDb(id)
//
//    }
//
//    fun addTaskDb(item: ToDoItem) = viewModelScope.launch(Dispatchers.IO) {
//
//        toDoRepository.addTaskDb(item)
//
//    }
//
//    fun updateTaskDb(item: ToDoItem) = viewModelScope.launch(Dispatchers.IO) {
//
//        toDoRepository.updateTaskDb(item)
//
//    }
//
//    // Подсчет сделанных дел
//
//    fun incrementCounterToDo() {
//
//        _counterToDo.value += 1
//
//    }
//
//    fun decrementCounterToDo() {
//
//        _counterToDo.value -= 1
//
//    }
//
//
//
//    fun setTaskComplete(id: UUID){
//        viewModelScope.launch(Dispatchers.IO) {
//            val item = getTaskById(id)
//            item.done=!item.done
//            updateTask(item)
//        }
//    }
//
//
////    fun restoreItem(item: ToDoItem,
////                position: Int){
////        toDoRepository.restoreItem(item,position)
////    }
//
//    fun getPositionById(itemId:UUID):Int{
//        return toDoRepository.getPositionById(itemId)
//    }
//
////    fun getCompleteNumber():Int{
////        return toDoRepository.getDoneNumber()
////    }
////
////    fun getAllTaskNumber():Int{
////        return toDoRepository.getTaskNumber()
////    }
//
//    private fun hasInternetConnection(): Boolean {
//        val connectivityManager = app.getSystemService(
//            Context.CONNECTIVITY_SERVICE
//        ) as ConnectivityManager
//
//        val activeNetwork = connectivityManager.activeNetwork ?: return false
//        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
//        return when {
//            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
//            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
//            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
//            else -> false
//        }
//    }
//
//
//    //    fun getIncompleteToDoList(){
////        viewModelScope.launch(Dispatchers.IO){
////            toDoRepository.getListApi().map { it.data!!.list.filter { it -> !it.done } }
////        }
////    }
//
////    fun getItemById(id:UUID){
////        viewModelScope.launch {
////            _currentList.emit(Resource.Loading())
////
////            try {
////
////                if (hasInternetConnection()) {
////
////                    val response = toDoRepository.getTaskDb(id)
////                        .onStart { _currentList.emit(Resource.Loading()) }
////                        .catch { e -> _currentList.emit(Resource.Error("Error lol")) }
////                        .collect{result -> _currentList.emit(Resource.Success(result.data!!))}
////
////                } else {
////                    _currentList.emit(Resource.Error("No Internet connection"))
////                }
////
////            } catch (t: Throwable) {
////
////                when (t) {
////
////                    is IOException -> _currentList.emit(Resource.Error("Network Failure"))
////                    is HttpException -> _currentList.emit(Resource.Error("Failed: ${t.code()} ${t.message()}"))
////                    else -> _currentList.emit(Resource.Error("Conversion Error"))
////
////                }
////
////            }
////        }
////    }
//
//
//
//
//


}