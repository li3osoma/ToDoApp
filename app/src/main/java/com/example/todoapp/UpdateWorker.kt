package com.example.todoapp

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.todoapp.repository.ToDoRepository
import com.example.todoapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

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