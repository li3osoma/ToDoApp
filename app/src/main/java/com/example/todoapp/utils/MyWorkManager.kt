package com.example.todoapp.utils

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.todoapp.datasource.repository.ToDoRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Provider

class MyWorkManager(
    private val context: Context,
    workerParams: WorkerParameters,
    private val notificationUtil: NotificationUtil,
    private val repositoryImpl: ToDoRepositoryImpl,
    private val coroutineScope: CoroutineScope
) : Worker(context, workerParams)

{

    private lateinit var job:Job
    override fun doWork(): Result {

        Log.d("MVM", "STARTED")
        job = coroutineScope.launch {
            repositoryImpl.getListDb().find {
                //Log.d("MVVM CHECK", "${it.deadline}\n${DateUtils.dateToLong(Date())}\n${DateUtils.dateToLong(Date())+24*60*60*1000}")
                    (it.deadline!=null
                            && DateUtils.dateToLong(Date())<=it.deadline!!
                            && DateUtils.dateToLong(Date())+24*60*60*1000>=it.deadline!!
                            )
                }?.let {todo ->
                    context.getSystemService(NotificationManager::class.java).notify(
                        101,
                        notificationUtil.create(title = "Deadline!", text = "${todo.text} and other..")
                    )
                }
        }
        job.cancel()

        return Result.success()
    }

    class Factory @Inject constructor(
        private val notificationUtil: Provider<NotificationUtil>,
        private val repositoryImpl: Provider<ToDoRepositoryImpl>,
        private val coroutineScope: Provider<CoroutineScope>
    ): ChildWorkerFactory {

        override fun create(appContext: Context, params: WorkerParameters): Worker {
            return MyWorkManager(appContext, params, notificationUtil.get(), repositoryImpl.get(), coroutineScope.get())
        }
    }
}