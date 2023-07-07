package com.example.todoapp.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
class AppModule(
    private val context: Context
) {
    @Provides
    @Singleton
    fun provideContext(): Context = context

    @Provides
    @Singleton
    fun provideScope() = CoroutineScope(SupervisorJob())

    @Provides
    @Singleton
    fun provideApplication(): Application = (context.applicationContext as Application)
}