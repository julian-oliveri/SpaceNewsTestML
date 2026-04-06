package com.example.spacenewstestml.ui.articles

import android.util.Log
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spacenewstestml.ui.components.ArticleList
import com.example.spacenewstestml.ui.components.HeaderSearch
import com.example.spacenewstestml.ui.components.ErrorView
import com.example.spacenewstestml.ui.components.LoadingView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.example.spacenewstestml.data.models.Article

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ArticleScreen(
    viewModel: ArticleViewModel = hiltViewModel(),
    listState: LazyListState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onArticleClick: (Article) -> Unit
) {
    Log.d("SCREEN", "Recomposed")

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()

    Column {

        HeaderSearch(
            query = query,
            isSearching = isSearching,
            onQueryChange = viewModel::onQueryChange,
            onToggleSearch = viewModel::toggleSearch
        )

        Box(modifier = Modifier.fillMaxSize()) {

            when (val state = uiState) {

                is ArticleState.Success -> {
                    ArticleList(
                        articles = state.articles,
                        listState = listState,
                        canLoadMore = state.canLoadMore,
                        isLoadingMore = state.isLoadingMore,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        onArticleClick = onArticleClick,
                        onLoadMore = viewModel::loadMore,
                        type = "articles"
                    )
                }

                is ArticleState.Loading -> {
                    LoadingView()
                }

                is ArticleState.Error -> {
                    ErrorView(
                        message = state.message,
                        onRetry = viewModel::fetchArticles
                    )
                }
            }
        }
    }
}
