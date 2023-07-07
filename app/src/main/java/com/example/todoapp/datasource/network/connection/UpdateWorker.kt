package com.example.todoapp.datasource.network.connection

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.todoapp.domain.repository.ToDoRepository
import com.example.todoapp.datasource.network.connection.Resource
import com.example.todoapp.utils.localeLazy
import kotlinx.coroutines.runBlocking

class UpdateWorker(context: Context, params: WorkerParameters)
    : Worker(context, params) {

    private val repository: ToDoRepository by localeLazy()
    override fun doWork(): Result {
        return when (syncData()) {
            is Resource.Success -> Result.success()
            else -> {
                Result.failure()
            }
        }
    }

    private fun syncData() = runBlocking {
        return@runBlocking repository.loadList()
    }
}