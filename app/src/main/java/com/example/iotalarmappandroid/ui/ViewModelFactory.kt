package com.example.iotalarmappandroid.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.iotalarmappandroid.data.AlarmRepository

class AlarmViewModelFactory(private val repository: AlarmRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlarmViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
