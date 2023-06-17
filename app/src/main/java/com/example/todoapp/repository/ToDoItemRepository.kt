package com.example.todoapp.repository

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
            ToDoItem("1", "Task01", "No", "17.06.2023", false, "14.06.2023", ""),
            ToDoItem("2", "Task02", "No", "17.06.2023", false, "14.06.2023", "16.06.2023"),
            ToDoItem("3", "Task03", "No", "17.06.2023", true, "14.06.2023", ""),
            ToDoItem("4", "Task04", "No", "17.06.2023", false, "14.06.2023", "16.06.2023"),
            ToDoItem("5", "Task05", "No", "11.06.2023", false, "10.06.2023", ""),
            ToDoItem("6", "Task06\nUsual task", "No", "11.06.2023", false, "10.06.2023", "10.06.2023"),

            ToDoItem("7", "Task11", "Low", "17.06.2023", false, "14.06.2023", ""),
            ToDoItem("8", "Task12", "Low", "17.06.2023", false, "14.06.2023", "16.06.2023"),
            ToDoItem("9", "Task13\nNeed to complete task\nIt's not so important\nBut have to be done", "Low", "17.06.2023", true, "14.06.2023", ""),
            ToDoItem("10", "Task14", "Low", "17.06.2023", false, "14.06.2023", "16.06.2023"),
            ToDoItem("11", "Task15", "Low", "11.06.2023", false, "10.06.2023", ""),
            ToDoItem("12", "Task16", "Low", "11.06.2023", false, "10.06.2023", "10.06.2023"),

            ToDoItem("13", "Task21\nImportant task", "High", "17.06.2023", false, "14.06.2023", ""),
            ToDoItem("14", "Task22", "High", "17.06.2023", false, "14.06.2023", "16.06.2023"),
            ToDoItem("15", "Task23", "High", "17.06.2023", true, "14.06.2023", ""),
            ToDoItem("16", "Task24", "High", "17.06.2023", false, "14.06.2023", "16.06.2023"),
            ToDoItem("17", "Task25\nNeed to complete task\nIt's very important\nAnd have to be done", "High", "11.06.2023", false, "10.06.2023", ""),
            ToDoItem("18", "Task26", "High", "11.06.2023", false, "10.06.2023", "10.06.2023")
        )
        doneNum=3
        return items
    }

    fun getItemList():List<ToDoItem>{
        return toDoItemList.reversed()
    }

    fun addItem(text:String,
                importance:String,
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
                       importance:String,
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