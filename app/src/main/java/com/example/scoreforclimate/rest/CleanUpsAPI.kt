package com.example.scoreforclimate.rest


import com.example.scoreforclimate.rest.CleanUpInfo
import com.example.scoreforclimate.rest.CleanUps
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CleanUpsAPI {
    @GET("cities.json")
    fun getAllCleanUps(): Call<List<CleanUps>>

    @GET("{code}.json")
    fun getCleanUpInfo(@Path("code")code : String): Call<List<CleanUpInfo>>
}