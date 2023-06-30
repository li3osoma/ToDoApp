package com.example.todoapp.api.request

import com.example.todoapp.model.ToDoItem
import com.google.gson.annotations.SerializedName

class TaskRequest(@SerializedName("status") val status : String,
                  @SerializedName("element") val element : ToDoItem
)