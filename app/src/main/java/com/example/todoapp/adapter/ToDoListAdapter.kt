package com.example.todoapp.utils

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.model.ToDoItem
import com.example.todoapp.databinding.ItemTaskBinding
import com.example.todoapp.model.Importance

interface TaskActionListener{
    fun onTaskDetails(itemId:String)
    fun onTaskChangeComplete(itemId:String)
    fun onCompleteNumberChanged()
    fun onTaskDelete(itemId: String)
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
                && oldItem.date_deadline.equals(newItem.date_deadline)
                && oldItem.is_complete.equals(newItem.is_complete)
                && oldItem.date_changing.equals(newItem.date_changing)
                && oldItem.date_creation.equals(newItem.date_creation)

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

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: ToDoListViewHolder, position: Int) {
        holder.itemView.tag=items[position]
        val button:CompoundButton=holder.itemView.findViewById(R.id.checkbox)
        button.tag=items[position]

        holder.binding.checkbox.isChecked=items[position].is_complete
        if(items[position].is_complete) {
            holder.binding.text.paintFlags =
                holder.binding.text.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.binding.text.setTextColor(R.color.grey)
        }
        else{
            holder.binding.text.paintFlags =
                holder.binding.text.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.binding.text.setTextColor(R.color.black)
        }
        when (items[position].importance) {
            Importance.LOW -> {
                holder.binding.imageView.setImageResource(R.drawable.icon_slow)
                holder.binding.imageView.visibility=View.VISIBLE

            }
            Importance.HIGH -> {
                holder.binding.imageView.setImageResource(R.drawable.icon_run)
                holder.binding.imageView.visibility=View.VISIBLE
            }
            else -> holder.binding.imageView.visibility=View.GONE
        }

        if(items[position].date_deadline!="") {
            holder.binding.deadline.visibility=View.VISIBLE
            holder.binding.deadline.text=items[position].date_deadline
        }
        else holder.binding.deadline.visibility=View.INVISIBLE

        holder.binding.text.text= items[position].text

        holder.itemView.setOnClickListener(this)

        holder.binding.checkbox.setOnClickListener {
            val itemId:String=(it.tag as ToDoItem).id
            taskActionListener.onTaskChangeComplete(itemId)
            taskActionListener.onCompleteNumberChanged()
        }

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

    override fun onClick(v: View) {
        val itemId:String=(v.tag as ToDoItem).id
        taskActionListener.onTaskDetails(itemId)
    }
}
