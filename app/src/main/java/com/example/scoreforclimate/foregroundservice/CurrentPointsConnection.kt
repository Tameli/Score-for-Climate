package com.example.scoreforclimate.foregroundservice

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.example.scoreforclimate.foregroundservice.CurrentPointsApi

class CurrentPointsConnection: ServiceConnection {
    private var currentPointsApi : CurrentPointsApi? = null

    fun getCurrentPointsApi(): CurrentPointsApi? {
        return currentPointsApi
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        currentPointsApi = service as CurrentPointsApi
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        currentPointsApi = null
    }
}