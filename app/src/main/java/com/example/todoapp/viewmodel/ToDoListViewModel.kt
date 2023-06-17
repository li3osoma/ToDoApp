package com.example.todoapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todoapp.model.ToDoItem
import com.example.todoapp.repository.TaskListener
import com.example.todoapp.repository.ToDoItemRepository
import java.text.FieldPosition

class ToDoListViewModel(private var toDoItemRepository: ToDoItemRepository):ViewModel() {
    private val _mToDoItemList=MutableLiveData<List<ToDoItem>>()
    private val mToDoItemList: LiveData<List<ToDoItem>> = _mToDoItemList

    private val _itemDetails=MutableLiveData<ToDoItem>()
    private val itemDetails:LiveData<ToDoItem> = _itemDetails

    fun getToDoList(): LiveData<List<ToDoItem>> {
        _mToDoItemList.value=toDoItemRepository.getItemList()
        return mToDoItemList
    }

    private fun notifyChanges(){
        _mToDoItemList.value=toDoItemRepository.getItemList()
    }

    fun getItemById(id:String):LiveData<ToDoItem>{
        _itemDetails.value=toDoItemRepository.getItemById(id)
        return itemDetails
    }

    fun updateItemById(id:String,
                       text:String,
                       importance:String,
                       date_deadline:String,
                       is_complete:Boolean,
                       date_creation:String,
                       date_changing:String){
        toDoItemRepository.updateItemById(id, text, importance, date_deadline, is_complete, date_creation, date_changing)
        notifyChanges()
    }

    fun setTaskComplete(id: String){
        val item=getItemById(id).value!!
        updateItemById(id,
        item.text,
        item.importance,
        item.date_deadline,
        !item.is_complete,
        item.date_creation,
        item.date_changing)
    }

    fun deleteItemById(id:String){
        toDoItemRepository.deleteItemById(id)
        notifyChanges()
    }

    fun restoreItem(item: ToDoItem,
                position: Int){
        toDoItemRepository.restoreItem(item,position)
        notifyChanges()
    }

    fun getPositionById(itemId:String):Int{
        return toDoItemRepository.getPositionById(itemId)
    }

    fun addListener(taskListener: TaskListener){
        toDoItemRepository.addListener(taskListener)
    }

    fun deleteListener(taskListener: TaskListener){
        toDoItemRepository.deleteListener(taskListener)
    }

    fun getCompleteNumber():Int{
        return toDoItemRepository.getDoneNumber()
    }
}