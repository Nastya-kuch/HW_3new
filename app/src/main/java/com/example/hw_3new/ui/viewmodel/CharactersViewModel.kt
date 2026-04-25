package com.example.hw_3.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw_3.data.repository.CharacterRepository
import com.example.hw_3.model.Character
import kotlinx.coroutines.launch

data class CharacterListUiState(
    val searchQuery: String = "",
    val characters: List<Character> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val hasMorePages: Boolean = true,
    val currentPage: Int = 1
)

class CharacterViewModel(
    private val repository: CharacterRepository = CharacterRepository()
) : ViewModel() {

    var uiState by mutableStateOf(CharacterListUiState(isLoading = true))
        private set

    private var searchJob: kotlinx.coroutines.Job? = null

    init {
        loadFirstPage()
    }

    fun loadFirstPage() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            try {
                val characters = repository.getCharactersPage(1)
                val pageInfo = repository.getPagesInfo()

                uiState = uiState.copy(
                    isLoading = false,
                    characters = characters,
                    currentPage = 1,
                    hasMorePages = pageInfo.totalPages > 1
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun loadNextPage() {
        if (uiState.isLoadingMore || !uiState.hasMorePages) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoadingMore = true)

            try {
                val nextPage = uiState.currentPage + 1
                val newCharacters = repository.getCharactersPage(nextPage)
                val allCurrent = uiState.characters.toMutableList()
                allCurrent.addAll(newCharacters)

                val pageInfo = repository.getPagesInfo()

                uiState = uiState.copy(
                    isLoadingMore = false,
                    characters = allCurrent,
                    currentPage = nextPage,
                    hasMorePages = nextPage < pageInfo.totalPages
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoadingMore = false,
                    errorMessage = e.message ?: "Error loading more"
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            uiState = uiState.copy(searchQuery = query, isLoading = true)

            try {
                if (query.isBlank()) {
                    loadFirstPage()
                } else {
                    val results = repository.getCharactersPage(1)
                    val filtered = results.filter { character ->
                        character.name.contains(query, ignoreCase = true)
                    }
                    uiState = uiState.copy(
                        isLoading = false,
                        characters = filtered,
                        hasMorePages = false
                    )
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Search error"
                )
            }
        }
    }

    fun retry() {
        loadFirstPage()
    }
}