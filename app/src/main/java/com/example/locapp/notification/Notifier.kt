package com.example.locapp.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.locapp.MainActivity

class Notifier {

    fun inviteToReview(context: Context, locationName: String) {
        createNotificationChannel(context)
        sendReviewNotification(context, locationName)
    }

    private fun createNotificationChannel(context: Context) {
        val name = "Review Channel"
        val descriptionText = "Channel for review notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("review_channel_id", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun sendReviewNotification(context: Context, locationName: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("navigateToLocationReviews", true)
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, "review_channel_id")
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("Dish the Dish!")
            .setContentText("Rate your last bite at ${locationName}!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return@with
            }
            notify(1, builder.build())
        }
    }
}