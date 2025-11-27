package com.jhainusa.netourism.Zones

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
data class Zone(
    val id: String,
    val name: String,
    val description: String? = null,
    val zone_type: String,
    val risk_level: String = "MEDIUM",
    val shape_type: String = "circle",

    val latitude: Double? = null,
    val longitude: Double? = null,
    val radius_meters: Double? = null,

    // polygon: List of coordinate pairs -> [[lat, lng], [lat, lng], ...]
    val polygon_coordinates: List<List<Double>>? = null,

    // raw geometry — decode as object
    val geom: Geometry?,  // ✅ notice the comma here

    val status: String = "ACTIVE",

    // default: {"exit": true, "entry": true, "extended_stay": false}
    val notifications: Map<String, Boolean>? = mapOf(
        "exit" to true,
        "entry" to true,
        "extended_stay" to false
    ),

    // default: []
    val rules: List<String> = emptyList(),          // << FIX HERE

    val active_visitors: Int? = 0,
    val total_visits: Int? = 0,
    val total_alerts: Int? = 0,

    val region: String? = null,
    val state: String? = null,
    val district: String? = null,

    val created_by: String? = null,

    val created_at: Instant? = null,
    val updated_at: Instant? = null
)

// Nested data classes to match JSON geom object
@Serializable
data class Geometry(
    val type: String,
    val crs: CRS? = null,
    val coordinates: List<List<List<Double>>>? = null  // You can make this more precise for Polygons
)

@Serializable
data class CRS(
    val type: String,
    val properties: CRSProperties
)

@Serializable
data class CRSProperties(
    val name: String
)
@Serializable
data class Rule(
    val text: String
)
