package api

import ServerDog
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// Address control
interface IClient {
    // "images/search" - endpoint
    @GET("images/search")

    // Query with parameter
    // Using suspend fun for Kotlin Coroutines
    suspend fun getDogs(@Query("breed_ids") breeds_ids: Int): Response<List<ServerDog>>

}