package com.example.hw_3.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw_3.data.repository.CharacterRepository
import com.example.hw_3.model.Character
import kotlinx.coroutines.launch

data class CharacterDetailUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val character: Character? = null
)

class CharacterDetailViewModel(
    private val characterId: Int,
    private val repository: CharacterRepository = CharacterRepository()
) : ViewModel() {

    var uiState by mutableStateOf(CharacterDetailUiState(isLoading = true))
        private set

    init {
        loadCharacter()
    }

    fun loadCharacter() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            try {
                val character = repository.getCharacterById(characterId)
                uiState = uiState.copy(
                    isLoading = false,
                    character = character
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun retry() {
        loadCharacter()
    }
}