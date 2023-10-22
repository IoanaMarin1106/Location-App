package com.example.locapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.locapp.service.LocationService

class RestartBackgroundService: BroadcastReceiver() {
    private val TAG = "Broadcast Listened"

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "Service tried to stop")
        Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show()

        context!!.startForegroundService(Intent(context, LocationService::class.java))
    }
}