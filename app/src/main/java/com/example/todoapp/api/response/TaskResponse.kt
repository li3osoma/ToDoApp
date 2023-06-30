package com.example.todoapp.api.response

import com.example.todoapp.model.ToDoItem
import com.google.gson.annotations.SerializedName

data class TaskResponse (@SerializedName("status") val status : String,
                         @SerializedName("element") val element : ToDoItem,
                         @SerializedName("revision")val revision: Int)