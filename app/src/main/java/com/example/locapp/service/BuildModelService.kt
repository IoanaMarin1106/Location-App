package com.example.locapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class BuildModelService: Service() {
    private val buildModelWorker: BuildModelWorker = BuildModelWorker("x")
    private val TAG = "BuildModelService"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreateCommand has been called")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        buildModelWorker.start()
        super.onStartCommand(intent, flags, startId)
        Log.d(TAG, "onStartCommand has been called")
        return START_STICKY
    }

    override fun onDestroy() {
        buildModelWorker.stopWorker()
        super.onDestroy()
        Log.d(TAG, "onDestroyCommand has been called")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}