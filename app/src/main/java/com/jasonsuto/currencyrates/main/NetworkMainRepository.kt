package com.jasonsuto.currencyrates.main

import com.jasonsuto.currencyrates.data.RatesApi
import com.jasonsuto.currencyrates.data.models.RatesResponse
import com.jasonsuto.currencyrates.util.Resource
import javax.inject.Inject

class NetworkMainRepository @Inject constructor(
    private val api: RatesApi
) : MainRepository {
    override suspend fun getRates(base: String, apiKey:String): Resource<RatesResponse> {
        return try {
            val response =
                api.getRates(base, apiKey) //move api key somewhere else
            val result = response.body()

            response.errorBody()

            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                println("Failure 5! $response.message()")
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error Occurred!")
        }
    }
}
