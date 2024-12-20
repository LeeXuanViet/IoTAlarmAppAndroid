package com.example.iotalarmappandroid.data

data class Alarm(
    val id: Int,               // ID báo thức
    val startDate: String,     // Ngày bắt đầu
    val alarmTime: String,     // Thời gian báo thức
    val isUsed: Boolean,       // Báo thức bật/tắt
    val sound: Int,            // Loại nhạc
    val volume: Float,         // Âm lượng
    val lightBlink: Boolean,   // Đèn nhấp nháy
    val repeat: String,        // Chu kỳ lặp lại
    val createDate: String = "",   // Ngày tạo
    val updateDate: String = "",   // Ngày cập nhật
    val _id: String? = null,   // ID MongoDB (tùy chọn)
    val __v: Int? = null       // Phiên bản dữ liệu (tùy chọn)
)
