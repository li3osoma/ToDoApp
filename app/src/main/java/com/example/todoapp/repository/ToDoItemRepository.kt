package com.example.todoapp.repository

import com.example.todoapp.model.Importance
import com.example.todoapp.model.ToDoItem

typealias TaskListener = (users:List<ToDoItem>) -> Unit

class ToDoItemRepository {

    private var toDoItemList:MutableList<ToDoItem>
    private var doneNum=0
    private val listeners=mutableSetOf<TaskListener>()

    init {
        toDoItemList=generateItemList().toMutableList()
    }

    private fun generateItemList():List<ToDoItem>{
        var items = mutableListOf<ToDoItem>(
            ToDoItem("1", "Write a todo-app before deadline!!", Importance.HIGH, "June 17, 2023", true, "June 10, 2023", ""),
            ToDoItem("2", "Write a todo-app before deadline properly", Importance.NO, "June 10, 2023", false, "June 14, 2023", "June 16, 2023"),
            ToDoItem("3", "Plan a visit to the bar", Importance.NO, "", false, "June 14, 2023", ""),
            ToDoItem("4", "Find a kickboxing club", Importance.LOW, "", false, "June 14, 2023", "June 16, 2023"),
            ToDoItem("5", "Go to gym", Importance.LOW, "June 17, 2023", true, "June 10, 2023", ""),
            ToDoItem("6", "Task\nUsual task", Importance.NO, "June 11, 2023",true, "June 10, 2023", "June 10, 2023"),

            ToDoItem("7", "Call mom", Importance.HIGH, "", true, "June 14, 2023", ""),
            ToDoItem("8", "Choose a present for mom", Importance.HIGH, "June 22, 2023", false, "June 14, 2023", "June 16, 2023"),
            ToDoItem("9", "Task\nNeed to complete task\nIt's not so important\nBut have to be done", Importance.LOW, "June 17, 2023", true, "June 14, 2023", ""),
            ToDoItem("10", "Prepare for exam", Importance.LOW, "", false, "June 14, 2023", "June 16, 2023"),
            ToDoItem("11", "What is this task?", Importance.HIGH, "", false, "June 10, 2023", ""),
            ToDoItem("12", "Bye ingredients for salad", Importance.NO, "", true, "June 10, 2023", "June 10, 2023"),

        )
        doneNum=6
        return items
    }

    fun getItemList():List<ToDoItem>{
        return toDoItemList.reversed()
    }

    fun addItem(text:String,
                importance:Importance,
                date_deadline:String,
                is_complete:Boolean,
                date_creation:String,
                date_changing:String){
        toDoItemList.add(ToDoItem(generateId(), text, importance, date_deadline, is_complete, date_creation, date_changing))
        if(is_complete) doneNum++
        notifyChanges()
    }

    fun restoreItem(item:ToDoItem,
                position:Int){
        toDoItemList.add(position,item)
        if(item.is_complete) doneNum++
        notifyChanges()
    }

    fun getPositionById(itemId:String):Int{
        for(i in toDoItemList.indices){
            if(toDoItemList[i].id==itemId)
                return i
        }
        return -1
    }

    fun deleteItemById(id:String){
        if(getItemById(id).is_complete) doneNum--
        toDoItemList.remove(getItemById(id))
        notifyChanges()
    }

    fun getItemById(id:String): ToDoItem {
        return toDoItemList.find { toDoItem: ToDoItem -> toDoItem.id==id }!!
    }

    fun updateItemById(id:String,
                       text:String,
                       importance:Importance,
                       date_deadline:String,
                       is_complete:Boolean,
                       date_creation:String,
                       date_changing:String){
        val item = ToDoItem(id,
            text,
            importance,
            date_deadline,
            is_complete,
            date_creation,
            date_changing)
        var index:Int=0
        for(i in toDoItemList.indices){
            if(toDoItemList[i].id==id){
                index = i
                break
            }
        }
        if(toDoItemList[index].is_complete && !is_complete) doneNum--
        else if(!toDoItemList[index].is_complete && is_complete) doneNum++
        toDoItemList[index] = item
        notifyChanges()
    }

    private fun generateId():String{
        return (toDoItemList.size+1).toString()
    }

    fun getDoneNumber():Int{
        return doneNum
    }

    fun addListener(listener:TaskListener){
        listeners.add(listener)
        listener.invoke(toDoItemList)

    }

    fun deleteListener(listener: TaskListener){
        listeners.remove(listener)
        listener.invoke(toDoItemList)
    }

    private fun notifyChanges(){
        listeners.forEach{it.invoke(toDoItemList)}
    }
}