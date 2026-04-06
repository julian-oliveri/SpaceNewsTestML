package com.example.spacenewstestml.data.remote.dto

import com.example.spacenewstestml.data.remote.dto.ArticleResponse
import com.google.gson.annotations.SerializedName

data class ArticleListResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("previous") val previous: String?,
    @SerializedName("results") val results: List<ArticleResponse>
)