package com.example.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tasks.service.listener.APIListener
import com.example.tasks.service.listener.ValidetionListener
import com.example.tasks.service.model.PriorityModel
import com.example.tasks.service.model.TaskModel
import com.example.tasks.service.repository.PriorityRepository
import com.example.tasks.service.repository.TaskRepository

class TaskFormViewModel(application: Application) : AndroidViewModel(application) {

    private val mPriorityrepository = PriorityRepository(application)
    private val mTaskRepository = TaskRepository(application)

    private val mPriorityList = MutableLiveData<List<PriorityModel>>()
    var priorityList: LiveData<List<PriorityModel>> = mPriorityList

    private val mValidation = MutableLiveData<ValidetionListener>()
    var validation: LiveData<ValidetionListener> = mValidation

    private val mTask = MutableLiveData<TaskModel>()
    var task: LiveData<TaskModel> = mTask

    fun listPriorities() {
        mPriorityList.value = mPriorityrepository.list()
    }

    fun save(task: TaskModel) {

        if(task.id == 0){
            mTaskRepository.create(task, object : APIListener<Boolean> {
                override fun onSuccess(model: Boolean) {
                    mValidation.value = ValidetionListener()
                }

                override fun onFailure(str: String) {
                    mValidation.value = ValidetionListener(str)
                }

            })
        } else {
            mTaskRepository.update(task, object : APIListener<Boolean> {
                override fun onSuccess(model: Boolean) {
                    mValidation.value = ValidetionListener()
                }

                override fun onFailure(str: String) {
                    mValidation.value = ValidetionListener(str)
                }
            })
        }
    }

    fun load(id: Int) {
        mTaskRepository.load(id, object : APIListener<TaskModel>{
            override fun onSuccess(model: TaskModel) {
                mTask.value = model
            }

            override fun onFailure(str: String) {

            }

        })
    }

}