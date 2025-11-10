package com.wasfa.doctor.network

import android.util.Log
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit


object NetworkClient {

   // private const val BASE_URL = "https://apixrx.com/api/v2/"

   private const val BASE_URL = "https://stagging.apixrx.com/api/v2/"
     const val IMAGE_URL = "https://stagging.apixrx.com/api/v2/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Set your desired logging level
    }

    private val timeoutInterceptor = object : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            try {
                return chain.proceed(chain.request())
            } catch (e: SocketTimeoutException) {
                Log.e("NetworkClient", "Socket Timeout Exception: ${e.message}")
                throw TimeoutException("Request Timeout", e)
            } catch (e: Exception) {
                Log.e("NetworkClient", "Unexpected Exception: ${e.message}")
                throw NetworkException("Unexpected error during network call", e)
            }
        }
    }

    class TimeoutException(message: String, cause: Throwable) : IOException(message, cause)
    class NetworkException(message: String, cause: Throwable) : IOException(message, cause)

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(timeoutInterceptor)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Set the OkHttpClient with the logging interceptor
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
