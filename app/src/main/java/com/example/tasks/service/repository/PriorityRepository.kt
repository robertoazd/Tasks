package com.example.tasks.service.repository

import android.content.Context
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.listener.APIListener
import com.example.tasks.service.model.PriorityModel
import com.example.tasks.service.repository.local.TaskDatabase
import com.example.tasks.service.repository.remote.BaseRepository
import com.example.tasks.service.repository.remote.PriorityService
import com.example.tasks.service.repository.remote.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PriorityRepository(val context: Context): BaseRepository(context){

    private val mRemote = RetrofitClient.createService(PriorityService::class.java)
    private val mPriorityDatabase = TaskDatabase.getDatabase(context).priorityDAO()

    fun all(param: APIListener<List<PriorityModel>>) {

        if (!isConnectionAvailable(context)) {
            return
        }

        val call: Call<List<PriorityModel>> = mRemote.list()
        call.enqueue(object  : Callback<List<PriorityModel>>{
            override fun onResponse(
                call: Call<List<PriorityModel>>,
                response: Response<List<PriorityModel>>
            ) {
                if (response.code() == TaskConstants.HTTP.SUCCESS) {
                    mPriorityDatabase.clear()
                    response.body()?.let { mPriorityDatabase.save(it) }
                }
            }

            override fun onFailure(call: Call<List<PriorityModel>>, t: Throwable) {

            }

        })
    }

    fun list() = mPriorityDatabase.list()

    fun getDrescription(id: Int) = mPriorityDatabase.getDescription(id)
    fun save(result: List<PriorityModel>) {
    }
}