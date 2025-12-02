package com.test.livecrypto.presentation.listScreen.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.compose.AsyncImage
import com.test.livecrypto.data.model.crypto_list.CryptoListResponseItem
import com.test.livecrypto.presentation.listScreen.viewmodel.CryptoListViewModel
import com.test.livecrypto.presentation.listScreen.viewmodel.CryptoUIState

@Composable
fun CryptoList(
    modifier: Modifier,
    cryptoListViewModel: CryptoListViewModel = hiltViewModel()
) {

    val state = cryptoListViewModel.cryptoList.collectAsState().value
    val ctx = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            cryptoListViewModel.startAutoRefresh()
        }
    }

    when(state) {
        is CryptoUIState.Loading -> {
            Box(modifier = modifier.fillMaxSize().background(color = Color.White), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is CryptoUIState.Error -> {
            Box(modifier = modifier.fillMaxSize().background(color = Color.Black), contentAlignment = Alignment.Center) {
                Text(text = state.message, color = Color.White)
            }
        }

        is CryptoUIState.Success -> {
            CryptoListSuccessScreen(modifier = modifier, cryptoItems = state.coins, viewModel = cryptoListViewModel)
        }

        null -> {

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = { Text(text = "Crypto List") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun CryptoListScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar() },
    ) { paddingValues ->
        CryptoList(
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun CryptoListItem(
    imageUrl : String,
    name : String,
    price : String
) {
    Card {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
            )

            Text(name)
            Text("₹${price}")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoListSuccessScreen(
    modifier: Modifier = Modifier,
    cryptoItems: List<CryptoListResponseItem>,
    viewModel: CryptoListViewModel
) {

    val isRefreshing = viewModel.isRefreshing.collectAsState().value
    val state = rememberPullToRefreshState()

    // If refreshing finished → stop indicator
    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) {
            state.animateToHidden()
        }
    }

    PullToRefreshBox(
        modifier = modifier.fillMaxSize(),
        state = state,
        isRefreshing = isRefreshing,
        onRefresh = {
            viewModel.refresh()
        }
    ) {

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(items = cryptoItems, key = { it.id!! }) { item ->
                CryptoListItem(
                    imageUrl = item.image.toString(),
                    name = item.name.toString(),
                    price = item.currentPrice.toString(),
                )
            }
        }
    }
}

