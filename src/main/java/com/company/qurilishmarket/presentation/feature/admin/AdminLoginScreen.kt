package com.company.qurilishmarket.presentation.feature.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.company.qurilishmarket.domain.model.AuthState
import com.company.qurilishmarket.presentation.common.LoadingIndicator
import com.company.qurilishmarket.presentation.theme.ErrorRed
import com.company.qurilishmarket.presentation.theme.TextSecondary

/**
 * §11: Bu ekran — "yashirin" trigger orqali kelinadi, lekin haqiqiy chegara shu yerdagi
 * isCurrentUserAdmin() tekshiruvi VA undan ham muhimi, Supabase Security Rules (§6). Login
 * kerak bo'lsa oddiy Login ekraniga yuboradi; u yerdan popBackStack bilan qaytilganda,
 * quyidagi LaunchedEffect(authState) o'zi qayta tekshiradi — Checkout'dagi auth gate bilan
 * bir xil naqsh.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginScreen(
    onAccessGranted: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onBack: () -> Unit,
    viewModel: AdminLoginViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val isAdmin by viewModel.isAdmin.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        if (authState is AuthState.LoggedIn) viewModel.checkAccess()
    }

    LaunchedEffect(isAdmin) {
        if (isAdmin == true) onAccessGranted()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
            when {
                authState is AuthState.Loading -> LoadingIndicator()
                authState is AuthState.LoggedOut -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        "Admin panelga kirish uchun avval tizimga kiring",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onNavigateToLogin) { Text("Kirish") }
                }
                isAdmin == false -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = ErrorRed)
                    Text("Sizda admin huquqi yo'q", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Bu hisobda administrator ruxsati yo'q",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
                else -> LoadingIndicator() // isAdmin == null — tekshirilmoqda
            }
        }
    }
}
