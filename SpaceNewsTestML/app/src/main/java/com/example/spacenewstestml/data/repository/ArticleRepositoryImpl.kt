package com.example.spacenewstestml.data.repository

import android.util.Log
import com.example.spacenewstestml.data.models.Article
import com.example.spacenewstestml.data.models.toArticleModel
import com.example.spacenewstestml.data.remote.SpaceNewsWebService
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class ArticleRepositoryImpl @Inject constructor(
    private val api: SpaceNewsWebService
) : ArticleRepository {

    override suspend fun getArticles(limit: Int, offset: Int, search: String?): Result<List<Article>> {
        return try {
            val response = api.getArticles(limit = limit, offset = offset, search = search)
            Result.success(response.results.map { it.toArticleModel() })
        } catch (e: HttpException) {
            Log.e("ArticleRepository", "HTTP error: ${e.code()}", e)
            Result.failure(e)
        } catch (e: IOException) {
            Log.e("ArticleRepository", "Network error", e)
            Result.failure(e)
        }
    }

    // No lo usamos, agarro la data de la lista y me ahorro un llamado
    // ver razon completa en SpaceNewsWebService
    override suspend fun getArticleById(id: Int): Result<Article> {
        return try {
            Result.success(api.getArticleById(id).toArticleModel())
        } catch (e: HttpException) {
            Log.e("ArticleRepository", "HTTP error: ${e.code()}", e)
            Result.failure(e)
        } catch (e: IOException) {
            Log.e("ArticleRepository", "Network error", e)
            Result.failure(e)
        }
    }


    // Devuelven los mismos modelos, porque esta fuera de scope y es para mejora
    // usamos el mismo modelo (lo correcto es tener 3 distintos)
    // y lo tenemos en el mismo repository
    override suspend fun getBlogs(limit: Int, offset: Int, search: String?): Result<List<Article>> {
        return try {
            val response = api.getBlogs(limit = limit, offset = offset, search = search)
            Result.success(response.results.map { it.toArticleModel() })
        } catch (e: HttpException) {
            Log.e("ArticleRepository", "HTTP error: ${e.code()}", e)
            Result.failure(e)
        } catch (e: IOException) {
            Log.e("ArticleRepository", "Network error", e)
            Result.failure(e)
        }
    }


    override suspend fun getReports(limit: Int, offset: Int, search: String?): Result<List<Article>> {
        return try {
            val response = api.getReports(limit = limit, offset = offset, search = search)
            Result.success(response.results.map { it.toArticleModel() })
        } catch (e: HttpException) {
            Log.e("ArticleRepository", "HTTP error: ${e.code()}", e)
            Result.failure(e)
        } catch (e: IOException) {
            Log.e("ArticleRepository", "Network error", e)
            Result.failure(e)
        }
    }


}