package com.example.todoapp.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.application.App
import com.example.todoapp.viewmodel.ToDoItemEditViewModel
import com.example.todoapp.viewmodel.ToDoListViewModel

class ViewModelFactory(private val app: App):ViewModelProvider.Factory{

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when (modelClass) {
            ToDoListViewModel::class.java -> {
                ToDoListViewModel(app.toDoItemRepository)
            }
            ToDoItemEditViewModel::class.java -> {
                ToDoItemEditViewModel(app.toDoItemRepository)
            }
            else -> {
                throw IllegalStateException("Unknown view model class")
            }
        }
        return viewModel as T
    }

}

fun Fragment.factory() = ViewModelFactory(requireContext().applicationContext as App)