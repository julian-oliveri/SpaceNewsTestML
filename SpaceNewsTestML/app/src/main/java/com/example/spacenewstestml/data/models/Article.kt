package com.example.spacenewstestml.data.models

import com.example.spacenewstestml.data.remote.dto.ArticleResponse
import java.text.SimpleDateFormat
import java.util.Locale

data class Article(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val summary: String,
    val url: String,
    val newsSite: String,
    val publishedAt: String,
    val authors: List<Author>
)

fun ArticleResponse.toArticleModel() = Article(
    id = id,
    title = title,
    imageUrl = imageUrl,
    summary = summary,
    url = url,
    newsSite = newsSite,
    publishedAt = publishedAt.formatDate(),
    authors = authors.map { it.toAuthorModel() }
)

// Parse de la fecha a una mas "user friendly"
private fun String.formatDate(): String = try {
    val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
    val output = SimpleDateFormat("MMMM d, yyyy", Locale.US)
    output.format(input.parse(this)!!)
} catch (e: Exception) {
    this
}
