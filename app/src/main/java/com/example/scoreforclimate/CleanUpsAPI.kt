package com.example.scoreforclimate


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CleanUpsAPI {
    @GET("cities.json")
    fun getAllCleanUps(): Call<List<CleanUps>>

    @GET("{code}.json")
    fun getCleanUpInfo(@Path("code")code : String): Call<CleanUpInfo>
}