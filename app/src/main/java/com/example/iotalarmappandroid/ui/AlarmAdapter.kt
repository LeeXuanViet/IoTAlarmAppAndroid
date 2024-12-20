package com.example.iotalarmappandroid.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.iotalarmappandroid.data.Alarm
import com.example.iotalarmappandroid.databinding.ItemAlarmBinding // Import View Binding từ item_alarm.xml

class AlarmAdapter(private val alarms: List<Alarm>) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        // Sử dụng View Binding để inflate layout
        val binding = ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]
        println("Dữ liệu hiển thị: $alarm") // Debug log
        holder.bind(alarm)
    }


    override fun getItemCount(): Int = alarms.size

    // ViewHolder để ánh xạ dữ liệu
    inner class AlarmViewHolder(private val binding: ItemAlarmBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(alarm: Alarm) {
            binding.tvAlarmTime.text = alarm.alarmTime
            binding.tvAlarmSound.text = "Sound: ${alarm.sound}"
            binding.tvAlarmVolume.text = "Volume: ${alarm.volume}"
            binding.tvAlarmRepeat.text = "Repeat: ${alarm.repeat}"

            // Xử lý sự kiện click vào item để mở EditAlarmActivity
            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, EditAlarmActivity::class.java).apply {
                    putExtra("alarm_id", alarm.id)
                    putExtra("alarm_time", alarm.alarmTime)
                    putExtra("sound", alarm.sound)
                    putExtra("repeat", alarm.repeat)
                    putExtra("volume", alarm.volume)
                }
                context.startActivity(intent)
            }
        }
    }


}
