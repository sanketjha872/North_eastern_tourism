package com.jhainusa.netourism.SupaBase


import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.jhainusa.netourism.Map.LocationService
import com.jhainusa.netourism.SupaBase.Supabase.client
import com.jhainusa.netourism.UserPreferences.UserPreferencesManager
import com.jhainusa.netourism.Zones.Zone
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.realtime.Realtime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


object Supabase {

    // Paste your Supabase URL & API Key here
    private const val SUPABASE_URL = "https://yhutbezocwtowoqhpibk.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InlodXRiZXpvY3d0b3dvcWhwaWJrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjM1NzQ5NjAsImV4cCI6MjA3OTE1MDk2MH0.ebtpSclx-yP1TQ4jmTNfoTTmupKYdpFEaYxPgWQWQwU"

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Postgrest)
        install(Storage)
        install(Realtime)
    }
}
suspend fun fetchAllUsersbyBlockchainID(blockchainId : String): User? {
    return client
        .from("users")
        .select {
            filter { eq("blockchain_id", blockchainId) }
        }
        .decodeSingleOrNull<User>()
}
suspend fun fetchAllUsers(): List<User> {
    return client
        .from("users")
        .select()
        .decodeList<User>()
}

suspend fun uploadAlert(alert: Alert): Boolean {
    return try {
        client
            .from("alerts")
            .insert(alert)

        true
    } catch (e: Exception) {
        Log.e("Supabase", "Error uploading alert", e)
        false
    }
}
suspend fun loadZones() : List<Zone> {
     return client
            .from("zones")
            .select()
            .decodeList<Zone>()
}

data class TouristLocation(val latitude: Double, val longitude: Double, val name: String)


class ReportViewModel( private val prefsManager: UserPreferencesManager,
                       private val context: Context
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _saved = MutableStateFlow<User?>(null)
    val saver : StateFlow<User?> = _saved
    
    private val _zones = MutableStateFlow<List<Zone>>(emptyList())
    val zones = _zones.asStateFlow()

    private val _nearbyTourists = MutableStateFlow<List<TouristLocation>>(emptyList())
    val nearbyTourists = _nearbyTourists.asStateFlow()

    fun fetchNearbyTourists() {
        viewModelScope.launch {
            // Dummy data for now
            _nearbyTourists.value = listOf(
                TouristLocation(25.6155141, 91.9001645, "Tourist 1"),
                TouristLocation(25.6154186, 91.9004584, "Tourist 2"),
                TouristLocation(25.6151686, 91.9006400, "Tourist 3"),
                TouristLocation(25.6148596, 91.9006400, "Tourist 4"),
                TouristLocation(25.6146096, 91.9004584, "Tourist 5"),
                TouristLocation(25.6145141, 91.9001645, "Tourist 6")
            )
        }
    }

    fun fetchUsers() {
        viewModelScope.launch {
            try {
                val list = fetchAllUsers()
                Log.d("users", list.toString())

            } catch (e: Exception) {
                print(e)
            }
        }
    }

    fun fetchZones() {
        viewModelScope.launch {
            try {
                val list = loadZones()
                Log.d("zones",list.toString())
                _zones.value = list
                startLocationService(list)
            } catch (e: Exception) {
                Log.e("zones_error", e.toString())
            }
        }
    }
    private fun startLocationService(zones: List<Zone>) {
        val intent = Intent(context, LocationService::class.java).apply {
            putExtra("zones", Gson().toJson(zones))
        }
        context.startService(intent)
    }

    fun loadReportsFromNetwork(id: String) {
        viewModelScope.launch {
            try {
                val data = fetchAllUsersbyBlockchainID(id)
                if (data != null) {
                    _user.value = data

                    // Save to SharedPreferences
                    prefsManager.saveUser(data)
                    Log.d("ReportViewModel", "User saved to preferences: ${data.name}")
                } else {
                    Log.d("User not found","$id")
                }
            } catch (e: Exception) {
                Log.e("ReportViewModel", "Error loading user", e)
            }
        }
    }

    fun loadUserFromPrefs() {
        val savedUser = prefsManager.getUser()
        if (savedUser != null) {
            _saved.value = savedUser
            Log.d("ReportViewModel", "User loaded from preferences: ${savedUser.name}")
        }
    }

    fun uploadAlertToServer(
        alert: Alert
    ) {
        viewModelScope.launch {
            val savedUser = prefsManager.getUser()
            if (savedUser == null) {
                Log.e("Alert", "Cannot send alert — No user logged in")
                return@launch
            }
            try {
                val result = uploadAlert(alert)

                if (result) {
                    Log.d("Alert", "Alert sent successfully to Supabase")
                } else {
                    Log.e("Alert", "Failed to send alert")
                }

            } catch (e: Exception) {
                Log.e("Alert", "Exception while sending alert", e)
            }
        }
    }

}

class ReportViewModelFactory(
    private val prefsManager: UserPreferencesManager,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportViewModel(prefsManager, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}