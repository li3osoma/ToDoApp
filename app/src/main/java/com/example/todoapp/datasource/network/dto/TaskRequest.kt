package com.example.todoapp.datasource.network.dto

import com.example.todoapp.domain.model.ToDoItem
import com.google.gson.annotations.SerializedName

class TaskRequest(@SerializedName("status") val status : String,
                  @SerializedName("element") val element : ToDoItem
)