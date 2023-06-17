package com.example.todoapp.model

data class ToDoItem(
    var id:String,
    var text:String,
    var importance:String="Обычная",
    var date_deadline:String="",
    var is_complete:Boolean,
    var date_creation:String,
    var date_changing:String=""
){
}