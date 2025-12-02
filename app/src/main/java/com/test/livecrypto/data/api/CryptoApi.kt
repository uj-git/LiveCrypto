package com.test.livecrypto.data.api

import com.test.livecrypto.data.model.crypto_list.CryptoListResponseItem
import com.test.livecrypto.utils.Urls
import retrofit2.http.GET
import retrofit2.http.Query


interface CryptoApi {
    @GET(Urls.GET_COINS)
    suspend fun getCoins(
        @Query("vs_currency") vsCurrency: String = "inr",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false
    ) : List<CryptoListResponseItem>
}