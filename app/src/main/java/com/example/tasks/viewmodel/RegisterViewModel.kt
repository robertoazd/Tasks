package com.example.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tasks.service.model.HeaderModel
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.listener.APIListener
import com.example.tasks.service.listener.ValidetionListener
import com.example.tasks.service.repository.PersonRepository
import com.example.tasks.service.repository.local.SecurityPreferences

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val mPersonRepository = PersonRepository(application)
    private val mSharedPreferences = SecurityPreferences(application)

    private val mCreate = MutableLiveData<ValidetionListener>()
    var create: LiveData<ValidetionListener> = mCreate

    fun create(name: String, email: String, password: String) {
        mPersonRepository.create(name, email, password, object  : APIListener<HeaderModel>{
            override fun onSuccess(model: HeaderModel) {
                mSharedPreferences.store(TaskConstants.SHARED.TOKEN_KEY, model.token)
                mSharedPreferences.store(TaskConstants.SHARED.PERSON_KEY, model.personKey)
                mSharedPreferences.store(TaskConstants.SHARED.PERSON_NAME, model.name)

                mCreate.value = ValidetionListener()
            }

            override fun onFailure(str: String) {
                mCreate.value = ValidetionListener(str)
            }

        })
    }
}