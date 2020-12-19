package api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.retrofitaplication.util.Constants.Companion.BASE_URL

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val api: IClient by lazy {
        retrofit.create(IClient::class.java)
    }

}