package com.example.spacenewstestml.ui.articles

import com.example.spacenewstestml.data.models.Article

sealed class ArticleState {
    object Loading : ArticleState()
    data class Success(
        val articles: List<Article>,
        val canLoadMore: Boolean = true,
        val isLoadingMore: Boolean = false
    ) : ArticleState()
    data class Error(val message: String) : ArticleState()
}