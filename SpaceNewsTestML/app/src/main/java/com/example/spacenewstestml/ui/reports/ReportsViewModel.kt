package com.example.spacenewstestml.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacenewstestml.data.models.Article
import com.example.spacenewstestml.data.repository.ArticleRepository
import com.example.spacenewstestml.ui.articles.ArticleState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val repository: ArticleRepository
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 10
    }

    private val _uiState = MutableStateFlow<ArticleState>(ArticleState.Loading)
    val uiState: StateFlow<ArticleState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private var currentOffset = 0

    init {
        observeSearch()
        fetchArticles()
    }

    fun fetchArticles(query: String? = null) {
        viewModelScope.launch {
            currentOffset = 0
            _uiState.value = ArticleState.Loading

            repository.getReports(limit = PAGE_SIZE, offset = 0, search = query).fold(
                onSuccess = { articles ->
                    _uiState.value = ArticleState.Success(
                        articles = articles,
                        canLoadMore = articles.size == PAGE_SIZE
                    )
                    currentOffset = articles.size
                },
                onFailure = { _uiState.value = ArticleState.Error(it.toUserMessage()) }
            )
        }
    }

    fun loadMore() {
        val current = _uiState.value as? ArticleState.Success ?: return
        if (!current.canLoadMore || current.isLoadingMore) return

        viewModelScope.launch {
            // Show spinner at bottom without wiping the list
            _uiState.value = current.copy(isLoadingMore = true)

            repository.getReports(
                limit = PAGE_SIZE,
                offset = currentOffset,
                search = _searchQuery.value.ifBlank { null }
            ).fold(
                onSuccess = { newArticles ->
                    _uiState.value = current.copy(
                        articles = current.articles + newArticles,
                        canLoadMore = newArticles.size == PAGE_SIZE,
                        isLoadingMore = false
                    )
                    currentOffset += newArticles.size
                },
                onFailure = {
                    // si hay error devuelvo la misma lista, no borro todo
                    _uiState.value = current.copy(isLoadingMore = false)
                }
            )
        }
    }

    private fun observeSearch() {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collect { query ->
                    fetchArticles(query = query.ifBlank { null })
                }
        }
    }

    fun onQueryChange(query: String) { _searchQuery.value = query }

    fun toggleSearch() {
        _isSearching.value = !_isSearching.value
        if (!_isSearching.value) _searchQuery.value = ""
    }

    private fun Throwable.toUserMessage(): String = when (this) {
        is HttpException -> "Error: (${code()}). Por favor vuelva a intentar."
        is IOException -> "No hay internet. Por favor intente nuevamente."
        else -> "Ha ocurrido un error. Por favor vuelva a intentar."
    }
}