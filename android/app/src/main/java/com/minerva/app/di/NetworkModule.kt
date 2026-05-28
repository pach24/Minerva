package com.minerva.app.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.minerva.app.BuildConfig
import com.minerva.app.data.local.SessionDataStore
import com.minerva.app.data.remote.AuthInterceptor
import com.minerva.app.data.remote.MinervaApi
import com.minerva.app.data.remote.SupabaseAuthApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(sessionDataStore: SessionDataStore): AuthInterceptor =
        AuthInterceptor(sessionDataStore)

    @Provides
    @Singleton
    @Named("minerva")
    fun provideMinervaOkHttp(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides
    @Singleton
    @Named("supabase")
    fun provideSupabaseOkHttp(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides
    @Singleton
    fun provideMinervaApi(json: Json, @Named("minerva") okHttp: OkHttpClient): MinervaApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.MINERVA_BASE_URL.trimEnd('/') + "/")
            .client(okHttp)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(MinervaApi::class.java)

    @Provides
    @Singleton
    fun provideSupabaseAuthApi(json: Json, @Named("supabase") okHttp: OkHttpClient): SupabaseAuthApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.SUPABASE_URL.trimEnd('/') + "/")
            .client(okHttp)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(SupabaseAuthApi::class.java)

    @Provides
    @Named("supabase_anon_key")
    fun provideSupabaseAnonKey(): String = BuildConfig.SUPABASE_ANON_KEY
}
