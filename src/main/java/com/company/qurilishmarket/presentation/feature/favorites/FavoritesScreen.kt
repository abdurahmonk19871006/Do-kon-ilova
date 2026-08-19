package com.company.qurilishmarket.presentation.feature.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.company.qurilishmarket.domain.model.AuthState
import com.company.qurilishmarket.presentation.common.EmptyState
import com.company.qurilishmarket.presentation.common.ErrorView
import com.company.qurilishmarket.presentation.common.LoadingIndicator
import com.company.qurilishmarket.presentation.common.ProductGrid
import com.company.qurilishmarket.presentation.common.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onProductClick: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        if (authState is AuthState.LoggedIn) viewModel.load()
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Sevimlilar") }) }) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val auth = authState) {
                AuthState.Loading -> LoadingIndicator()
                AuthState.LoggedOut -> EmptyState(
                    message = "Sevimli mahsulotlaringizni ko'rish uchun tizimga kiring",
                    actionLabel = "Kirish",
                    onAction = onNavigateToLogin
                )
                is AuthState.LoggedIn -> when (val state = uiState) {
                    is UiState.Loading -> LoadingIndicator()
                    is UiState.Error -> ErrorView(message = state.message, onRetry = viewModel::load)
                    is UiState.Empty -> EmptyState(message = "Hali sevimli mahsulot yo'q")
                    is UiState.Success -> ProductGrid(products = state.data, onProductClick = onProductClick)
                }
            }
        }
    }
}
