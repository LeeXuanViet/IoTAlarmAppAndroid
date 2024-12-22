package com.example.iotalarmappandroid.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.iotalarmappandroid.data.Alarm
import com.example.iotalarmappandroid.databinding.ItemAlarmBinding // Import View Binding từ item_alarm.xml
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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


    // Hàm chuyển đổi thời gian ISO 8601 (UTC) sang giờ địa phương
    private fun parseIsoTimeToLocalTime(isoTime: String): Pair<Int, Int> {
        return try {
            val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            utcFormat.timeZone = TimeZone.getTimeZone("UTC") // Backend gửi ở UTC

            val parsedDate = utcFormat.parse(isoTime) ?: Date()
            val localCalendar = Calendar.getInstance()
            localCalendar.time = parsedDate

            // Trả về giờ và phút ở múi giờ địa phương
            Pair(localCalendar.get(Calendar.HOUR_OF_DAY), localCalendar.get(Calendar.MINUTE))
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(0, 0) // Mặc định nếu có lỗi
        }
    }

    // ViewHolder để ánh xạ dữ liệu
    inner class AlarmViewHolder(private val binding: ItemAlarmBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(alarm: Alarm) {
            val parsedTime = parseIsoTimeToLocalTime(alarm.alarmTime) // Chuyển đổi thời gian
            val formattedTime = String.format("%02d:%02d", parsedTime.first, parsedTime.second)
            binding.tvAlarmTime.text = formattedTime // Hiển thị giờ-phút
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
                    putExtra("light_blink", alarm.lightBlink) // Thêm lightBlink
                    putExtra("start_date", alarm.startDate) // Giữ nguyên startDate
                    putExtra("create_date", alarm.createDate) // Giữ nguyên createDate
                }
                context.startActivity(intent)
            }
        }

    }


}
