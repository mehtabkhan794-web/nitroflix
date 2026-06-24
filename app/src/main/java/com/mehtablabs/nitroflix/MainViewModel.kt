package com.mehtablabs.nitroflix

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehtablabs.nitroflix.network.RetrofitClient
import com.mehtablabs.nitroflix.network.StreamItem
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _streams = mutableStateOf<List<StreamItem>>(emptyList())
    val streams: State<List<StreamItem>> = _streams

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun fetchStreams(url: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.instance.getCustomStreams(url)
                _streams.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}