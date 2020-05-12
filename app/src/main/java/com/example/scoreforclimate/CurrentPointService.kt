package com.example.scoreforclimate

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.scoreforclimate.roomDB.ScoreDatabase

class CurrentPointService : Service() {

    private val NOTIFICATION_ID = 23
    private val currentPointsApi = CurrentPointsApiImpl()
    private var notificationManager : NotificationManager? = null

    override fun onCreate() {
        super.onCreate()
        notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        startForeground(NOTIFICATION_ID, createNotification("---waiting---"))
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return currentPointsApi
    }

    fun showPoints():String {
        val currentPoints : Int
        currentPoints = 2
        return "Aktueller Punktestand: $currentPoints"
    }
    fun createNotification(pointsString: String): Notification {
        return NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
            .setOngoing(true)
            .setContentTitle("Score for Climate")
            .setTicker("Score for Climate")
            .setContentText(pointsString)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setWhen(System.currentTimeMillis())
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
    }

    inner class CurrentPointsApiImpl : Binder(), CurrentPointsApi {
        override fun showPoints(): String {
            return this@CurrentPointService.showPoints()
        }
    }


}