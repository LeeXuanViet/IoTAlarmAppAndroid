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

class EditAlarmActivity : AppCompatActivity() {
    private lateinit var timePicker: TimePicker
    private lateinit var spinnerSound: Spinner
    private lateinit var editTextRepeat: EditText
    private lateinit var seekBarVolume: SeekBar
    private lateinit var btnSave: Button

    private var alarmId: Int = 0 // ID của báo thức

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_alarm)

        // Ánh xạ các view
        timePicker = findViewById(R.id.timePicker)
        spinnerSound = findViewById(R.id.spinnerSound)
        editTextRepeat = findViewById(R.id.editTextRepeat)
        seekBarVolume = findViewById(R.id.seekBarVolume)
        btnSave = findViewById(R.id.btnSaveAlarm)

        // Lấy dữ liệu báo thức từ intent
        alarmId = intent.getIntExtra("alarm_id", -1) // Trả về -1 nếu không có ID
        val alarmTime = intent.getStringExtra("alarm_time") ?: "00:00"
        val sound = intent.getIntExtra("sound", 1)
        val repeat = intent.getStringExtra("repeat") ?: "None"
        val volume = intent.getFloatExtra("volume", 50f)

// Log dữ liệu nhận được để kiểm tra
        println("alarmId: $alarmId, alarmTime: $alarmTime, sound: $sound, repeat: $repeat, volume: $volume")

// Hiển thị dữ liệu lên giao diện
        val timeParts = alarmTime.split(":")
        timePicker.hour = timeParts[0].toIntOrNull() ?: 0
        timePicker.minute = timeParts[1].toIntOrNull() ?: 0
        spinnerSound.setSelection(sound - 1) // Giả sử sound là 1-based index
        editTextRepeat.setText(repeat)
        seekBarVolume.progress = volume.toInt()


        // Xử lý khi nhấn nút Save
        btnSave.setOnClickListener {
            val updatedAlarm = Alarm(
                id = alarmId,
                startDate = "", // Bỏ qua trường này trong cập nhật
                alarmTime = "${timePicker.hour}:${timePicker.minute}",
                isUsed = true,
                sound = spinnerSound.selectedItemPosition + 1,
                volume = seekBarVolume.progress.toFloat(),
                lightBlink = false,
                repeat = editTextRepeat.text.toString(),
                createDate = "", // Trường này backend không cần
                updateDate = ""  // Trường này backend sẽ cập nhật khi nhận request
            )

            // Gọi API để cập nhật báo thức
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    ApiClient.apiService.updateAlarm(alarmId, updatedAlarm)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EditAlarmActivity, "Alarm updated", Toast.LENGTH_SHORT).show()
                        finish() // Quay lại màn hình trước
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EditAlarmActivity, "Update failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
