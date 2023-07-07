package com.example.todoapp

import android.app.Application
import com.example.todoapp.di.AppComponent
import com.example.todoapp.di.AppModule
import com.example.todoapp.di.DaggerAppComponent
import javax.inject.Inject

class App: Application() {

    lateinit var appComponent: AppComponent

    @Inject
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(applicationContext))
            .build()

    }



}