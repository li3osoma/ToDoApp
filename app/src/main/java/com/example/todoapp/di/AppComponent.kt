package com.example.todoapp.di

import com.example.todoapp.ui.view.MainActivity
import com.example.todoapp.ui.view.ToDoItemEditFragment
import com.example.todoapp.ui.view.ToDoListFragment
import com.example.todoapp.ui.viewmodel.ViewModelFactory
import dagger.Component
import javax.inject.Singleton

@AppScope
@Component(dependencies = [], modules = [DatabaseModule::class, NetworkModule::class,
    RepositoryModule::class, SharedPreferencesHelperModule::class, AppModule::class])
interface AppComponent {
    fun viewModelFactory(): ViewModelFactory
    fun inject(activity: MainActivity)
    fun inject(fragment:ToDoListFragment)
    fun inject(fragment:ToDoItemEditFragment)
}