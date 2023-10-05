package com.example.alarmpoc

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MainActivity : AppCompatActivity() {
    private val alarms = mutableListOf<Alarm>()
    private lateinit var alarmAdapter: AlarmAdapter // Create a custom adapter for your alarms

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize your RecyclerView and adapter
        alarmAdapter = AlarmAdapter(alarms)
        checkAndRequestPermissions()
        findViewById<RecyclerView>(R.id.alarmRecyclerView).layoutManager = LinearLayoutManager(this)
        findViewById<RecyclerView>(R.id.alarmRecyclerView).adapter = alarmAdapter

        // Set up a click listener for your "Set Alarm" button
        findViewById<Button>(R.id.setAlarmButton).setOnClickListener {
            // Handle user input to set an alarm here
            // You can show a dialog or navigate to a screen to enter alarm details
            // Once the user sets an alarm, add it to the 'alarms' list and notify the adapter
            // Schedule the alarm using the AlarmManager (as shown in the previous response)
            showAlarmInputDialog()
        }
    }

    private fun showAlarmInputDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_alarm_input, null)
        val timePicker = dialogView.findViewById<TimePicker>(R.id.timePicker)
        val messageEditText = dialogView.findViewById<EditText>(R.id.messageEditText)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Set Alarm")
            .setView(dialogView)
            .setPositiveButton("Set") { dialog, _ ->
                val hour = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePicker.hour
                } else {
                    timePicker.currentHour
                }
                val minute = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePicker.minute
                } else {
                    timePicker.currentMinute
                }
                val message = messageEditText.text.toString()

                // Create an Alarm object and add it to the 'alarms' list
                val alarm = Alarm(generateUniqueAlarmId(), calculateAlarmTime(hour, minute), message)
                alarms.add(alarm)
                alarmAdapter.notifyDataSetChanged()

                // Schedule the alarm using the AlarmManager (as shown in the previous response)
                scheduleAlarm(alarm)

//                newScheduleAlarm()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    // Function to generate a unique alarm ID
    private fun generateUniqueAlarmId(): Int {
        // Generate a unique ID for the new alarm (You can implement your logic here)
        // This is a simplified example using a simple incrementing counter
        return alarms.size + 1
    }

    // Function to calculate the alarm time in milliseconds
    private fun calculateAlarmTime(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val currentTime = Calendar.getInstance()
        if (calendar.before(currentTime)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return calendar.timeInMillis
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkAndRequestPermissions() {
        val permissionsToRequest = arrayOf(android.Manifest.permission.POST_NOTIFICATIONS , android.Manifest.permission.VIBRATE,
            android.Manifest.permission.USE_FULL_SCREEN_INTENT)

        val permissionsNotGranted = permissionsToRequest.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsNotGranted.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsNotGranted.toTypedArray(),
                PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            // Handle the result of the permission request here
            // You can check grantResults to see if the permissions were granted or denied
        }
    }
    // Add other lifecycle methods and utility functions as needed

    // Example function to schedule an alarm
    private fun scheduleAlarm(alarm: Alarm) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("ALARM_MESSAGE", alarm.message)
        intent.putExtra("ALARM_ID", alarm.id)
        val pendingIntent = PendingIntent.getBroadcast(this, alarm.id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Set the alarm to trigger at the specified time
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.timeInMillis, pendingIntent)
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 123 // You can choose any positive integer value
    }
}
