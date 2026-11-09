package com.example.generator

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object LocalLlmClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .build()

    data class ConnectionTestResult(
        val isSuccessful: Boolean,
        val message: String,
        val availableModels: List<String> = emptyList()
    )

    /**
     * Checks if the user's local Ollama or LM Studio instance is running and reachable.
     */
    suspend fun testConnection(baseUrl: String): ConnectionTestResult = withContext(Dispatchers.IO) {
        val sanitizedUrl = baseUrl.trimEnd('/')
        try {
            // Check Ollama tags endpoint
            val request = Request.Builder()
                .url("$sanitizedUrl/api/tags")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: ""
                    val json = JSONObject(body)
                    val modelsArray = json.optJSONArray("models") ?: JSONArray()
                    val modelNames = mutableListOf<String>()
                    for (i in 0 until modelsArray.length()) {
                        val item = modelsArray.getJSONObject(i)
                        modelNames.add(item.optString("name", "model-$i"))
                    }
                    return@withContext ConnectionTestResult(
                        isSuccessful = true,
                        message = "Connected to local server! Found ${modelNames.size} models.",
                        availableModels = modelNames
                    )
                }
            }

            // Fallback check OpenAI-compatible /v1/models
            val v1Request = Request.Builder()
                .url("$sanitizedUrl/v1/models")
                .get()
                .build()

            client.newCall(v1Request).execute().use { response ->
                if (response.isSuccessful) {
                    return@withContext ConnectionTestResult(
                        isSuccessful = true,
                        message = "Connected to local OpenAI/LM Studio endpoint!"
                    )
                }
            }

            ConnectionTestResult(
                isSuccessful = false,
                message = "Server reached but returned error status. Ensure Ollama/LM Studio is running."
            )
        } catch (e: Exception) {
            ConnectionTestResult(
                isSuccessful = false,
                message = "Connection failed: ${e.localizedMessage ?: "Check IP / Port and ensure device is on the same Wi-Fi"}"
            )
        }
    }

    /**
     * Sends prompt to local model to get augmented architecture recommendations.
     */
    suspend fun queryLocalModel(
        baseUrl: String,
        modelName: String,
        prompt: String
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val sanitizedUrl = baseUrl.trimEnd('/')
            val payload = JSONObject().apply {
                put("model", modelName)
                put("prompt", prompt)
                put("stream", false)
            }

            val request = Request.Builder()
                .url("$sanitizedUrl/api/generate")
                .post(payload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IllegalStateException("Local server returned code ${response.code}")
                }
                val body = response.body?.string() ?: throw IllegalStateException("Empty response")
                val json = JSONObject(body)
                json.optString("response", "No response generated")
            }
        }
    }
}
