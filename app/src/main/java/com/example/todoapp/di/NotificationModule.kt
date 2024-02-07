package com.example.todoapp.di

import android.content.Context
import com.example.todoapp.utils.NotificationUtil
import dagger.Module
import dagger.Provides

@Module
class NotificationModule {
    @Provides
    @AppScope
    fun provideNotificationUtil(context: Context): NotificationUtil =
        NotificationUtil(context)
}