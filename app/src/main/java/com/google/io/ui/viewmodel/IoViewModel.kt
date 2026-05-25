package com.google.io.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.io.data.model.Session
import com.google.io.data.repository.IoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * The ViewModel is like the "Brain" of our screen. 
 */
class IoViewModel(private val repository: IoRepository) : ViewModel() {

    // TASK 1: State for Search and Filtering
    // These "MutableStateFlows" hold the current search text and the chosen category.
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    /**
     * This is the heart of our Intelligent Search!
     * We use "combine" to watch BOTH the search text and the category.
     * Whenever either one changes, it triggers a new query to Room!
     */
    val filteredSessions: StateFlow<List<Session>> = combine(
        _searchQuery,
        _selectedCategory
    ) { query, category ->
        query to category
    }.flatMapLatest { (query, category) ->
        // We tell the repository to give us a Flow of sessions 
        // that match our current query and category.
        repository.getFilteredSessions(query, category)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // 1. All Sessions StateFlow
    val allSessions: StateFlow<List<Session>> = repository.getSessions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 2. Bookmarked Sessions StateFlow
    val bookmarkedSessions: StateFlow<List<Session>> = repository.getBookmarkedSessions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 3. Loading State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // When the brain starts, we ask it to "refresh" the data from the internet
        refreshData()
    }

    /**
     * Updates the search query when the user types in the SearchBar.
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Updates the selected category when the user clicks a FilterChip.
     */
    fun updateCategory(category: String) {
        _selectedCategory.value = category
    }

    /**
     * Fetches the latest sessions from our Mock API and saves them to Room.
     */
    fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.refreshSessions()
            _isLoading.value = false
        }
    }

    /**
     * Toggles the bookmark status of a session.
     */
    fun toggleBookmark(session: Session) {
        viewModelScope.launch {
            repository.toggleBookmark(session.id, !session.isBookmarked)
        }
    }
}

/**
 * This "Factory" is a helper to create the ViewModel.
 */
class IoViewModelFactory(private val repository: IoRepository) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
