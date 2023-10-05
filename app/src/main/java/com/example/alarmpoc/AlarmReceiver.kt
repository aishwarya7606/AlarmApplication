package com.example.alarmpoc

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService

class AlarmReceiver : BroadcastReceiver() {

    var id = 0

    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("ALARM_MESSAGE") ?: "Default message"


        if (!message.equals("Default message")) {
            setupnotifychannel(context)
            id= intent?.getIntExtra("ALARM_ID",0)!!
            // Show a notification
            showNotification(context, message)

            // Play a ringtone
            playRingtone(context)
        } else {

            // Cancel the alarm (you'll need to have access to the alarm ID)
            val alarmId = id

            cancelAlarm(context, alarmId)

            // Dismiss the notification
            val notificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(0) // 0 is the notification ID you used when displaying the notification
        }
    }

    private fun cancelAlarm(context: Context?, alarmId: Int?) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, alarmId!!, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Cancel the alarm using the same PendingIntent used for scheduling it
        alarmManager.cancel(pendingIntent)
    }

    private fun setupnotifychannel(context: Context?) {
        // Create the notification channel (should be done only once)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "alarm_channel_id"
            val channelName = "Alarm Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = getSystemService(context!!, NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun showNotification(context: Context?, message: String) {

        val stopIntent = Intent(context, AlarmReceiver::class.java)
        stopIntent.action = "STOP_ALARM_ACTION" // Define an action string
        val pendingStopIntent = PendingIntent.getBroadcast(
            context,
            0,
            stopIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val channelId = "alarm_channel_id" // Ensure this matches the channel ID used in your app
        val notificationBuilder = NotificationCompat.Builder(context!!, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Alarm")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(
                R.drawable.ic_launcher_foreground, // Icon for the action button
                "Stop Alarm", // Title of the action button
                pendingStopIntent // PendingIntent to handle the action
            )
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //permission already given at app lauch
            return
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun playRingtone(context: Context?) {
        val defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ringtone = RingtoneManager.getRingtone(context, defaultRingtoneUri)
        ringtone.play()
    }
}

