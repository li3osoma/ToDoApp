package com.example.todoapp.di

import android.content.Context
import android.content.SharedPreferences
import com.example.todoapp.datasource.network.api.ToDoApi
import com.example.todoapp.datasource.network.connection.NetworkConnectionObserver
import com.example.todoapp.utils.BASE_URL
import com.example.todoapp.utils.TOKEN
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {
    @Provides
    @Singleton
    fun provideApi(sharedPreferences: SharedPreferences): ToDoApi =
        provideRetrofitClient(sharedPreferences).create(ToDoApi::class.java)

    @Provides
    @Singleton
    fun provideRetrofitClient(sharedPreferencesHelper: SharedPreferences): Retrofit {
        System.setProperty("http.keepAlive", "false")
        return Retrofit.Builder()
            .client(provideHttpClient(sharedPreferencesHelper))
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @Singleton
    fun provideHttpClient(sharedPreferencesHelper: SharedPreferences): OkHttpClient =
        OkHttpClient.Builder().addInterceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                .addHeader("Authorization", TOKEN)
                .build()
            chain.proceed(newRequest)
        }.addInterceptor(getInterceptor()).build()

    @Provides
    fun getInterceptor(): Interceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return interceptor
    }

    @Provides
    @Singleton
    fun provideConnectionObserver(context: Context): NetworkConnectionObserver =
        NetworkConnectionObserver(context)
}