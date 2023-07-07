package com.example.todoapp.datasource.network.dto

import com.example.todoapp.domain.model.ToDoItem
import com.google.gson.annotations.SerializedName

data class TaskListResponse (@SerializedName("status") val status : String,
                             @SerializedName("list") val list : List<ToDoItem>,
                             @SerializedName("revision")val revision: Int)