package com.example.todoapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todoapp.model.Importance
import com.example.todoapp.model.ToDoItem
import com.example.todoapp.repository.ToDoItemRepository

class ToDoItemEditViewModel(private val toDoItemRepository: ToDoItemRepository): ViewModel() {
    private val _itemDetails=MutableLiveData<ToDoItem>()
    private val itemDetails:LiveData<ToDoItem> = _itemDetails

    fun getItemById(id:String):LiveData<ToDoItem>{
        _itemDetails.value=toDoItemRepository.getItemById(id)
        return itemDetails
    }

    fun deleteItemById(id:String){
        toDoItemRepository.deleteItemById(id)
    }

    fun updateItemById(id:String,
                       text:String,
                       importance:Importance,
                       date_deadline:String,
                       is_complete:Boolean,
                       date_creation:String,
                       date_changing:String){
        toDoItemRepository.updateItemById(id, text, importance, date_deadline, is_complete, date_creation, date_changing)
    }

    fun addItem(text:String,
                importance:Importance,
                date_deadline:String,
                is_complete:Boolean,
                date_creation:String,
                date_changing:String){
        toDoItemRepository.addItem(text, importance, date_deadline, is_complete, date_creation, date_changing)
    }

}