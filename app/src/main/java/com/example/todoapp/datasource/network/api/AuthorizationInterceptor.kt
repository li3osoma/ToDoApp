package com.example.todoapp.datasource.network.api

import com.example.todoapp.utils.TOKEN
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