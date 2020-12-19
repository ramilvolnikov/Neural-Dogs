package api

import ServerDog
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IClient {
    @GET("images/search")
    suspend fun getDogs(@Query("breed_ids") breeds_ids: Int): Response<List<ServerDog>>

}