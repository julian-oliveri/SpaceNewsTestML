package com.example.spacenewstestml.data.repository

import com.example.spacenewstestml.data.models.Article

interface ArticleRepository {
    suspend fun getArticles(limit: Int = 10, offset: Int = 0, search: String? = null): Result<List<Article>>

    // No se usa, la razon esta en SpaceNewsWebService
    suspend fun getArticleById(id: Int): Result<Article>

    suspend fun getBlogs(limit: Int = 10, offset: Int = 0, search: String? = null): Result<List<Article>>

    suspend fun getReports(limit: Int = 10, offset: Int = 0, search: String? = null): Result<List<Article>>

}