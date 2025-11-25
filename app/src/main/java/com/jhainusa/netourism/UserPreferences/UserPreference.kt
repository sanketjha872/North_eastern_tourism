package com.jhainusa.netourism.UserPreferences

import android.content.Context
import android.content.SharedPreferences
import com.jhainusa.netourism.SupaBase.User
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UserPreferencesManager(val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    val appContext: Context
        get() = context.applicationContext

    companion object {
        private const val PREFS_NAME = "user_preferences"

        // Keys
        private const val KEY_USER_ID = "user_id"
        private const val KEY_TOURIST_ID = "tourist_id"
        private const val KEY_BLOCKCHAIN_ID = "blockchain_id"
        private const val KEY_EMAIL = "email"
        private const val KEY_NAME = "name"
        private const val KEY_DOCUMENT_TYPE = "document_type"
        private const val KEY_DOCUMENT_NUMBER = "document_number"
        private const val KEY_DOCUMENT_HASH = "document_hash"
        private const val KEY_COUNTRY = "country"
        private const val KEY_STATE = "state"
        private const val KEY_ITINERARY_START = "itinerary_start_date"
        private const val KEY_ITINERARY_END = "itinerary_end_date"
        private const val KEY_CREATED_AT = "created_at"
        private const val KEY_UPDATED_AT = "updated_at"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_JSON = "user_json" // For storing entire user as JSON
    }

    // Save entire User object
    fun saveUser(user: User) {
        prefs.edit().apply {
            putString(KEY_USER_ID, user.id)
            putString(KEY_TOURIST_ID, user.touristId)
            putString(KEY_BLOCKCHAIN_ID, user.blockchainId)
            putString(KEY_EMAIL, user.email)
            putString(KEY_NAME, user.name)
            putString(KEY_DOCUMENT_TYPE, user.documentType)
            putString(KEY_DOCUMENT_NUMBER, user.documentNumber)
            putString(KEY_DOCUMENT_HASH, user.documentHash)
            putString(KEY_COUNTRY, user.country)
            putString(KEY_STATE, user.state)
            putString(KEY_ITINERARY_START, user.itineraryStartDate)
            putString(KEY_ITINERARY_END, user.itineraryEndDate)
            putString(KEY_CREATED_AT, user.createdAt)
            putString(KEY_UPDATED_AT, user.updatedAt)
            putBoolean(KEY_IS_LOGGED_IN, true)

            // Also save as JSON for easy retrieval
            val userJson = json.encodeToString(user)
            putString(KEY_USER_JSON, userJson)

            apply()
        }
    }

    // Get entire User object
    fun getUser(): User? {
        return try {
            val userJson = prefs.getString(KEY_USER_JSON, null)
            if (userJson != null) {
                json.decodeFromString<User>(userJson)
            } else {
                // Fallback: construct from individual fields
                if (prefs.contains(KEY_TOURIST_ID)) {
                    User(
                        id = prefs.getString(KEY_USER_ID, null),
                        touristId = prefs.getString(KEY_TOURIST_ID, "") ?: "",
                        blockchainId = prefs.getString(KEY_BLOCKCHAIN_ID, null),
                        email = prefs.getString(KEY_EMAIL, "") ?: "",
                        name = prefs.getString(KEY_NAME, "") ?: "",
                        documentType = prefs.getString(KEY_DOCUMENT_TYPE, "") ?: "",
                        documentNumber = prefs.getString(KEY_DOCUMENT_NUMBER, "") ?: "",
                        documentHash = prefs.getString(KEY_DOCUMENT_HASH, null),
                        country = prefs.getString(KEY_COUNTRY, null),
                        state = prefs.getString(KEY_STATE, null),
                        itineraryStartDate = prefs.getString(KEY_ITINERARY_START, null),
                        itineraryEndDate = prefs.getString(KEY_ITINERARY_END, null),
                        createdAt = prefs.getString(KEY_CREATED_AT, null),
                        updatedAt = prefs.getString(KEY_UPDATED_AT, null)
                    )
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    // Individual getters
    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)
    fun getTouristId(): String? = prefs.getString(KEY_TOURIST_ID, null)
    fun getBlockchainId(): String? = prefs.getString(KEY_BLOCKCHAIN_ID, null)
    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)
    fun getName(): String? = prefs.getString(KEY_NAME, null)
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    // Individual setters
    fun setBlockchainId(blockchainId: String) {
        prefs.edit().putString(KEY_BLOCKCHAIN_ID, blockchainId).apply()
    }

    fun setItinerary(startDate: String, endDate: String) {
        prefs.edit().apply {
            putString(KEY_ITINERARY_START, startDate)
            putString(KEY_ITINERARY_END, endDate)
            apply()
        }
    }

    // Clear all user data (logout)
    fun clearUser() {
        prefs.edit().clear().apply()
    }

    // Check if user exists
    fun hasUser(): Boolean {
        return prefs.contains(KEY_USER_JSON) || prefs.contains(KEY_TOURIST_ID)
    }
}
fun Context.getUserPrefs(): UserPreferencesManager {
    return UserPreferencesManager(this)
}