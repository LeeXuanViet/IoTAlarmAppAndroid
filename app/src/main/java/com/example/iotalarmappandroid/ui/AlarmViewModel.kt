package com.example.iotalarmappandroid.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iotalarmappandroid.data.Alarm
import com.example.iotalarmappandroid.data.AlarmRepository
import kotlinx.coroutines.launch

class AlarmViewModel(private val alarmRepository: AlarmRepository) : ViewModel() {

    private val _alarms = MutableLiveData<List<Alarm>>()
    val alarms: LiveData<List<Alarm>> get() = _alarms

    fun addAlarm(alarm: Alarm) {
        viewModelScope.launch {
            try {
                alarmRepository.createAlarm(alarm)
                fetchAlarms() // Refresh danh sách báo thức
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateAlarm(id: Int, alarm: Alarm) {
        viewModelScope.launch {
            try {
                alarmRepository.updateAlarm(id, alarm)
                fetchAlarms() // Refresh danh sách báo thức
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteAlarm(id: Int) {
        viewModelScope.launch {
            try {
                alarmRepository.deleteAlarm(id)
                fetchAlarms() // Refresh danh sách báo thức
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchAlarms() {
        viewModelScope.launch {
            try {
                val result = alarmRepository.getAllAlarms()
                _alarms.postValue(result)
                println("Fetch alarms successful: $result")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error fetching alarms: ${e.message}")
            }
        }
    }


}
