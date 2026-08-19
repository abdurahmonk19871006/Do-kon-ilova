package com.company.qurilishmarket.presentation.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.company.qurilishmarket.domain.model.AuthState
import com.company.qurilishmarket.presentation.theme.TextSecondary

private const val ADMIN_TRIGGER_TAP_COUNT = 7

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToAdmin: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    var tapCount by remember { mutableIntStateOf(0) }

    Scaffold(topBar = { TopAppBar(title = { Text("Sozlamalar") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Hozircha statik — til/bildirishnoma sozlamalari keyingi bosqichda funksional bo'ladi (§10)
            ListItem(headlineContent = { Text("Til") }, supportingContent = { Text("O'zbekcha") })
            HorizontalDivider()
            ListItem(headlineContent = { Text("Bildirishnomalar") }, supportingContent = { Text("Yoqilgan") })
            HorizontalDivider()

            if (authState is AuthState.LoggedIn) {
                Spacer(Modifier.height(24.dp))
                OutlinedButton(
                    onClick = viewModel::signOut,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Text("Chiqish")
                }
            }

            Spacer(Modifier.weight(1f, fill = true))

            // §11: yashirin admin kirish — Android'ning o'z "Developer options"ini ochish
            // naqshiga o'xshab. Bu FAQAT qulaylik, himoya emas — haqiqiy tekshiruv
            // AdminLoginScreen'da (authRepository.isCurrentUserAdmin(), va undan ham
            // muhimi — Supabase Security Rules'da, §6).
            Text(
                "QurilishMarket v1.0.0",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        tapCount++
                        if (tapCount >= ADMIN_TRIGGER_TAP_COUNT) {
                            tapCount = 0
                            onNavigateToAdmin()
                        }
                    }
                    .padding(16.dp)
            )
        }
    }
}
