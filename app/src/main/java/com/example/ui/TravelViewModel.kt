package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Destination
import com.example.data.DestinationsData
import com.example.data.DiaryEntry
import com.example.api.GeminiClient
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TravelViewModel(application: Application) : AndroidViewModel(application) {

    private val db by lazy {
        androidx.room.Room.databaseBuilder(
            application,
            com.example.data.TravelDatabase::class.java,
            "tn_tour_diary_database"
        ).fallbackToDestructiveMigration().build()
    }

    private val repository by lazy {
        com.example.data.TravelRepository(db.diaryDao())
    }

    // --- Diary States ---
    val diaryEntries: StateFlow<List<DiaryEntry>> = repository.allEntries
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addDiaryEntry(title: String, destination: String, visitDate: String, rating: Int, notes: String) {
        viewModelScope.launch {
            repository.insert(
                DiaryEntry(
                    title = title,
                    destination = destination,
                    visitDate = visitDate,
                    rating = rating,
                    notes = notes
                )
            )
        }
    }

    fun deleteDiaryEntry(entry: DiaryEntry) {
        viewModelScope.launch {
            repository.delete(entry)
        }
    }

    // --- Exploration Screen States ---
    var searchQuery by mutableStateOf("")
        private set

    var selectedCategory by mutableStateOf("All")
        private set

    var bookmarkedIds by mutableStateOf(setOf<String>())
        private set

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun selectCategory(category: String) {
        selectedCategory = category
    }

    fun toggleBookmark(destinationId: String) {
        bookmarkedIds = if (bookmarkedIds.contains(destinationId)) {
            bookmarkedIds - destinationId
        } else {
            bookmarkedIds + destinationId
        }
    }

    val filteredDestinations: List<Destination>
        get() = DestinationsData.list.filter { dest ->
            val matchesQuery = dest.name.contains(searchQuery, ignoreCase = true) ||
                    dest.region.contains(searchQuery, ignoreCase = true) ||
                    dest.summary.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == "All" || dest.category == selectedCategory
            matchesQuery && matchesCategory
        }

    // --- AI Planner States ---
    var startCity by mutableStateOf("Chennai")
    var selectedDestName by mutableStateOf("Madurai")
    var numDays by mutableStateOf(3)
    var travelStyle by mutableStateOf("Cultural Heritage")
    var extraRequirements by mutableStateOf("")

    var itineraryResult by mutableStateOf("")
        private set

    var isPlanningLoading by mutableStateOf(false)
        private set

    fun updateStartCity(city: String) { startCity = city }
    fun updateSelectedDestName(name: String) { selectedDestName = name }
    fun updateNumDays(days: Int) { numDays = days }
    fun updateTravelStyle(style: String) { travelStyle = style }
    fun updateExtraRequirements(reqs: String) { extraRequirements = reqs }

    fun generateItinerary() {
        if (isPlanningLoading) return
        isPlanningLoading = true
        itineraryResult = ""
        viewModelScope.launch {
            val response = GeminiClient.generateItinerary(
                startCity = startCity,
                destinations = selectedDestName,
                numDays = numDays,
                travelStyle = travelStyle,
                extraRequirements = extraRequirements
            )
            itineraryResult = response
            isPlanningLoading = false
        }
    }

    fun setQuickPlan(start: String, dest: String, days: Int, style: String, extra: String = "") {
        startCity = start
        selectedDestName = dest
        numDays = days
        travelStyle = style
        extraRequirements = extra
        generateItinerary()
    }
}
