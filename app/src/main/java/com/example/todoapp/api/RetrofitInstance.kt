package com.example.todoapp.api

import com.example.todoapp.utils.BASE_URL
import com.example.todoapp.utils.TOKEN
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("Authorization", "Bearer $TOKEN")
            .build()
        return chain.proceed(request)
    }
}

class RetrofitInstance {

    companion object {

        private val retrofit by lazy {
            val tokenInterceptor = AuthorizationInterceptor()

            val httpClient = OkHttpClient.Builder()
                .addInterceptor(tokenInterceptor)
                .build()
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api by lazy {
            retrofit.create(ToDoApi::class.java)

        }
    }
}