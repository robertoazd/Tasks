package com.example.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tasks.service.model.HeaderModel
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.helper.FingerPrintHelper
import com.example.tasks.service.listener.APIListener
import com.example.tasks.service.listener.ValidetionListener
import com.example.tasks.service.model.PriorityModel
import com.example.tasks.service.repository.PersonRepository
import com.example.tasks.service.repository.PriorityRepository
import com.example.tasks.service.repository.local.SecurityPreferences
import com.example.tasks.service.repository.remote.RetrofitClient

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val mPersonRepository = PersonRepository(application)
    private val mPriorityRepository = PriorityRepository(application)
    private val mSharedPreferences = SecurityPreferences(application)

    private val mLogin = MutableLiveData<ValidetionListener>()
    var login: LiveData<ValidetionListener> = mLogin

    private val mFingerPrint = MutableLiveData<Boolean>()
    var fingerPrint: LiveData<Boolean> = mFingerPrint

    /**
     * Faz login usando API
     */
    fun doLogin(email: String, password: String) {
        mPersonRepository.login(email, password, object : APIListener<HeaderModel>{
            override fun onSuccess(model: HeaderModel) {

                mSharedPreferences.store(TaskConstants.SHARED.TOKEN_KEY, model.token)
                mSharedPreferences.store(TaskConstants.SHARED.PERSON_KEY, model.personKey)
                mSharedPreferences.store(TaskConstants.SHARED.PERSON_NAME, model.name)

                RetrofitClient.addHeader(model.token, model.personKey)

                mLogin.value = ValidetionListener()
            }

            override fun onFailure(str: String) {
                mLogin.value = ValidetionListener(str)
            }

        })
    }

    fun isAuthenticationAvaliable() {

        val token = mSharedPreferences.get(TaskConstants.SHARED.TOKEN_KEY)
        val person = mSharedPreferences.get(TaskConstants.SHARED.PERSON_KEY)

        // Se token e person key forem diferentes de vazio, usuario esta logado
        val everLogged = (token != "" && person != "")

        // Atualiza valores de Header para requisições
        RetrofitClient.addHeader(token, person)

        // Se usuario n estiver logado, aplicaçao vai atualizar os dados
        if (!everLogged) {
            mPriorityRepository.all(object : APIListener<List<PriorityModel>> {
                override fun onSuccess(result: List<PriorityModel>) {
                    mPriorityRepository.save(result)
                }

                override fun onFailure(message: String) {
                }
            })
        }

        if (FingerPrintHelper.isAuthenticationAvailable(getApplication())) {
            mFingerPrint.value = everLogged
        }
    }

    /**
     * Verifica se usuário está logado
     */
    /*fun verifyLoggedUser() {
    }*/
}