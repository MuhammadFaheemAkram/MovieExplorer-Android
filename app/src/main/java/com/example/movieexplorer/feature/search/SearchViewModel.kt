package com.example.movieexplorer.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieexplorer.domain.usecase.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            uiState
                .map { state -> state.query.trim() }
                .debounce(350)
                .distinctUntilChanged()
                .collectLatest { query ->
                    search(query)
                }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(query = query)
        }
    }

    fun clearQuery() {
        _uiState.value = SearchUiState()
    }

    fun retry() {
        viewModelScope.launch {
            search(uiState.value.query.trim())
        }
    }

    private suspend fun search(query: String) {
        if (query.isBlank()) {
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    hasSearched = false,
                    movies = emptyList(),
                    errorMessage = null,
                )
            }
            return
        }

        _uiState.update { state ->
            state.copy(
                isLoading = true,
                hasSearched = true,
                errorMessage = null,
            )
        }

        runCatching {
            searchMoviesUseCase(query).map { movie -> movie.toSearchUiModel() }
        }.onSuccess { movies ->
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    movies = movies,
                    errorMessage = null,
                )
            }
        }.onFailure { throwable ->
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    movies = emptyList(),
                    errorMessage = throwable.message ?: "Could not search movies.",
                )
            }
        }
    }
}
