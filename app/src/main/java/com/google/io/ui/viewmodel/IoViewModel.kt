package com.google.io.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.io.data.Keynote
import com.google.io.data.KeynoteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class IoUiState {
    object Loading : IoUiState()
    data class Success(val keynotes: List<Keynote>) : IoUiState()
    data class Error(val message: String) : IoUiState()
}

class IoViewModel(private val repository: KeynoteRepository = KeynoteRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow<IoUiState>(IoUiState.Loading)
    val uiState: StateFlow<IoUiState> = _uiState.asStateFlow()

    init {
        fetchKeynotes()
    }

    fun fetchKeynotes() {
        viewModelScope.launch {
            _uiState.value = IoUiState.Loading
            repository.getKeynotes()
                .catch { e ->
                    _uiState.value = IoUiState.Error(e.message ?: "Unknown Error")
                }
                .collect { keynotes ->
                    _uiState.value = IoUiState.Success(keynotes)
                }
        }
    }
}
