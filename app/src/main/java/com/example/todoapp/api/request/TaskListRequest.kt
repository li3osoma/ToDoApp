package com.example.todoapp.api.request

import com.example.todoapp.model.ToDoItem
import com.google.gson.annotations.SerializedName

data class TaskListRequest(@SerializedName("status") val status : String,
                           @SerializedName("list") val list : List<ToDoItem>)