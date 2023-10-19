package com.jasonsuto.currencyrates.main

import com.jasonsuto.currencyrates.data.models.RatesResponse
import com.jasonsuto.currencyrates.util.Resource

interface MainRepository { //separate to expedite testing without network requests.

    suspend fun getRates(base:String, akiKey: String): Resource<RatesResponse>

}
