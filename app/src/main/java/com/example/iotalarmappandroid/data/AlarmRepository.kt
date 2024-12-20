package com.example.iotalarmappandroid.data

import com.example.iotalarmappandroid.network.ApiService

class AlarmRepository(private val apiService: ApiService) {

    // Lấy danh sách báo thức từ backend
    suspend fun getAllAlarms() = apiService.getAlarms()

    // Thêm báo thức mới
    suspend fun createAlarm(alarm: Alarm) = apiService.createAlarm(alarm)

    // Cập nhật báo thức
    suspend fun updateAlarm(id: Int, alarm: Alarm) = apiService.updateAlarm(id, alarm)

    // Xóa báo thức
    suspend fun deleteAlarm(id: Int) = apiService.deleteAlarm(id)
}
