package com.example.todoapp.utils

import com.example.todoapp.api.request.TaskListRequest
import com.example.todoapp.api.request.TaskRequest
import com.example.todoapp.api.response.TaskListResponse
import com.example.todoapp.api.response.TaskResponse
import com.example.todoapp.model.ToDoItem
import java.util.UUID

class TypeUtils {
    companion object{

        @JvmStatic
        fun modelToRequest(status:String, toDoItem: ToDoItem) = TaskRequest(status, toDoItem)

        @JvmStatic
        fun responseToModel(response: TaskResponse) = response.element


        @JvmStatic
        fun listResponseToListModel(response:TaskListResponse) = response.list

        @JvmStatic
        fun listModelToListRequest(status: String, list:List<ToDoItem>) = TaskListRequest(status, list)



    }
}