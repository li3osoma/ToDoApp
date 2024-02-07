package com.example.todoapp.ui.viewmodel
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.datasource.network.connection.ConnectionObserver
import com.example.todoapp.datasource.network.connection.NetworkConnectionObserver
import com.example.todoapp.datasource.network.dto.TaskListResponse
import com.example.todoapp.domain.model.ToDoItem
import com.example.todoapp.datasource.network.connection.Resource
import com.example.todoapp.datasource.repository.ToDoRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/*

ViewModel

 */
class ToDoViewModel @Inject constructor(
    private val application: Application,
    private val toDoRepositoryImpl: ToDoRepositoryImpl,
    private val connection: NetworkConnectionObserver
): AndroidViewModel(application) {

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


    private fun createDefaultTask(): ToDoItem {
        return ToDoItem(
            UUID.randomUUID(), "", ToDoItem.Importance.basic,
            Date().time, false, "#000000", Date().time, Date().time
        )
    }

    init {
        observeNetwork()
        getList()
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            connection.observe().collectLatest {
                _status.emit(it)
            }
        }
    }

    fun changeMode() {
        modeVisibility = !modeVisibility
        job?.cancel()
        getList()
    }

    fun getList() {
        job = viewModelScope.launch(Dispatchers.IO) {
            _list.emitAll(toDoRepositoryImpl.getListDbFlow())
        }
        Log.println(Log.INFO, "LOAD LIST", "")
    }

    fun getTaskById(id: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            _item.value = toDoRepositoryImpl.getTaskDb(id)
        }
    }

    fun loadList() {
        if (status.value == ConnectionObserver.Status.Available) {
            _loading.value = Resource.Loading()
            viewModelScope.launch(Dispatchers.IO) {
                _loading.emit(toDoRepositoryImpl.loadList())
            }
        }
    }

    fun addTaskDb(item: ToDoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            toDoRepositoryImpl.addTaskDb(item)
        }
    }

    fun deleteTaskDb(item: ToDoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            toDoRepositoryImpl.deleteTaskDb(item)
        }
    }

    fun updateTaskDb(item: ToDoItem) {
        item.changed_at = Date().time
        viewModelScope.launch(Dispatchers.IO) {
            toDoRepositoryImpl.updateTaskDb(item)
        }
        Log.println(Log.INFO, "UPDATE DB", item.done.toString())
    }

    fun addTaskApi(item: ToDoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            toDoRepositoryImpl.addTaskApi(item)
        }
    }

    fun deleteTaskByIdApi(id: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            toDoRepositoryImpl.deleteTaskByIdApi(id)
        }
        Log.println(Log.INFO, "DELETE API 0", id.toString())
    }

    fun updateTaskApi(item: ToDoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            toDoRepositoryImpl.updateTaskApi(item.id, item)
        }
        Log.println(Log.INFO, "UPDATE API", item.done.toString())
    }


    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    fun getPositionById(id: UUID): Int {
        var ind = 0
        viewModelScope.launch(Dispatchers.IO) {
            val toDoList = list.toList()[0]
            for (i in 0..toDoList.size) {
                if (toDoList[i].id == id) {
                    ind = i
                    break
                }
            }
        }
        return ind
    }

  fun restoreTask(item: ToDoItem, position:Int){
      viewModelScope.launch(Dispatchers.IO) {
          toDoRepositoryImpl.restoreTask(item, position, list.toList()[0])
      }
  }

    fun restoreTaskDb(item: ToDoItem, position:Int){
        viewModelScope.launch(Dispatchers.IO) {
            toDoRepositoryImpl.restoreTaskDb(item, position, list.toList()[0])
        }
    }
}