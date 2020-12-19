package com.example.retrofitaplication.repository

import ServerDog
import api.RetrofitInstance
import com.example.retrofitaplication.util.Constants.Companion.BREED_IDS
import retrofit2.Response

class Repository {
    suspend fun getDogs(breeds_ids: Int): Response<List<ServerDog>>
    {
        return RetrofitInstance.api.getDogs(breeds_ids)
    }
}