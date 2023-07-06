package com.example.todoapp

import kotlinx.coroutines.flow.Flow

interface ConnectionObserver {

    fun observe(): Flow<Status>

    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}