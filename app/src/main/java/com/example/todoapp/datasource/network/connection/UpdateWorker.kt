package com.example.todoapp.datasource.network.connection

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.todoapp.datasource.repository.ToDoRepositoryImpl
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/*

Periodic update manager description

 */

class UpdateWorker(context: Context, params: WorkerParameters)
    : Worker(context, params) {

    @Inject
    lateinit var repository: ToDoRepositoryImpl
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