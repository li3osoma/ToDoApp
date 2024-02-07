package com.example.todoapp.di

import com.example.todoapp.App
import com.example.todoapp.ui.view.MainActivity
import com.example.todoapp.ui.view.ToDoItemEditFragment
import com.example.todoapp.ui.view.ToDoListFragment
import com.example.todoapp.ui.viewmodel.ViewModelFactory
import com.example.todoapp.utils.ChildWorkerFactory
import com.example.todoapp.utils.MyWorkManagerFactory
import dagger.BindsInstance
import dagger.Component

@AppScope
@Component(dependencies = [], modules = [DatabaseModule::class, NetworkModule::class,
    RepositoryModule::class, SharedPreferencesHelperModule::class, AppModule::class,
    MyWorkManagerModule::class, NotificationModule::class])
interface AppComponent {

//    @Component.Builder
//    interface Builder {
//
//        @BindsInstance
//        fun application(application: App): Builder
//
//        fun build(): AppComponent
//    }
    fun viewModelFactory(): ViewModelFactory

    fun myWorkManagerFactory():MyWorkManagerFactory
    fun inject(activity: MainActivity)
    fun inject(fragment:ToDoListFragment)
    fun inject(fragment:ToDoItemEditFragment)

}