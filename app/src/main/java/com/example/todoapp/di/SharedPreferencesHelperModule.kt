package com.example.todoapp.di

import android.content.Context
import com.example.todoapp.datasource.persistence.SharedPreferencesHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SharedPreferencesHelperModule {
    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferencesHelper =
        SharedPreferencesHelper(context)
}