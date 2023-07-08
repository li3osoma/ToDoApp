package com.example.todoapp.datasource.network.connection

import kotlinx.coroutines.flow.Flow

/*

Network connection states

 */
interface ConnectionObserver {

    fun observe(): Flow<Status>

    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}