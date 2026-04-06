package com.example.spacenewstestml.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ArticleResponse (
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("summary") val summary: String,
    @SerializedName("url") val url: String,
    @SerializedName("news_site") val newsSite: String,
    @SerializedName("published_at") val publishedAt: String,
    @SerializedName("authors") val authors: List<AuthorResponse>
)