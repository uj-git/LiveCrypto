package com.test.livecrypto.presentation.listScreen.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.livecrypto.data.repository.CryptoListRepo
import com.test.livecrypto.data.model.crypto_list.CryptoListResponseItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CryptoUIState {
    object Loading : CryptoUIState()
    data class Success(val coins: List<CryptoListResponseItem>) : CryptoUIState()
    data class Error(val message: String) : CryptoUIState()
}

@HiltViewModel
class CryptoListViewModel @Inject constructor(private val cryptoListRepo : CryptoListRepo) : ViewModel() {

    private var _cryptoState : MutableStateFlow<CryptoUIState?> = MutableStateFlow(CryptoUIState.Loading)
    val cryptoList : StateFlow<CryptoUIState?> get() = _cryptoState

    private val refreshInterval = 10_000L

    init {
        startAutoRefresh()
    }

    fun startAutoRefresh() {
        viewModelScope.launch {
            tickerFlow(refreshInterval).collect {
                loadCrypto()
            }
        }
    }

    fun tickerFlow(period: Long) = flow {
        while (true) {
            emit(Unit)
            delay(period)
        }
    }

    fun loadCrypto() {
        _cryptoState.value = CryptoUIState.Loading

        viewModelScope.launch {
            try {
                val coins = cryptoListRepo.getCoins()
                _cryptoState.value = CryptoUIState.Success(coins)
            } catch (e : Exception) {
                _cryptoState.value = CryptoUIState.Error(e.message.toString())
            }
        }
    }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val result = cryptoListRepo.getCoins()
                _cryptoState.value = CryptoUIState.Success(result)
            } catch (e: Exception) {
                _cryptoState.value = CryptoUIState.Error(e.message ?: "Failed")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

}