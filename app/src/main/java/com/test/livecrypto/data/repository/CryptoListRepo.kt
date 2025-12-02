package com.test.livecrypto.data.repository

import com.test.livecrypto.data.api.CryptoApi
import com.test.livecrypto.data.model.crypto_list.CryptoListResponseItem
import javax.inject.Inject


class CryptoListRepo @Inject constructor( private val cryptoApi: CryptoApi) {
    suspend fun getCoins() : List<CryptoListResponseItem> {
        return cryptoApi.getCoins()
    }
}