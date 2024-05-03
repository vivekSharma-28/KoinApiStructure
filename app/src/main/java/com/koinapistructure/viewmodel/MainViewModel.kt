package com.koinapistructure.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theweekin.modelClass.homepage.news_model
import com.koinapistructure.Apirequest.Request
import com.koinapistructure.repository.ApiRepository
import com.koinapistructure.response.newsfeed.newsfeed
import com.koinapistructure.utils.DataStatus
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ApiRepository) : ViewModel() {

    val mutableLiveData = MutableLiveData<DataStatus<newsfeed>>()
    val data: LiveData<DataStatus<newsfeed>>
        get() = mutableLiveData


    /*fun getData() = viewModelScope.launch {
        repository.getData().collect {
            mutableLiveData.value = it
        }
    }*/

    fun product(request: Request)=viewModelScope.launch {
        repository.getProduct(request).collect{
            mutableLiveData.value=it
        }
    }


}