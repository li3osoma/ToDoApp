package com.example.todoapp.di

import android.content.Context
import com.example.todoapp.datasource.network.api.AuthorizationInterceptor
import com.example.todoapp.datasource.network.api.ToDoApi
import com.example.todoapp.datasource.network.connection.NetworkConnectionObserver
import com.example.todoapp.utils.BASE_URL
import com.example.todoapp.utils.TOKEN
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {
    @Provides
    @AppScope
    fun provideApi(): ToDoApi =
        provideRetrofitClient().create(ToDoApi::class.java)

    @Provides
    @AppScope
    fun provideRetrofitClient(): Retrofit {
        return Retrofit.Builder()
            .client(provideHttpClient())
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @AppScope
    fun provideHttpClient(): OkHttpClient =
        OkHttpClient.Builder().addInterceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                .addHeader("Authorization", TOKEN)
                .build()
            chain.proceed(newRequest)
        }.addInterceptor(getInterceptor()).build()

    @Provides
    @AppScope
    fun getInterceptor(): Interceptor {
        return AuthorizationInterceptor()
    }

    @Provides
    @AppScope
    fun provideConnectionObserver(context: Context): NetworkConnectionObserver =
        NetworkConnectionObserver(context)
}