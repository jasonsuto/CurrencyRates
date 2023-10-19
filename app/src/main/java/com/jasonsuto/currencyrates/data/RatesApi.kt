package com.jasonsuto.currencyrates.data
import com.jasonsuto.currencyrates.data.models.RatesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RatesApi {

    @GET("latest")
    suspend fun getRates(
        @Query("base") base: String,
        @Query("access_key") apiKey: String
    ): Response<RatesResponse>

}