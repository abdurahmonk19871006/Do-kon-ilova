package com.company.qurilishmarket.presentation.feature.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.company.qurilishmarket.domain.model.AuthState
import com.company.qurilishmarket.domain.model.User
import com.company.qurilishmarket.presentation.common.EmptyState
import com.company.qurilishmarket.presentation.common.LoadingIndicator
import com.company.qurilishmarket.presentation.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToAddresses: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val isSavingName by viewModel.isSavingName.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        if (authState is AuthState.LoggedIn) viewModel.loadProfile()
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Profil") }) }) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (authState) {
                AuthState.Loading -> LoadingIndicator()
                AuthState.LoggedOut -> EmptyState(
                    message = "Profilingizni ko'rish uchun tizimga kiring",
                    actionLabel = "Kirish",
                    onAction = onNavigateToLogin
                )
                is AuthState.LoggedIn -> ProfileContent(
                    profile = profile,
                    isSavingName = isSavingName,
                    onSaveName = viewModel::saveName,
                    onNavigateToAddresses = onNavigateToAddresses,
                    onNavigateToFavorites = onNavigateToFavorites,
                    onNavigateToSettings = onNavigateToSettings,
                    onSignOut = viewModel::signOut
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    profile: User?,
    isSavingName: Boolean,
    onSaveName: (String) -> Unit,
    onNavigateToAddresses: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onSignOut: () -> Unit
) {
    // profile yuklanganda (yoki saqlangandan keyin qayta yuklanganda) input shu bilan
    // sinxronlanadi — remember(profile?.name) shuni ta'minlaydi.
    var nameInput by remember(profile?.name) { mutableStateOf(profile?.name.orEmpty()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("Ismingiz") },
            placeholder = { Text("Ismingizni kiriting") },
            trailingIcon = {
                if (nameInput.trim() != profile?.name.orEmpty() && nameInput.isNotBlank()) {
                    IconButton(onClick = { onSaveName(nameInput) }, enabled = !isSavingName) {
                        Icon(Icons.Filled.Check, contentDescription = "Saqlash")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(4.dp))
        Text(
            profile?.phone.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(start = 4.dp)
        )

        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        ProfileMenuItem("Manzillarim", Icons.Filled.LocationOn, onNavigateToAddresses)
        HorizontalDivider()
        ProfileMenuItem("Sevimlilar", Icons.Filled.Favorite, onNavigateToFavorites)
        HorizontalDivider()
        ProfileMenuItem("Sozlamalar", Icons.Filled.Settings, onNavigateToSettings)
        HorizontalDivider()

        Spacer(Modifier.height(24.dp))
        OutlinedButton(onClick = onSignOut, modifier = Modifier.fillMaxWidth()) {
            Text("Chiqish")
        }
    }
}

@Composable
private fun ProfileMenuItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(label) },
        leadingContent = { Icon(icon, contentDescription = null) },
        trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
