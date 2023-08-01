package com.example.tasks.view

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.tasks.R
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.model.TaskModel
import com.example.tasks.viewmodel.RegisterViewModel
import com.example.tasks.viewmodel.TaskFormViewModel
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.button_save
import kotlinx.android.synthetic.main.activity_task_form.*
import java.text.SimpleDateFormat
import java.util.*

class TaskFormActivity : AppCompatActivity(), View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private lateinit var mViewModel: TaskFormViewModel
    private val mDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    private val mListPriorityId: MutableList<Int> = arrayListOf()
    private var mTaskId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_form)

        mViewModel = ViewModelProvider(this).get(TaskFormViewModel::class.java)

        // Inicializa eventos
        listeners()
        observe()

        mViewModel.listPriorities()

        loadDataFromActivity()
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.button_save) {
            handleSave()

        } else if (id == R.id.button_date) {
            showDatePicker()
        }
    }

    private fun loadDataFromActivity() {
        val bunble = intent.extras
        if (bunble != null){
            mTaskId = bunble.getInt(TaskConstants.BUNDLE.TASKID)
            mViewModel.load(mTaskId)
            button_save.text = getString(R.string.update_task)
        }
    }

    private fun handleSave() {
        val task = TaskModel().apply {
            this.id = mTaskId
            this.description = edit_description.text.toString()
            this.complete = check_complete.isChecked
            this.dueDate = button_date.text.toString()
            this.priorityId = mListPriorityId[spinner_priority.selectedItemPosition]
        }

        mViewModel.save(task)
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(this, this, year, month, day).show()
    }


    private fun observe() {
        mViewModel.priorityList.observe(this, androidx.lifecycle.Observer {
            val list: MutableList<String> = arrayListOf()
            for (item in it) {
                list.add(item.description)
                mListPriorityId.add(item.id)
            }

            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list)
            spinner_priority.adapter = adapter
        })

        //Load
        mViewModel.task.observe(this, androidx.lifecycle.Observer {
            edit_description.setText(it.description)
            check_complete.isChecked = it.complete
            spinner_priority.setSelection(getIndex(it.priorityId))

            //Formato yyyy-MM-dd
            val date = SimpleDateFormat("yyyy-MM-dd").parse(it.dueDate)
            button_date.text = (mDateFormat.format(date))
        })

        mViewModel.validation.observe(this, androidx.lifecycle.Observer {
            if (it.success()) {
                if (mTaskId == 0){
                    toast(getString(R.string.task_created))
                } else {
                    toast(getString(R.string.task_updated))
                }
                finish()
            } else {
                toast(it.failure())
            }
        })
    }

    private fun toast(str: String){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
    }

    private fun getIndex(priorityId: Int): Int {
        var index = 0
        for (i in 0 until mListPriorityId.count()) {
            if (mListPriorityId[i] == priorityId) {
                index = i
                break
            }
        }
        return index
    }

    private fun listeners() {
        button_save.setOnClickListener(this)
        button_date.setOnClickListener(this)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        //Atribuir o calendario
        val calender = Calendar.getInstance()
        calender.set(year, month, dayOfMonth)

        //Formatar a data
        val str = mDateFormat.format(calender.time)
        button_date.text = str

    }

}
