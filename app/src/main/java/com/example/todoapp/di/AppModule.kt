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
    @AppScope
    fun provideContext(): Context = context

    @Provides
    @AppScope
    fun provideScope() = CoroutineScope(SupervisorJob())

    @Provides
    @AppScope
    fun provideApplication(): Application = (context.applicationContext as Application)
}