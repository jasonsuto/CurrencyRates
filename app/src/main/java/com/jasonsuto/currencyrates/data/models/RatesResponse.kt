package com.jasonsuto.currencyrates.data.models

data class RatesResponse(
    val base: String,
    val date: String,
    val rates: Rates,
    val success: Boolean,
    val timestamp: Int
)