package com.example.todoapp.utils

import android.annotation.SuppressLint
import android.graphics.Paint
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.databinding.ItemTaskBinding
import com.example.todoapp.model.ToDoItem
import java.util.UUID


interface TaskActionListener{
    fun onTaskDetails(itemId:UUID)
    fun onTaskChangeComplete(itemId:UUID)
    fun onCompleteNumberChanged()
    fun onTaskDelete(itemId: UUID)
    fun openActionMenu()
}

class ToDoListDiffUtilCallback(
    private val oldList:List<ToDoItem>,

    private val newList:List<ToDoItem>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem=oldList[oldItemPosition]
        val newItem=newList[newItemPosition]
        return oldItem.id.equals(newItem.id)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem=oldList[oldItemPosition]
        val newItem=newList[newItemPosition]
        return oldItem.id.equals(newItem.id)
                && oldItem.text.equals(newItem.text)
                && oldItem.importance.equals(newItem.importance)
                && oldItem.deadline?.equals(newItem.deadline) ?: true
                && oldItem.done.equals(newItem.done)
                && oldItem.changed_at.equals(newItem.changed_at)
                && oldItem.created_at.equals(newItem.created_at)

    }

}

class ToDoListAdapter(
    private val taskActionListener: TaskActionListener,
) : RecyclerView.Adapter<ToDoListAdapter.ToDoListViewHolder>(),
    View.OnClickListener{

    class ToDoListViewHolder(val binding: ItemTaskBinding)
        :RecyclerView.ViewHolder(binding.root)

    var items= emptyList<ToDoItem>()
    set(value) {
        val diffCallback=ToDoListDiffUtilCallback(field, value)
        val diffResult=DiffUtil.calculateDiff(diffCallback)
        field=value
        diffResult.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoListViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val binding=ItemTaskBinding.inflate(inflater,parent,false)
        return ToDoListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }


    private fun setUpItemCheckbox(holder: ToDoListViewHolder, position: Int, completeColor:Int, incompleteColor:Int){
        holder.binding.checkbox.isChecked=items[position].done
        if(items[position].done) {
            holder.binding.text.paintFlags =
                holder.binding.text.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.binding.text.setTextColor(completeColor)
        }
        else{
            holder.binding.text.paintFlags =
                holder.binding.text.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.binding.text.setTextColor(incompleteColor)
        }

        holder.binding.checkbox.setOnClickListener {
            val itemId:UUID=(it.tag as ToDoItem).id
            taskActionListener.onTaskChangeComplete(itemId)
            taskActionListener.onCompleteNumberChanged()
        }

    }
    private fun setUoItemImportance(holder: ToDoListViewHolder, position: Int){
        when (items[position].importance) {
            ToDoItem.Importance.low -> {
                holder.binding.imageView.setImageResource(R.drawable.icon_slow)
                holder.binding.imageView.visibility=View.VISIBLE

            }
            ToDoItem.Importance.important -> {
                holder.binding.imageView.setImageResource(R.drawable.icon_run)
                holder.binding.imageView.visibility=View.VISIBLE
            }
            else -> holder.binding.imageView.visibility=View.GONE
        }
    }
    private fun setUpItemDeadline(holder: ToDoListViewHolder, position: Int){
        if(items[position].deadline.toString()!="0") {
            holder.binding.deadline.visibility=View.VISIBLE
            holder.binding.deadline.text=
                DateUtils.dateToString(DateUtils.longToDate(items[position].deadline!!))
        }
        else{
            holder.binding.deadline.visibility=View.GONE
        }
    }
    private fun setUpItemText(holder: ToDoListViewHolder, position: Int){
        holder.binding.text.text= items[position].text
    }
    private fun setUpItemClickListener(holder: ToDoListViewHolder){
        holder.itemView.setOnClickListener(this)
        holder.itemView.setOnLongClickListener(View.OnLongClickListener {
            val todoItem=it.tag as ToDoItem
            val popupMenu = PopupMenu(holder.itemView.context, holder.itemView)
            popupMenu.inflate(R.menu.action_popup_menu)

            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.actionOpen -> {
                        taskActionListener.onTaskDetails(todoItem.id)
                    }
                }
                false
            }
            popupMenu.show()
            true
        })
    }


    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: ToDoListViewHolder, position: Int) {
        holder.itemView.tag=items[position]
        val button:CompoundButton=holder.itemView.findViewById(R.id.checkbox)
        button.tag=items[position]

        Log.println(Log.INFO, "CHECK ADAPTER", "${items[position]}")

        setUpItemCheckbox(holder,position, R.color.grey, R.color.black)
        setUoItemImportance(holder,position)
        setUpItemDeadline(holder, position)
        setUpItemText(holder, position)
        setUpItemClickListener(holder)
    }

    override fun onClick(v: View) {
        val itemId:UUID=(v.tag as ToDoItem).id
        taskActionListener.onTaskDetails(itemId)
    }
}
