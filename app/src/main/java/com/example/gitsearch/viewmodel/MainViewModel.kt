package com.example.gitsearch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gitsearch.data.GitHubRepository
import com.example.gitsearch.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: GitHubRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state: StateFlow<UiState> get() = _state

    fun fetchRepositories(username: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val result = repository.getUserRepositories(username)
            _state.value = when {
                result.isSuccess -> UiState.Success(result.getOrNull()!!)
                result.isFailure -> UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                else -> UiState.Error("An unexpected error occurred.")
            }
        }
    }

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val repos: List<Repository>) : UiState()
        data class Error(val message: String) : UiState()
    }
}
