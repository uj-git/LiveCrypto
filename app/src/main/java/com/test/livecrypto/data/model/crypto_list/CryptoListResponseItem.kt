package com.test.livecrypto.data.model.crypto_list

import com.google.gson.annotations.SerializedName

data class CryptoListResponseItem(
    @SerializedName("current_price")
    val currentPrice: Double?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("name")
    val name: String?
)