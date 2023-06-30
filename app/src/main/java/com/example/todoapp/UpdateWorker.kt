//package com.example.todoapp
//
//import android.content.Context
////import androidx.work.CoroutineWorker
////import androidx.work.WorkerParameters
//import com.example.todoapp.repository.ToDoRepository
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
//class UpdateWorker(context: Context, params: WorkerParameters)
//    : CoroutineWorker(context, params) {
//
//    override suspend fun doWork(): Result {
//        return withContext(Dispatchers.IO){
//            try {
//
//                Result.success()
//            }catch (e: Exception){
//                Result.failure()
//            }
//        }
//    }
//}