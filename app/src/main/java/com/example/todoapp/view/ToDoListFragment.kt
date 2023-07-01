package com.example.todoapp.view

import android.annotation.SuppressLint
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentToDoListBinding
import com.example.todoapp.model.ToDoItem
import com.example.todoapp.repository.TaskListener
import com.example.todoapp.utils.*
import com.example.todoapp.viewmodel.ToDoListViewModel
import com.example.todoapp.viewmodel.factory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.wait
import java.util.UUID


class ToDoListFragment : Fragment(){

    private lateinit var binding:FragmentToDoListBinding
    lateinit var adapter: ToDoListAdapter
    private val toDoListViewModel: ToDoListViewModel by viewModels {factory()}
    private var isVisible=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toDoListViewModel.updateList()
    }

    private fun setTaskById(id: UUID) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                toDoListViewModel.getTaskById(id)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToDoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUi()
    }

    override fun onResume() {
        Log.println(Log.DEBUG, "HUI", "HUI")
        super.onResume()
        setUpUi()
    }


    private fun setUpRecyclerView(){
        toDoListViewModel.updateList()
        adapter= ToDoListAdapter(object : TaskActionListener {
            override fun onTaskDetails(itemId: UUID) {
                openItemEditFragment(itemId.toString())
            }

            override fun onTaskChangeComplete(itemId: UUID) {
//                setTaskById(itemId)
//                val item=toDoListViewModel._currentTask
//                item.done=!item.done
                toDoListViewModel.setTaskComplete(itemId)
                toDoListViewModel.updateList()
                setUpCompleteNum()
            }

            override fun onCompleteNumberChanged() {
            }

            override fun onTaskDelete(itemId: UUID) {
                toDoListViewModel.deleteTaskByIdDb(itemId)
            }

            override fun openActionMenu() {

            }
        })
        viewLifecycleOwner.lifecycleScope.launch {
            toDoListViewModel.getList().collect {
                if(isVisible) adapter.items=it
                else adapter.items=it.filter { !it.done }
            }
        }
        binding.recyclerView.adapter=adapter
        val layoutManager= LinearLayoutManager(context)
        binding.recyclerView.layoutManager=layoutManager

        //setSwipeAction()
    }


    private fun setUpFloatingButton(){
        binding.addButton.setOnClickListener {
            openItemEditFragment("")
        }
    }

    private fun openItemEditFragment(itemId:String){
        val action = ToDoListFragmentDirections.actionToDoListFragmentToToDoItemEditFragment(itemId)
        Navigation.findNavController(binding.root).navigate(action)
    }

    private fun setUpUi(){
        setUpFloatingButton()
        setUpRecyclerView()
        setUpVisibility()
        setUpCompleteNum()
        setUpRefreshLayout()
        setSwipeAction()
    }

    private fun setUpRefreshLayout(){
        binding.swipeToRefreshLayout.setOnRefreshListener {
            viewLifecycleOwner.lifecycleScope.launch {
                toDoListViewModel.updateList()
                toDoListViewModel.getList().collect() {
                    if(isVisible) adapter.items=it
                    else adapter.items=it.filter { !it.done }
                }
                setUpCompleteNum()

            }
            binding.swipeToRefreshLayout.isRefreshing=false
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setUpVisibility(){

        if(isVisible){
            binding.eyeButton.setImageResource(R.drawable.icon_watch)
        }
        else{
            binding.eyeButton.setImageResource(R.drawable.ic_not_watch)
        }

        binding.eyeButton.setOnClickListener {
            if(isVisible){
                binding.eyeButton.setImageResource(R.drawable.ic_not_watch)
            }
            else{
                binding.eyeButton.setImageResource(R.drawable.icon_watch)
            }
            isVisible=!isVisible
            setUpRecyclerView()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpCompleteNum(){
        binding.completeTextView.text="${getString(R.string.done_title_text)} ${toDoListViewModel.getCompleteTaskNum()} / ${toDoListViewModel.getTaskNum()}"
    }

    private fun setSwipeAction(){
        val itemTouchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val itemId = (viewHolder.itemView.tag as ToDoItem).id
                    setTaskById(itemId)
                    when (direction) {
                        ItemTouchHelper.LEFT -> {
                            val item = toDoListViewModel._currentTask
                            val position = toDoListViewModel.getPositionById(itemId)
                            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                toDoListViewModel.deleteTaskById(itemId)
                                toDoListViewModel.updateList()
                                //showRestoreItemSnackbar(item, position)
                            }
                            setUpCompleteNum()
                            setUpRecyclerView()
                        }
                        ItemTouchHelper.RIGHT -> {
                            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                toDoListViewModel.setTaskComplete(itemId)
                                toDoListViewModel.updateList()
                            }
                            setUpCompleteNum()
                            setUpRecyclerView()
                        }
                    }
                }

                override fun getSwipeThreshold(viewHolder: ViewHolder) = 0.5f

                private fun showRestoreItemSnackbar(item: ToDoItem, position: Int) {
                    Snackbar.make(binding.recyclerView, "Task deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo") {
                            //toDoListViewModel.restoreItem(item, position)
                        }.show()
                }

                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val itemView: View = viewHolder.itemView
                    var p = Paint().also { it.color = resources.getColor(R.color.red) }
                    if (dX < 0) {
                        p = Paint().also { it.color = resources.getColor(R.color.red) }
                        c.drawRect(
                            itemView.right.toFloat() + dX,
                            itemView.top.toFloat(),
                            itemView.right.toFloat(),
                            itemView.bottom.toFloat(),
                            p
                        )
                        val icon: Bitmap =
                            requireContext().getDrawable(R.drawable.icon_delete_white)!!.toBitmap()
                        val iconMarginRight = (dX * -0.2f).coerceAtMost(70f).coerceAtLeast(0f)
                        c.drawBitmap(
                            icon,
                            itemView.right.toFloat() - iconMarginRight - icon.width,
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height) / 2,
                            p
                        )

                    }
                    if (dX > 0) {
                        p = Paint().also { it.color = resources.getColor(R.color.green) }
                        c.drawRect(
                            itemView.left.toFloat() + dX,
                            itemView.top.toFloat(),
                            itemView.left.toFloat(),
                            itemView.bottom.toFloat(),
                            p
                        )
                        val icon: Bitmap =
                            requireContext().getDrawable(R.drawable.icon_save_white)!!.toBitmap()

                        val iconMarginLeft = (dX * 0.2f).coerceAtMost(70f).coerceAtLeast(0f)
                        c.drawBitmap(
                            icon,
                            itemView.left.toFloat() + iconMarginLeft,
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height) / 2,
                            p
                        )
                    }

                    // Draw background


                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            })
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

    }

}