package com.example.api

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @Json(name = "contents") val contents: List<Content>,
    @Json(name = "generationConfig") val generationConfig: GenerationConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    @Json(name = "temperature") val temperature: Float? = null,
    @Json(name = "topP") val topP: Float? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @Json(name = "candidates") val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: Content? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    suspend fun generateItinerary(
        startCity: String,
        destinations: String,
        numDays: Int,
        travelStyle: String,
        extraRequirements: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "API_KEY_ERROR: Gemini API Key is missing. Please set GEMINI_API_KEY in the Secrets panel in AI Studio sidebar."
        }

        val prompt = """
            Create a custom tourist itinerary in Tamil Nadu based on these preferences:
            - Starting location: $startCity
            - Destinations: $destinations
            - Number of days: $numDays
            - Travel Style/Budget: $travelStyle
            - Special Requests: $extraRequirements
            
            Please deliver a highly engaging, professional day-by-day itinerary. For each day, provide:
            1. Day Theme: (e.g. 'Day 1: Colonial Heritage in Chennai' or 'Day 2: Temples & Spices in Madurai')
            2. Morning Activity: Description & specific place
            3. Afternoon Activity: Lunch choice (local delicacy) & site visit
            4. Evening Activity: Twilight stroll, viewpoint, or traditional show
            5. Insider Tip: A specific tip for photos, transport, or timings.
            
            Use clean, professional tone, separate the days with clear headers (e.g., '--- Day X ---'), and format as simple elegant text. Keep it highly detailed but exciting!
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = prompt)))
            ),
            generationConfig = GenerationConfig(temperature = 0.7f),
            systemInstruction = Content(
                parts = listOf(Part(text = "You are 'TN Tour AI Guide', a luxury, expert local travel planner specializing in Tamil Nadu’s classical heritage, hill stations, temples, beaches, and rich gastronomy. Your guides should feel warm, culturally authentic, and deeply knowledgeable."))
            )
        )

        try {
            val response = service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "No itinerary generated. Please try again."
        } catch (e: Exception) {
            "Error: ${e.localizedMessage ?: e.message ?: "An unknown network error occurred."}"
        }
    }
}
