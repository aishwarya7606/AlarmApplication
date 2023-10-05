package com.example.alarmpoc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class AlarmAdapter(private val alarms: List<Alarm>) : RecyclerView.Adapter<AlarmViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarm_item_layout, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]
        holder.bind(alarm)
    }

    override fun getItemCount(): Int {
        return alarms.size
    }
}

class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(alarm: Alarm) {

        itemView.findViewById<TextView>(R.id.alarmMessageTextView).text = alarm.message
        itemView.findViewById<TextView>(R.id.alarmTimeTextView).text = convertEpochToDateTime(alarm.timeInMillis)

    }

    fun convertEpochToDateTime(epochTimeMillis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yy hh:mm a", Locale.getDefault())
        val date = Date(epochTimeMillis)
        return sdf.format(date)
    }
}