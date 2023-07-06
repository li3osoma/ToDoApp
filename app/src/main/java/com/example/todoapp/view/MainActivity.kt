package com.example.todoapp.view
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.todoapp.UpdateWorker
import com.example.todoapp.application.App
import com.example.todoapp.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(){

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        periodicUpdate()
    }
    private fun periodicUpdate() {
        val constraints: Constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()


        val myWorkRequest = PeriodicWorkRequest.Builder(
            UpdateWorker::class.java,
            8,
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .addTag("update_data")
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "update_data",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            myWorkRequest
        )
    }
}