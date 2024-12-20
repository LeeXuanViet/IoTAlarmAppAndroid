package com.example.iotalarmappandroid.network

import com.example.iotalarmappandroid.data.Alarm
import retrofit2.http.*

interface ApiService {

    @GET("/alarms")  // Lấy tất cả báo thức
    suspend fun getAlarms(): List<Alarm>

    @POST("/alarms") // Thêm báo thức mới
    suspend fun createAlarm(@Body alarm: Alarm)

    @PUT("/alarms/{id}") // Cập nhật báo thức theo ID
    suspend fun updateAlarm(@Path("id") id: Int, @Body alarm: Alarm)

    @DELETE("/alarms/{id}") // Xóa báo thức theo ID
    suspend fun deleteAlarm(@Path("id") id: Int)
}
