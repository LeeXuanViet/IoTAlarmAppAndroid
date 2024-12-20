package com.example.iotalarmappandroid

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.iotalarmappandroid.data.AlarmRepository
import com.example.iotalarmappandroid.databinding.ActivityMainBinding
import com.example.iotalarmappandroid.network.ApiClient
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
            println("Dữ liệu nhận được từ ViewModel: $alarms") // Debug log
            if (alarms.isNullOrEmpty()) {
                binding.emptyStateText.text = "Không có báo thức nào."
                binding.recyclerView.adapter = null
            } else {
                binding.emptyStateText.text = "" // Xóa trạng thái rỗng
                binding.recyclerView.adapter = AlarmAdapter(alarms)
            }
        })

        alarmViewModel.fetchAlarms()
    }

}
