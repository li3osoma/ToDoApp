package com.example.todoapp.di

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerFactory
import com.example.todoapp.utils.ChildWorkerFactory
import com.example.todoapp.utils.MyWorkManager
import com.example.todoapp.utils.MyWorkManagerFactory
import com.example.todoapp.utils.NotificationUtil
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@MapKey
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class WorkerKey(val value: KClass<out ListenableWorker>)
@Module
interface MyWorkManagerModule {
//    @Provides
//    @AppScope
//    fun provideMyWorkManager(
//        context: Context,
//        workerParams:WorkerParameters,
//        notificationUtil: NotificationUtil,
//        repositoryImpl: ToDoRepositoryImpl): MyWorkManager =
//        MyWorkManager(context, workerParams, notificationUtil, repositoryImpl)

    @Binds
    @IntoMap
    @WorkerKey(MyWorkManager::class)
    fun bindMyWorkManagerFactory(factory: MyWorkManager.Factory): ChildWorkerFactory
//    @Binds
//    fun bindWorkManagerFactory(factory: MyWorkManagerFactory): WorkerFactory

//    @Binds
//    @IntoMap
//    @WorkerKey(MyWorkManager::class)
//    fun bindMyWorker(worker: MyWorkManager): Worker
}