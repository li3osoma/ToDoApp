package com.example.todoapp.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentToDoItemEditBinding
import com.example.todoapp.utils.DateUtils
import com.example.todoapp.utils.StringUtils
import com.example.todoapp.viewmodel.ToDoItemEditViewModel
import com.example.todoapp.viewmodel.factory
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialDatePicker.INPUT_MODE_CALENDAR
import java.text.SimpleDateFormat
import java.util.*


class ToDoItemEditFragment : Fragment() {

    lateinit var binding:FragmentToDoItemEditBinding
    private val toDoItemEditViewModel:ToDoItemEditViewModel by viewModels {factory()}
    private val args:ToDoItemEditFragmentArgs by navArgs()
    private var itemId=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        itemId=args.itemId
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
            val toDoItemLD=toDoItemEditViewModel.getItemById(itemId)
            val toDoItem=toDoItemLD.value!!
            binding.taskEditText.text= StringUtils.Editable(toDoItem.text)

            setImportance(toDoItem.importance)

            if(toDoItem.date_deadline!=""){
                binding.deadlineSwitch.isChecked=true
                binding.dateTextView.visibility=View.VISIBLE
                binding.dateTextView.isClickable=true
                binding.dateTextView.text=toDoItem.date_deadline
            }
        }
        else{
            binding.deadlineSwitch.isChecked=false
            binding.dateTextView.visibility=View.GONE
            binding.dateTextView.isClickable=false
        }
    }

    private fun setUpDeleteButton(){

        if(itemId==""){
            binding.deleteIcon.isClickable=false
            binding.deleteTextView.isClickable=false
            binding.deleteIcon.setImageResource(R.drawable.icon_delete_grey)
            binding.deleteTextView.setTextColor(resources.getColor(R.color.grey))
        }
        else{
            binding.deleteIcon.isClickable=true
            binding.deleteTextView.isClickable=true
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

    private fun setImportance(importance:String){
        binding.importanceEditTextView.text=importance
        when (importance){
            "Low" -> {
                binding.importanceEditTextView.setTextColor(resources.getColor(R.color.green))
                val params:MarginLayoutParams=binding.importanceEditTextView.layoutParams
                        as MarginLayoutParams
                params.marginStart=5
                binding.importanceEditTextView.layoutParams=params

                binding.importanceImageView.setImageResource(R.drawable.icon_slow)
                binding.importanceImageView.visibility=View.VISIBLE
            }
            "High" -> {
                binding.importanceEditTextView.setTextColor(resources.getColor(R.color.red))
                val params:MarginLayoutParams=binding.importanceEditTextView.layoutParams
                        as MarginLayoutParams
                params.marginStart=5
                binding.importanceEditTextView.layoutParams=params

                binding.importanceImageView.setImageResource(R.drawable.icon_run)
                binding.importanceImageView.visibility=View.VISIBLE
            }
            "No" -> {
                binding.importanceEditTextView.setTextColor(resources.getColor(R.color.black))
                binding.importanceImageView.visibility=View.GONE
                val params:MarginLayoutParams=binding.importanceEditTextView.layoutParams
                        as MarginLayoutParams
                params.marginStart=70
                binding.importanceEditTextView.layoutParams=params
            }
        }

    }

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
        val importance=binding.importanceEditTextView.text.toString()

        val date_deadline = if(binding.deadlineSwitch.isChecked)
            binding.dateTextView.text.toString()
        else ""

        if(itemId==""){
            val date_creation= DateUtils.getCurrentDateString()
            val date_changing=date_creation
            val is_complete=false
            toDoItemEditViewModel.addItem(text, importance, date_deadline, is_complete, date_creation, date_changing)
        }
        else{
            val item = toDoItemEditViewModel.getItemById(itemId).value!!
            val date_creation=item.date_creation
            val is_complete=item.is_complete
            val date_changing= DateUtils.getCurrentDateString()
            toDoItemEditViewModel.updateItemById(itemId, text, importance, date_deadline,is_complete, date_creation, date_changing)
        }
    }

    private fun deleteItem(){
        toDoItemEditViewModel.deleteItemById(itemId)
    }

}