package com.example.todoapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.App
import com.example.todoapp.datasource.network.connection.NetworkConnectionObserver
import com.example.todoapp.datasource.repository.ToDoRepositoryImpl
import javax.inject.Inject

class ViewModelFactory @Inject constructor(
    private val application: Application,
    private val toDoRepositoryImpl: ToDoRepositoryImpl,
    private val networkConnectionObserver: NetworkConnectionObserver
):ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when (modelClass) {
            ToDoViewModel::class.java -> {
                ToDoViewModel(application, toDoRepositoryImpl, networkConnectionObserver)
            }
            else -> {
                throw IllegalStateException("Unknown view model class")
            }
        }
        return viewModel as T
    }
}
