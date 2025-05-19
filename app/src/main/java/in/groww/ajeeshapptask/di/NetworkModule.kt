package `in`.groww.ajeeshapptask.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import `in`.groww.ajeeshapptask.data.local.RecentSearchManager
import `in`.groww.ajeeshapptask.data.local.ThemePreferenceManager
import `in`.groww.ajeeshapptask.data.remote.service.ApiService
import `in`.groww.ajeeshapptask.data.repository.ApiRepository
import `in`.groww.ajeeshapptask.data.repository.ApiRepositoryImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://www.alphavantage.co/"
    private const val TIMEOUT = 30L

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .addInterceptor { chain ->
                val response = chain.proceed(chain.request())
                if (response.code == 429) {
                    throw RateLimitExceededException()
                }
                response
            }
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
        .build()

    @Provides
    @Singleton
    fun provideAlphaVantageApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}

class RateLimitExceededException : Exception("API rate limit exceeded")

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindStockRepository(impl: ApiRepositoryImpl): ApiRepository

}

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {
    @Provides
    @Singleton
    fun provideRecentSearchManager(@ApplicationContext context: Context) =
        RecentSearchManager(context)
}

@Module
@InstallIn(SingletonComponent::class)
object ThemeModule {
    @Provides
    @Singleton
    fun provideThemePreferenceManager(@ApplicationContext context: Context): ThemePreferenceManager {
        return ThemePreferenceManager(context)
    }
}

