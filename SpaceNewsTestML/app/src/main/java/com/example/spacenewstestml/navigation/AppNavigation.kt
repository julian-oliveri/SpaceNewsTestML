package com.example.spacenewstestml.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.activity.compose.BackHandler
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spacenewstestml.ui.articledetail.ArticleDetailScreen
import com.example.spacenewstestml.ui.articledetail.DetailWebViewScreen
import com.example.spacenewstestml.ui.articles.ArticleScreen
import com.example.spacenewstestml.ui.articles.ArticleState
import com.example.spacenewstestml.ui.articles.ArticleViewModel
import com.example.spacenewstestml.ui.blogs.BlogsScreen
import com.example.spacenewstestml.ui.blogs.BlogsViewModel
import com.example.spacenewstestml.ui.reports.ReportsScreen
import com.example.spacenewstestml.ui.reports.ReportsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
    articlesViewModel: ArticleViewModel = hiltViewModel(),
    blogViewModel: BlogsViewModel = hiltViewModel(),
    reportsViewModel: ReportsViewModel = hiltViewModel()
) {
    var selectedTab by rememberSaveable { mutableStateOf(Tab.ARTICLES) }

    val articleListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val blogListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val reportsListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    val articleNav = remember { mutableStateOf<ScreenNavState>(ScreenNavState.List) }
    val blogNav = remember { mutableStateOf<ScreenNavState>(ScreenNavState.List) }
    val reportsNav = remember { mutableStateOf<ScreenNavState>(ScreenNavState.List) }

    val articleUiState by articlesViewModel.uiState.collectAsStateWithLifecycle()
    val blogUiState by blogViewModel.uiState.collectAsStateWithLifecycle()
    val reportsUiState by reportsViewModel.uiState.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    BackHandler {
        when (selectedTab) {
            Tab.ARTICLES -> handleBack(articleNav, articleUiState, articleListState, coroutineScope)
            Tab.BLOG -> handleBack(blogNav, blogUiState, blogListState, coroutineScope)
            Tab.REPORTS -> handleBack(reportsNav, reportsUiState, reportsListState, coroutineScope)
        }
    }

    SharedTransitionLayout {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                BottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                AnimatedVisibility(visible = selectedTab == Tab.ARTICLES) {
                    ArticlesSection(
                        sectionKey = "articles",
                        navState = articleNav,
                        uiState = articleUiState,
                        listState = articleListState,
                        coroutineScope = coroutineScope,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        viewModel = articlesViewModel
                    )
                }
                AnimatedVisibility(visible = selectedTab == Tab.BLOG) {
                    BlogsSection(
                        sectionKey = "blogs",
                        navState = blogNav,
                        uiState = blogUiState,
                        listState = blogListState,
                        coroutineScope = coroutineScope,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        viewModel = blogViewModel
                    )
                }
                AnimatedVisibility(visible = selectedTab == Tab.REPORTS) {
                    ReportsSection(
                        sectionKey = "reports",
                        navState = reportsNav,
                        uiState = reportsUiState,
                        listState = reportsListState,
                        coroutineScope = coroutineScope,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        viewModel = reportsViewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalAnimationApi::class)
@Composable
private fun ArticlesSection(
    sectionKey: String,
    navState: MutableState<ScreenNavState>,
    uiState: ArticleState,
    listState: LazyListState,
    coroutineScope: CoroutineScope,
    sharedTransitionScope: SharedTransitionScope,
    viewModel: ArticleViewModel
) {
    AnimatedContent(
        targetState = navState.value,
        label = "${sectionKey}_nav_transition",
        transitionSpec = { fadeIn() togetherWith fadeOut() }
    ) { screen ->
        when (screen) {
            ScreenNavState.List -> {
                ArticleScreen(
                    viewModel = viewModel,
                    listState = listState,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = this@AnimatedContent,
                    onArticleClick = { article ->
                        navState.value = ScreenNavState.Detail(article)
                    }
                )
            }
            is ScreenNavState.Detail -> {
                ArticleDetailScreen(
                    article = screen.article,
                    onBack = {
                        val articles = (uiState as? ArticleState.Success)?.articles
                        val index = articles?.indexOfFirst { it.id == screen.article.id } ?: -1
                        coroutineScope.launch {
                            if (index >= 0) listState.scrollToItem(index)
                            navState.value = ScreenNavState.List
                        }
                    },
                    onReadFullArticle = { url ->
                        navState.value = ScreenNavState.Web(url, screen.article)
                    },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = this@AnimatedContent,
                    "articles"
                )
            }
            is ScreenNavState.Web -> {
                DetailWebViewScreen(
                    url = screen.url,
                    onBack = {
                        navState.value = ScreenNavState.Detail(screen.article)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalAnimationApi::class)
@Composable
private fun BlogsSection(
    sectionKey: String,
    navState: MutableState<ScreenNavState>,
    uiState: ArticleState,
    listState: LazyListState,
    coroutineScope: CoroutineScope,
    sharedTransitionScope: SharedTransitionScope,
    viewModel: BlogsViewModel
) {
    AnimatedContent(
        targetState = navState.value,
        label = "${sectionKey}_nav_transition",
        transitionSpec = { fadeIn() togetherWith fadeOut() }
    ) { screen ->
        when (screen) {
            ScreenNavState.List -> {
                BlogsScreen(
                    viewModel = viewModel,
                    listState = listState,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = this@AnimatedContent,
                    onArticleClick = { article ->
                        navState.value = ScreenNavState.Detail(article)
                    }
                )
            }
            is ScreenNavState.Detail -> {
                ArticleDetailScreen(
                    article = screen.article,
                    onBack = {
                        val articles = (uiState as? ArticleState.Success)?.articles
                        val index = articles?.indexOfFirst { it.id == screen.article.id } ?: -1
                        coroutineScope.launch {
                            if (index >= 0) listState.scrollToItem(index)
                            navState.value = ScreenNavState.List
                        }
                    },
                    onReadFullArticle = { url ->
                        navState.value = ScreenNavState.Web(url, screen.article)
                    },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = this@AnimatedContent,
                    "blogs"
                )
            }
            is ScreenNavState.Web -> {
                DetailWebViewScreen(
                    url = screen.url,
                    onBack = {
                        navState.value = ScreenNavState.Detail(screen.article)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalAnimationApi::class)
@Composable
private fun ReportsSection(
    sectionKey: String,
    navState: MutableState<ScreenNavState>,
    uiState: ArticleState,
    listState: LazyListState,
    coroutineScope: CoroutineScope,
    sharedTransitionScope: SharedTransitionScope,
    viewModel: ReportsViewModel
) {
    AnimatedContent(
        targetState = navState.value,
        label = "${sectionKey}_nav_transition",
        transitionSpec = { fadeIn() togetherWith fadeOut() }
    ) { screen ->
        when (screen) {
            ScreenNavState.List -> {
                ReportsScreen(
                    viewModel = viewModel,
                    listState = listState,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = this@AnimatedContent,
                    onArticleClick = { article ->
                        navState.value = ScreenNavState.Detail(article)
                    }
                )
            }
            is ScreenNavState.Detail -> {
                ArticleDetailScreen(
                    article = screen.article,
                    onBack = {
                        val articles = (uiState as? ArticleState.Success)?.articles
                        val index = articles?.indexOfFirst { it.id == screen.article.id } ?: -1
                        coroutineScope.launch {
                            if (index >= 0) listState.scrollToItem(index)
                            navState.value = ScreenNavState.List
                        }
                    },
                    onReadFullArticle = { url ->
                        navState.value = ScreenNavState.Web(url, screen.article)
                    },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = this@AnimatedContent,
                    "reports"
                )
            }
            is ScreenNavState.Web -> {
                DetailWebViewScreen(
                    url = screen.url,
                    onBack = {
                        navState.value = ScreenNavState.Detail(screen.article)
                    }
                )
            }
        }
    }
}

@Composable
fun BottomBar(
    selectedTab: Tab,
    onTabSelected: (Tab) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == Tab.ARTICLES,
            onClick = { onTabSelected(Tab.ARTICLES) },
            icon = { Icon(Icons.Default.List, contentDescription = "Articulos") },
            label = { Text("Articulos") }
        )
        NavigationBarItem(
            selected = selectedTab == Tab.BLOG,
            onClick = { onTabSelected(Tab.BLOG) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Blog") },
            label = { Text("Blog") }
        )
        NavigationBarItem(
            selected = selectedTab == Tab.REPORTS,
            onClick = { onTabSelected(Tab.REPORTS) },
            icon = { Icon(Icons.Default.Build, contentDescription = "Reportes") },
            label = { Text("Reportes") }
        )
    }
}

private fun handleBack(
    navState: MutableState<ScreenNavState>,
    uiState: ArticleState,
    listState: LazyListState,
    coroutineScope: CoroutineScope
) {
    when (val current = navState.value) {
        is ScreenNavState.Web -> {
            navState.value = ScreenNavState.Detail(current.article)
        }
        is ScreenNavState.Detail -> {
            val articles = (uiState as? ArticleState.Success)?.articles
            val index = articles?.indexOfFirst { it.id == current.article.id } ?: -1
            coroutineScope.launch {
                if (index >= 0) listState.scrollToItem(index)
                navState.value = ScreenNavState.List
            }
        }
        ScreenNavState.List -> Unit
    }
}

enum class Tab {
    ARTICLES,
    BLOG,
    REPORTS
}

