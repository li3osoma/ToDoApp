package com.example.todoapp.model

enum class Importance {
    NO,
    LOW,
    HIGH
}
data class ToDoItem(
    var id:String,
    var text:String,
    var importance:Importance,
    var date_deadline:String,
    var is_complete:Boolean=false,
    var date_creation:String,
    var date_changing:String
){
}