package com.example.todoapp.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentToDoItemEditBinding
import com.example.todoapp.model.ToDoItem
import com.example.todoapp.utils.DateUtils
import com.example.todoapp.utils.StringUtils
import com.example.todoapp.viewmodel.ToDoItemEditViewModel
import com.example.todoapp.viewmodel.factory
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialDatePicker.INPUT_MODE_CALENDAR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.internal.wait
import java.text.SimpleDateFormat
import java.util.*


class ToDoItemEditFragment : Fragment() {

    lateinit var binding:FragmentToDoItemEditBinding
    private val toDoItemEditViewModel: ToDoItemEditViewModel by viewModels {factory()}
    private val args:ToDoItemEditFragmentArgs by navArgs()
    private var itemId:String=""
    //private var toDoItem=toDoItemEditViewModel.createDefaultTask()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        itemId=args.itemId
        if(itemId!="") setTaskById(UUID.fromString(itemId))
        //itemId="1"
    }

    private fun setTaskById(id: UUID) {
        Log.println(Log.INFO, "CHECK SET", "$id")
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                toDoItemEditViewModel.getTaskById(id)
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentToDoItemEditBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUi()
    }

    override fun onResume() {
        super.onResume()
        setUpUi()
    }

    private fun setUpUi(){
        setUpView()
        setUpDeleteButton()
        setUpSaveButton()
        setUpBackButton()
        setUpImportanceMenu()
        setUpDeadlineSwitch()
        setUpDatePicker()
    }

    private fun setUpView(){
        if(itemId!=""){

            val toDoItem=toDoItemEditViewModel._currentTask
            Log.println(Log.INFO, "CHECK", "$toDoItem")

            binding.taskEditText.text= StringUtils.Editable(toDoItem!!.text)

            setImportance(toDoItem.importance.toString())

            if(toDoItem.deadline.toString()!="0"){
                binding.deadlineSwitch.isChecked=true
                binding.dateTextView.visibility=View.VISIBLE
                binding.dateTextView.isClickable=true
                binding.dateTextView.text=DateUtils.dateToString(DateUtils.longToDate(toDoItem.deadline!!))
            }
        }
        else{
            binding.deadlineSwitch.isChecked=false
            binding.dateTextView.visibility=View.INVISIBLE
            binding.dateTextView.isClickable=false
        }
    }

    private fun setUpDeleteButton(){

        if(itemId==""){
            binding.deleteIcon.setImageResource(R.drawable.icon_delete_grey)
            binding.deleteTextView.setTextColor(resources.getColor(R.color.grey))
            binding.deleteIcon.setOnClickListener {
                Toast.makeText(requireContext(), getString(R.string.non_existed_task_message),Toast.LENGTH_SHORT).show()
            }
            binding.deleteTextView.setOnClickListener {
                Toast.makeText(requireContext(), getString(R.string.non_existed_task_message),Toast.LENGTH_SHORT).show()
            }
        }
        else{
            binding.deleteIcon.setImageResource(R.drawable.icon_delete)
            binding.deleteTextView.setTextColor(resources.getColor(R.color.black))
            binding.deleteIcon.setOnClickListener {
                deleteItem()
                Toast.makeText(requireContext(), getString(R.string.delete_message),Toast.LENGTH_SHORT).show()
                openListFragment()
            }
            binding.deleteTextView.setOnClickListener {
                deleteItem()
                Toast.makeText(requireContext(), getString(R.string.delete_message),Toast.LENGTH_SHORT).show()
                openListFragment()
            }
        }
    }

    private fun deleteItem(){
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            toDoItemEditViewModel.deleteTaskById(UUID.fromString(itemId))
        }
    }

    private fun setUpSaveButton(){
        binding.saveButton.setOnClickListener {
            if(binding.taskEditText.text.toString()==""){
                Toast.makeText(requireContext(), getString(R.string.empty_task_message), Toast.LENGTH_SHORT).show()
            }
            else{
                saveItem()
                if(itemId=="")
                    Toast.makeText(requireContext(), getString(R.string.save_message), Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(requireContext(), getString(R.string.edit_message), Toast.LENGTH_SHORT).show()
                openListFragment()
            }
        }
    }
    private fun setImportance(importance:String) {
        binding.importanceEditTextView.text = importance
        when (importance) {
            ToDoItem.Importance.low.toString() -> {
                binding.importanceEditTextView.setTextColor(resources.getColor(R.color.green))
                val params: MarginLayoutParams = binding.importanceEditTextView.layoutParams
                        as MarginLayoutParams
                params.marginStart = 5
                binding.importanceEditTextView.layoutParams = params

                binding.importanceImageView.setImageResource(R.drawable.icon_slow)
                binding.importanceImageView.visibility = View.VISIBLE
            }

            ToDoItem.Importance.important.toString() -> {
                binding.importanceEditTextView.setTextColor(resources.getColor(R.color.red))
                val params: MarginLayoutParams = binding.importanceEditTextView.layoutParams
                        as MarginLayoutParams
                params.marginStart = 5
                binding.importanceEditTextView.layoutParams = params

                binding.importanceImageView.setImageResource(R.drawable.icon_run)
                binding.importanceImageView.visibility = View.VISIBLE
            }

            ToDoItem.Importance.basic.toString() -> {
                binding.importanceEditTextView.setTextColor(resources.getColor(R.color.black))
                binding.importanceImageView.visibility = View.GONE
                val params: MarginLayoutParams = binding.importanceEditTextView.layoutParams
                        as MarginLayoutParams
                params.marginStart = 35
                binding.importanceEditTextView.layoutParams = params
            }
        }
    }
//
//    }
//
    private fun setUpImportanceMenu() {
        val popupMenu = PopupMenu(requireContext(), binding.importanceEditTextView)
        popupMenu.inflate(R.menu.importance_popup_menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.lowImportance -> {
                    setImportance(it.title.toString())
                    Toast.makeText(requireContext(), "${it.title} ${getString(R.string.importance_message)}", Toast.LENGTH_SHORT).show()
                }
                R.id.highImportance -> {
                    setImportance(it.title.toString())
                    Toast.makeText(requireContext(), "${it.title} ${getString(R.string.importance_message)}", Toast.LENGTH_SHORT).show()
                }
                R.id.noImportance -> {
                    setImportance(it.title.toString())
                    Toast.makeText(requireContext(), "${it.title} ${getString(R.string.importance_message)}", Toast.LENGTH_SHORT).show()
                }
            }
            false
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true)
        }

        binding.importanceEditTextView.setOnClickListener {
            popupMenu.show()
        }
    }

    private fun setUpDeadlineSwitch(){
        binding.deadlineSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.dateTextView.isClickable=true
                binding.dateTextView.visibility=View.VISIBLE
                binding.dateTextView.text= DateUtils.getCurrentDateString()
            }
            else{
                binding.dateTextView.isClickable=false
                binding.dateTextView.visibility=View.INVISIBLE
            }
        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun setUpDatePicker(){

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setPositiveButtonText(getString(R.string.ok_button_text))
            .setNegativeButtonText(getString(R.string.cancel_button_text))
            .setTitleText(getString(R.string.select_date_title))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setInputMode(INPUT_MODE_CALENDAR)
            .build()

        datePicker.addOnPositiveButtonClickListener {
            val dateFormatter = SimpleDateFormat("MMMM d, y")
            val date = dateFormatter.format(Date(it))
            binding.dateTextView.text=date
            Toast.makeText(requireContext(), getString(R.string.deadline_message), Toast.LENGTH_SHORT).show()
        }

        binding.dateTextView.setOnClickListener {
            datePicker.show(childFragmentManager, "date")
        }
    }

    private fun setUpBackButton(){
        binding.cancelButton.setOnClickListener {
            openListFragment()
        }
    }

    private fun openListFragment(){
        findNavController().popBackStack()
    }

    private fun saveItem(){
        val text=binding.taskEditText.text.toString()

        val importance = when (binding.importanceEditTextView.text) {
            ToDoItem.Importance.basic.toString() -> {
                ToDoItem.Importance.basic
            }
            ToDoItem.Importance.low.toString() -> {
                ToDoItem.Importance.low
            }
            else -> ToDoItem.Importance.important
        }

        val deadline = if(binding.deadlineSwitch.isChecked)
            DateUtils.dateToLong(DateUtils.stringToDate(binding.dateTextView.text.toString()))
        else 0

        if(itemId==""){
            val created_at= Date().time
            val changed_at=created_at
            val done=false
            val toDoItem=ToDoItem(UUID.randomUUID(), text, importance, deadline, done, "#000000", created_at, changed_at)
            viewLifecycleOwner.lifecycleScope.launch {
                toDoItemEditViewModel.addTask(toDoItem)
            }
        }
        else{
            var item:ToDoItem= toDoItemEditViewModel._currentTask

            val changed_at=Date().time
            item.text=text
            item.importance=importance
            item.deadline=deadline
            item.changed_at=changed_at

            viewLifecycleOwner.lifecycleScope.launch {
                toDoItemEditViewModel.updateTask(item)
            }
        }
    }
//


}