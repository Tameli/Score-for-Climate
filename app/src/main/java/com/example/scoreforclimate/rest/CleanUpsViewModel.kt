package com.example.scoreforclimate.rest

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.HttpURLConnection

class CleanUpsViewModel : ViewModel() {
    val cities: MutableLiveData<List<CleanUps>> = MutableLiveData()
    val cleanUpInfo: MutableLiveData<List<CleanUpInfo>> = MutableLiveData()

    private val retrofit = Retrofit.Builder()
        .client(OkHttpClient().newBuilder().build())
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl("http://mobpro.kev-ae.ch/")
        .build()
    private val cleanUpServices: CleanUpsAPI = retrofit.create(
        CleanUpsAPI:: class.java)

    fun requestAllCleanUps() {
        val call = cleanUpServices.getAllCleanUps()
        call.enqueue(object : Callback<List<CleanUps>> { //damit es nicht auf mein Main Thread läuft

            override fun onResponse(call: Call<List<CleanUps>>, response: Response<List<CleanUps>>) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    cities.value = response.body()
                }
            }
            override fun onFailure(call: Call<List<CleanUps>>, t: Throwable){
                Log.e("Fehler","siehe ", t)
            }
        })
    }

    fun requestCleanUpInfo(code:String){
        val call = cleanUpServices.getCleanUpInfo(code)
        call.enqueue(object : Callback<List<CleanUpInfo>> {

            override fun onResponse(call: Call<List<CleanUpInfo>>, response: Response<List<CleanUpInfo>>) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    cleanUpInfo.value = response.body()
                }
            }
            override fun onFailure(call: Call<List<CleanUpInfo>>, t: Throwable){
                Log.e("Fehler","siehe ", t)
            }
        })
    }
}