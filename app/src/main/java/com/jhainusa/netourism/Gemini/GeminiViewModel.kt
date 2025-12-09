package com.jhainusa.netourism.Gemini

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GeminiViewModel : ViewModel() {

    private val repository = GeminiRepository()

    private val _authority = MutableStateFlow("")
    val authority = _authority.asStateFlow()

    fun getAuthority(message: String) {
        viewModelScope.launch {
            val result = repository.getAuthority(message)
            _authority.value = result
            Log.d("MESSAGE SOS",result)
        }
    }
}
