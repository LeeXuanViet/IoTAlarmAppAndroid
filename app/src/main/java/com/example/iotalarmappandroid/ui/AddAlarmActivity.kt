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

class AddAlarmActivity : AppCompatActivity() {
    private lateinit var timePicker: TimePicker
    private lateinit var spinnerSound: Spinner
    private lateinit var spinnerRepeat: Spinner
    private lateinit var seekBarVolume: SeekBar
    private lateinit var btnSave: Button
    private lateinit var switchLightBlink: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alarm)

        // Ánh xạ các view
        timePicker = findViewById(R.id.timePicker)
        timePicker.setIs24HourView(true)
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

        // Xử lý khi nhấn Save
        btnSave.setOnClickListener {
            saveAlarm()
        }
    }

    private fun saveAlarm() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
        calendar.set(Calendar.MINUTE, timePicker.minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val alarmTimeUTC = sdf.format(calendar.time)

        val newAlarm = Alarm(
            id = 0, // ID sẽ được backend tự động tạo
            startDate = getCurrentDate(),
            alarmTime = alarmTimeUTC,
            isUsed = true,
            sound = spinnerSound.selectedItemPosition + 1,
            volume = seekBarVolume.progress.toFloat(),
            lightBlink = switchLightBlink.isChecked,
            repeat = spinnerRepeat.selectedItem.toString()
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                ApiClient.apiService.createAlarm(newAlarm)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddAlarmActivity, "Alarm created successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddAlarmActivity, "Create failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}
