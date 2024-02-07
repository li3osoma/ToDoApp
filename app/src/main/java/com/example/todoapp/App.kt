package com.example.todoapp

import android.app.Application
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.todoapp.di.AppComponent
import com.example.todoapp.di.AppModule
import com.example.todoapp.di.DaggerAppComponent
import com.example.todoapp.utils.MyWorkManager
import com.example.todoapp.utils.MyWorkManagerFactory
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class App: Application() {

    lateinit var appComponent: AppComponent

    lateinit var myWorkManagerFactory: MyWorkManagerFactory

    @Inject
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(applicationContext))
            .build()

        myWorkManagerFactory = appComponent.myWorkManagerFactory()

        val request = PeriodicWorkRequestBuilder<MyWorkManager>(
            repeatInterval = Duration.ofHours(24)
        ).setInitialDelay(5000, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setWorkerFactory(myWorkManagerFactory)
                .build()
        )


        val instance = WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
            "NOTIFY",
            ExistingPeriodicWorkPolicy.UPDATE,
            request)
    }

}