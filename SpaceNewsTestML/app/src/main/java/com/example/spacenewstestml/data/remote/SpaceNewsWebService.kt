package com.example.spacenewstestml.data.remote

import com.example.spacenewstestml.data.remote.dto.ArticleListResponse
import com.example.spacenewstestml.data.remote.dto.ArticleResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpaceNewsWebService  {

    @GET("articles/")
    suspend fun getArticles(
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0,
        @Query("search") search: String? = null
    ): ArticleListResponse

    // Este endpoint en teoria devuelve el detalle de un articulo en particular
    // pero la response tiene exactamente la misma data que el getArticles.
    // lo agrego para que este "completo" y por si en un futuro cambia
    // pero agarro la data del articulo de la lista y me ahorro una llamada a la api
    @GET("articles/{id}/")
    suspend fun getArticleById(@Path("id") id: Int): ArticleResponse

    @GET("blogs/")
    suspend fun getBlogs(
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0,
        @Query("search") search: String? = null
    ): ArticleListResponse

    @GET("reports/")
    suspend fun getReports(
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0,
        @Query("search") search: String? = null
    ): ArticleListResponse
}