package com.jhainusa.netourism.Map

import android.location.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object LocationRepository {
    private val _lastKnownLocation = MutableStateFlow<Location?>(null)
    val lastKnownLocation = _lastKnownLocation.asStateFlow()

    private val _currentPlaceName = MutableStateFlow<String?>(null)
    val currentPlaceName = _currentPlaceName.asStateFlow()

    fun updateLocation(location: Location) {
        _lastKnownLocation.value = location
    }

    fun updatePlaceName(placeName: String) {
        _currentPlaceName.value = placeName
    }
}
