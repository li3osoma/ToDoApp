package com.example.todoapp.di

import com.example.todoapp.datasource.repository.ToDoRepositoryImpl
import com.example.todoapp.domain.repository.ToDoRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface RepositoryModule {
    @Singleton
    @Binds
    fun bindTodoRepository(repositoryImpl: ToDoRepositoryImpl): ToDoRepository
}