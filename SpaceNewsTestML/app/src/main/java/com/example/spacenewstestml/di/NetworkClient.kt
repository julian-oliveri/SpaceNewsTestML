package com.example.spacenewstestml.di

import com.example.spacenewstestml.data.remote.ApiConstants
import com.example.spacenewstestml.data.remote.SpaceNewsWebService
import com.example.spacenewstestml.data.repository.ArticleRepository
import com.example.spacenewstestml.data.repository.ArticleRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkClient {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(ApiConstants.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideSpaceFlightApi(retrofit: Retrofit): SpaceNewsWebService =
        retrofit.create(SpaceNewsWebService::class.java)

    @Provides
    @Singleton
    fun provideArticleRepository(api: SpaceNewsWebService): ArticleRepository =
        ArticleRepositoryImpl(api)
}