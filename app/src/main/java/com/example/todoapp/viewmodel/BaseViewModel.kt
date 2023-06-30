//package com.example.todoapp.viewmodel
//
//import android.content.Context
//import android.net.ConnectivityManager
//import android.net.NetworkCapabilities
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.ViewModel
//import com.example.todoapp.application.App
//import com.example.todoapp.repository.ToDoRepository
//
//abstract class BaseViewModel(val app: App, val toDoRepository: ToDoRepository){
//
//    private fun hasInternetConnection(): Boolean {
//        val connectivityManager = app.getSystemService(
//            Context.CONNECTIVITY_SERVICE
//        ) as ConnectivityManager
//
//        val activeNetwork = connectivityManager.activeNetwork ?: return false
//        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
//        return when {
//            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
//            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
//            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
//            else -> false
//        }
//    }
//}
