package com.jhainusa.netourism.Gemini

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.jhainusa.netourism.Advice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

sealed class GeminiState {
    object Loading : GeminiState()
    data class Success(val dos: List<Advice>, val donts: List<Advice>) : GeminiState()
    data class Error(val message: String) : GeminiState()
}

@Serializable
data class GeminiResponse(val dos: List<Advice>, val donts: List<Advice>)

class DosAndDontsViewModel : ViewModel() {

    private val _geminiState = MutableStateFlow<GeminiState>(GeminiState.Loading)
    val geminiState = _geminiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = "AIzaSyBbVALiNts7npj7mriLJBdAFr_fh4H7IKQ" // TODO: Add your API key from https://makersuite.google.com/
    )

    fun generateAdvice(tribe: String) {
        viewModelScope.launch {
            _geminiState.value = GeminiState.Loading
            try {
                val prompt = "Provide a list of do's and don'ts for tourists visiting the $tribe place or tribe in North East India. Return the lists in a strict JSON format with two keys, \"dos\" and \"donts\", where each key contains five objects. Each object must have \"title\" and \"description\" keys with string values. Do not include any markdown formatting or other text outside of the JSON."
                val response = generativeModel.generateContent(prompt)
                val (dos, donts) = parseGeminiResponse(response.text ?: "")
                _geminiState.value = GeminiState.Success(dos, donts)
            } catch (e: Exception) {
                _geminiState.value = GeminiState.Error("Failed to generate or parse advice: ${e.message}")
            }
        }
    }

    private fun parseGeminiResponse(response: String): Pair<List<Advice>, List<Advice>> {
        val json = Json { ignoreUnknownKeys = true; isLenient = true }
        // Clean the response to remove potential markdown code blocks
        val cleanResponse = response.removePrefix("```json").removePrefix("\n").removeSuffix("```").trim()
        val geminiResponse = json.decodeFromString<GeminiResponse>(cleanResponse)
        return geminiResponse.dos to geminiResponse.donts
    }
}
