package com.example.retrofitaplication.view_model

import ServerDog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.retrofitaplication.repository.Repository
import androidx.lifecycle.viewModelScope
import com.example.retrofitaplication.util.Constants.Companion.BREED_IDS
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(private val repository: Repository): ViewModel()
{
    val myResponse : MutableLiveData<Response<List<ServerDog>>> = MutableLiveData()
    fun getDogs(breeds_ids: Int)
    {
        viewModelScope.launch{
        val response = repository.getDogs(breeds_ids)
        myResponse.value = response

        }
    }
}