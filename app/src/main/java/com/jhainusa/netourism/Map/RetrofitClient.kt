package com.jhainusa.netourism.Map

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// GraphHopper API
interface GraphHopperApi {
    @GET("route")
    suspend fun getRoute(
        @Query("point") start: String,
        @Query("point") end: String,
        @Query("profile") profile: String = "car",
        @Query("points_encoded") pointsEncoded: Boolean = false,
        @Query("key") key: String,
        @Header("User-Agent") userAgent: String = "com.jhainusa.netourism"
    ): GHRouteResponse
}

// Nominatim API for geocoding
interface NominatimApi {
    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 1
    ): List<NominatimResult>
}

data class NominatimResult(
    val lat: String,
    val lon: String,
    val display_name: String
)

// GraphHopper response models
data class GHRouteResponse(
    val paths: List<GHPath>
)

data class GHPath(
    val points: GHPoints,
    val instructions: List<GHInstruction>
)

data class GHPoints(
    val coordinates: List<List<Double>>
)

data class GHInstruction(
    val text: String,
    val distance: Double,
    val time: Long
)

object ApiClient {

    private val client = okhttp3.OkHttpClient.Builder().build()

    val graphhopper: GraphHopperApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://graphhopper.com/api/1/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GraphHopperApi::class.java)
    }

    val nominatim: NominatimApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NominatimApi::class.java)
    }
}
