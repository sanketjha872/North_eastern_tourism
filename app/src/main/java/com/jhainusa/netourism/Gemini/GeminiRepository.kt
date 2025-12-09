package com.jhainusa.netourism.Gemini

import com.google.ai.client.generativeai.GenerativeModel

class GeminiRepository {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = "AIzaSyA0SwuX6rytMimSK-hBv_qiNMZMifZiacg"
    )

    suspend fun getAuthority(message: String): String {
        val prompt = "Based on the following message, which authority should be contacted? Answer with only one word: Police, Ambulance, Fire or Women HelpLine . Message: $message"
        val response = generativeModel.generateContent(prompt)
        return response.text ?: "Authority"
    }
}
