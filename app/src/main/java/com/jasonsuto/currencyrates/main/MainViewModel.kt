package com.jasonsuto.currencyrates.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jasonsuto.currencyrates.data.models.Rates
import com.jasonsuto.currencyrates.util.DispatcherProvider
import com.jasonsuto.currencyrates.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.full.memberProperties

@HiltViewModel
class MainViewModel @Inject constructor( //@ViewModelInject is deprecated, replaced with @Inject
    private val repository: MainRepository,
    private val dispatchers: DispatcherProvider
) :
    ViewModel() {

    sealed class CurrencyEvent {
        class Success(val resultText: String) : CurrencyEvent()
        class Failure(val errorText: String) : CurrencyEvent()
        object Loading : CurrencyEvent()
        object Empty : CurrencyEvent()
    }

    private val _conversion = MutableStateFlow<CurrencyEvent>(CurrencyEvent.Empty)
    val conversion: StateFlow<CurrencyEvent> = _conversion

    fun convert(
        amountStr: String,
        fromCurrency: String,
        toCurrency: String,
        apiKey: String
    ) {
        val fromAmount = amountStr.toFloatOrNull()
        if (fromAmount == null) {
            _conversion.value = CurrencyEvent.Failure("Not a valid amount")
            return
        }

        viewModelScope.launch(dispatchers.io) {
            _conversion.value = CurrencyEvent.Loading
            //Hardcoding "EUR" below due to change in free version of api only allowing EUR base currency
            when (val ratesResponse = repository.getRates("EUR", apiKey)) {
                is Resource.Error -> {
                    _conversion.value = CurrencyEvent.Failure(ratesResponse.message!!)
                }

                is Resource.Success -> {
                    val rates = ratesResponse.data!!.rates
                    val fromRate = getRateForCurrencyUsingReflection(fromCurrency, rates)
                    val toRate = getRateForCurrencyUsingReflection(toCurrency, rates)
                    val resultRate = toRate?.div(fromRate!!)

                    if (fromRate == null) {
                        _conversion.value = CurrencyEvent.Failure("Unexpected error")
                    } else {
                        val convertedCurrency = fromAmount * resultRate!!
                        val formattedStartingValue = String.format("%.2f", fromAmount)
                        val formattedEndingValue = String.format("%.2f", convertedCurrency)
                        _conversion.value =
                            CurrencyEvent.Success("$formattedStartingValue $fromCurrency = $formattedEndingValue $toCurrency")
                    }
                }
            }
        }
    }

    //This was my old way of getting the currency rate before I redid it using reflection (see below)
    private fun getRateForCurrencyOld(currency: String, rates: Rates) = when (currency) {
        "CAD" -> rates.CAD
        "USD" -> rates.USD
        "HKD" -> rates.HKD
        "ISK" -> rates.ISK
        "EUR" -> rates.EUR
        //... 100+ currencies
        else -> null
    }

    private fun getRateForCurrencyUsingReflection(currency: String, rates: Rates): Double? {
        val property = Rates::class.memberProperties
            .firstOrNull { it.name == currency }
        return property?.get(rates) as? Double
    }
}
