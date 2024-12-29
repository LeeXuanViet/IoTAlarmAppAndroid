package com.example.iotalarmappandroid

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.iotalarmappandroid.data.AlarmRepository
import com.example.iotalarmappandroid.databinding.ActivityMainBinding
import com.example.iotalarmappandroid.network.ApiClient
import com.example.iotalarmappandroid.ui.AddAlarmActivity
import com.example.iotalarmappandroid.ui.AlarmAdapter
import com.example.iotalarmappandroid.ui.AlarmViewModel
import com.example.iotalarmappandroid.ui.AlarmViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val alarmRepository by lazy { AlarmRepository(ApiClient.apiService) }
    private val alarmViewModel: AlarmViewModel by viewModels {
        AlarmViewModelFactory(alarmRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        alarmViewModel.alarms.observe(this, Observer { alarms ->
            if (alarms.isNullOrEmpty()) {
                binding.emptyStateText.text = "Không có báo thức nào."
                binding.recyclerView.adapter = null
            } else {
                binding.emptyStateText.text = ""
                binding.recyclerView.adapter = AlarmAdapter(alarms) { updatedAlarm ->
                    alarmViewModel.updateAlarm(updatedAlarm.id, updatedAlarm)
                }
            }
        })

        alarmViewModel.fetchAlarms()

        // Khởi tạo nút Add Alarm
        val btnAddAlarm = findViewById<Button>(R.id.btnAddAlarm)

        // Thiết lập sự kiện click cho nút
        btnAddAlarm.setOnClickListener {
            // Mở AddAlarmActivity khi nút được nhấn
            val intent = Intent(this, AddAlarmActivity::class.java)
            startActivity(intent)
        }
    }

    fun deleteAlarm(alarmId: Int) {
        alarmViewModel.deleteAlarm(alarmId) // Gọi ViewModel để xóa báo thức
        Toast.makeText(this, "Đã xóa báo thức!", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        alarmViewModel.fetchAlarms() // Đảm bảo dữ liệu luôn được làm mới khi trở lại MainActivity
    }


}
