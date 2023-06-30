package com.example.todoapp.repository

import com.example.todoapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

abstract class BaseRepository(){

    suspend fun <T> safeApiCall(apiToBeCalled: suspend () -> Response<T>,): Resource<T> {
        // Returning api response
        // wrapped in Resource class

        try {

            // Here we are calling api lambda
            // function that will return response
            // wrapped in Retrofit's Response class
            val response: Response<T> = apiToBeCalled()

            if (response.isSuccessful) {
                // In case of success response we
                // are returning Resource.Success object
                // by passing our data in it.
                return Resource.Success(data = response.body()!!)
            } else {
                // parsing api's own custom json error
                // response in ExampleErrorResponse pojo
                val errorMess = response.message()
                // Simply returning api's own failure message
                return Resource.Error("Something went wrong: $errorMess")
            }

        } catch (e: HttpException) {
            // Returning HttpException's message
            // wrapped in Resource.Error
            return Resource.Error("Something went wrong")
        } catch (e: IOException) {
            // Returning no internet message
            // wrapped in Resource.Error
            return Resource.Error("Please check your network connection")
        } catch (e: Exception) {
            // Returning 'Someting went wrong' in case
            // of unknown error wrapped in Resource.Error
            return Resource.Error("Something went wrong")
        }

    }
}