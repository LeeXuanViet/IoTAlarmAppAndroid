package com.example.iotalarmappandroid.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.iotalarmappandroid.R
import com.example.iotalarmappandroid.data.Alarm
import com.example.iotalarmappandroid.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class EditAlarmActivity : AppCompatActivity() {
    private lateinit var timePicker: TimePicker
    private lateinit var spinnerSound: Spinner
    private lateinit var spinnerRepeat: Spinner
    private lateinit var seekBarVolume: SeekBar
    private lateinit var btnSave: Button
    private lateinit var switchLightBlink: Switch

    private var alarmId: Int = 0 // ID của báo thức

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_alarm)

        // Ánh xạ các view
        timePicker = findViewById(R.id.timePicker)
        timePicker.setIs24HourView(true) // Hiển thị theo 24 giờ
        spinnerSound = findViewById(R.id.spinnerSound)
        spinnerRepeat = findViewById(R.id.spinnerRepeat)
        seekBarVolume = findViewById(R.id.seekBarVolume)
        btnSave = findViewById(R.id.btnSaveAlarm)
        switchLightBlink = findViewById(R.id.switchLightBlink)

        // Thiết lập spinnerSound
        ArrayAdapter.createFromResource(
            this,
            R.array.sound_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSound.adapter = adapter
        }

        // Thiết lập spinnerRepeat
        ArrayAdapter.createFromResource(
            this,
            R.array.repeat_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerRepeat.adapter = adapter
        }

        // Nhận dữ liệu từ Intent
        alarmId = intent.getIntExtra("alarm_id", -1)
        val alarmTime = intent.getStringExtra("alarm_time") ?: "00:00:00"
        val sound = intent.getIntExtra("sound", 1)
        val repeat = intent.getStringExtra("repeat") ?: "None"
        val volume = intent.getFloatExtra("volume", 50f)
        val lightBlink = intent.getBooleanExtra("light_blink", false)

        // Log kiểm tra
        println("alarmId: $alarmId, alarmTime: $alarmTime, sound: $sound, repeat: $repeat, volume: $volume, lightBlink: $lightBlink")

        // Chuyển đổi alarmTime từ UTC sang giờ địa phương
        val parsedTime = parseIsoTimeToLocalTime(alarmTime)
        timePicker.hour = parsedTime.first
        timePicker.minute = parsedTime.second

        spinnerSound.setSelection(sound - 1) // Giả sử sound là 1-based index
        spinnerRepeat.setSelection(getRepeatIndex(repeat))
        seekBarVolume.progress = volume.toInt()
        switchLightBlink.isChecked = lightBlink

        // Xử lý khi nhấn Save
        btnSave.setOnClickListener {
            saveAlarm()
        }
    }

    // Hàm định dạng thời gian 24 giờ (HH:mm:ss)
    private fun formatTime24h(hour: Int, minute: Int): String {
        return String.format("%02d:%02d:00", hour, minute)
    }
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


    // Hàm lấy ngày hiện tại định dạng ISO
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    // Hàm tìm index giá trị Repeat
    private fun getRepeatIndex(repeat: String): Int {
        val repeatOptions = resources.getStringArray(R.array.repeat_options)
        return repeatOptions.indexOf(repeat).takeIf { it >= 0 } ?: 0
    }


    private fun saveAlarm() {
        // Lấy ngày hiện tại hoặc từ startDate nếu có
        val startDateStr = intent.getStringExtra("start_date") ?: ""
        val startDate: Date = if (startDateStr.isNotEmpty()) {
            // Parse startDate nếu cần thiết
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.parse(startDateStr) ?: Date()
        } else {
            // Nếu không có, sử dụng ngày hiện tại
            Date()
        }

        // Tạo Calendar với ngày startDate và giờ từ TimePicker
        val calendar = Calendar.getInstance()
        calendar.time = startDate
        calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
        calendar.set(Calendar.MINUTE, timePicker.minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Chuyển đổi sang UTC
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val alarmTimeUTC = sdf.format(calendar.time)

        val updatedAlarm = Alarm(
            id = alarmId,
            startDate = startDateStr,
            alarmTime = alarmTimeUTC,
            isUsed = true,
            sound = spinnerSound.selectedItemPosition + 1,
            volume = seekBarVolume.progress.toFloat(),
            lightBlink = switchLightBlink.isChecked,
            repeat = spinnerRepeat.selectedItem.toString()
            // Không cần gửi lại createDate và updateDate, backend sẽ xử lý
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                ApiClient.apiService.updateAlarm(alarmId, updatedAlarm)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditAlarmActivity, "Alarm updated successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditAlarmActivity, "Update failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
