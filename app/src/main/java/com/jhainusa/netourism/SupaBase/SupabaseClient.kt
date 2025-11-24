package com.jhainusa.netourism.SupaBase


import android.util.Log
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jhainusa.netourism.SupaBase.Supabase.client
import com.jhainusa.netourism.UserPreferences.UserPreferencesManager
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.realtime.Realtime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
        .select{
            filter { eq("blockchain_id", blockchainId) }
        }
        .decodeSingleOrNull<User>()
}
class ReportViewModel( private val prefsManager: UserPreferencesManager
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _saved = MutableStateFlow<User?>(null)
    val saver : StateFlow<User?> = _saved

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

}

class ReportViewModelFactory(
    private val prefsManager: UserPreferencesManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportViewModel(prefsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}