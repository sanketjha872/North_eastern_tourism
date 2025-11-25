package com.jhainusa.netourism.SupaBase

import kotlinx.serialization.Serializable

@Serializable
data class Alert(
    val tourist_id: String,
    val alert_type: String,
    val severity: String = "medium",
    val description: String? = null,
    val location_name: String? = null,
    val latitude: Double,
    val longitude: Double,
    val status: String = "open"
)