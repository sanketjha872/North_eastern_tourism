package com.jhainusa.netourism.SupaBase


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String? = null,

    @SerialName("tourist_id")
    val touristId: String,

    @SerialName("blockchain_id")
    val blockchainId: String? = null,

    val email: String,
    val name: String,

    @SerialName("document_type")
    val documentType: String,

    @SerialName("document_number")
    val documentNumber: String,

    @SerialName("document_hash")
    val documentHash: String? = null,

    val country: String? = null,
    val state: String? = null,

    @SerialName("itinerary_start_date")
    val itineraryStartDate: String? = null, // or Instant

    @SerialName("itinerary_end_date")
    val itineraryEndDate: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null,

    @SerialName("image_url")
    val image_url: String? = null

)
