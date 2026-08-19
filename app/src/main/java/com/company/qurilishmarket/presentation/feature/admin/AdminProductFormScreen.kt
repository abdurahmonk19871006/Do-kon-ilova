package com.company.qurilishmarket.presentation.feature.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.company.qurilishmarket.domain.model.Category
import com.company.qurilishmarket.domain.model.MeasureUnit
import com.company.qurilishmarket.presentation.common.LoadingIndicator
import com.company.qurilishmarket.presentation.common.displayName
import com.company.qurilishmarket.presentation.theme.ErrorRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductFormScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AdminProductFormViewModel = hiltViewModel()
) {
    val form by viewModel.form.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.saved.collect { onSaved() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.isEditing) "Mahsulotni tahrirlash" else "Yangi mahsulot") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                LoadingIndicator()
            } else {
                ProductForm(
                    form = form,
                    categories = categories,
                    isEditing = viewModel.isEditing,
                    isSaving = isSaving,
                    error = error,
                    onFormChange = viewModel::updateForm,
                    onSave = viewModel::save,
                    onDeactivate = viewModel::deactivate
                )
            }
        }
    }
}

@Composable
private fun ProductForm(
    form: ProductFormState,
    categories: List<Category>,
    isEditing: Boolean,
    isSaving: Boolean,
    error: String?,
    onFormChange: ((ProductFormState) -> ProductFormState) -> Unit,
    onSave: () -> Unit,
    onDeactivate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = form.name,
            onValueChange = { v -> onFormChange { it.copy(name = v) } },
            label = { Text("Nomi") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = form.code,
            onValueChange = { v -> onFormChange { it.copy(code = v) } },
            label = { Text("Mahsulot kodi") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        CategoryDropdown(
            categories = categories,
            selectedId = form.categoryId,
            onSelect = { id -> onFormChange { it.copy(categoryId = id) } }
        )
        Spacer(Modifier.height(12.dp))

        UnitDropdown(
            selected = form.unit,
            onSelect = { unit -> onFormChange { it.copy(unit = unit) } }
        )
        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = form.price,
                onValueChange = { v -> onFormChange { it.copy(price = v.filter(Char::isDigit)) } },
                label = { Text("Narx (so'm)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = form.oldPrice,
                onValueChange = { v -> onFormChange { it.copy(oldPrice = v.filter(Char::isDigit)) } },
                label = { Text("Eski narx (ixtiyoriy)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = form.stock,
            onValueChange = { v -> onFormChange { it.copy(stock = v.filter(Char::isDigit)) } },
            label = { Text("Qoldiq") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = form.shortDescription,
            onValueChange = { v -> onFormChange { it.copy(shortDescription = v) } },
            label = { Text("Qisqa tavsif") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = form.fullDescription,
            onValueChange = { v -> onFormChange { it.copy(fullDescription = v) } },
            label = { Text("Batafsil tavsif") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = form.imagesCsv,
            onValueChange = { v -> onFormChange { it.copy(imagesCsv = v) } },
            label = { Text("Rasm URL'lari (vergul bilan)") },
            supportingText = { Text("Masalan: https://.../1.jpg, https://.../2.jpg") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        SwitchRow("Mashhur", form.isPopular) { v -> onFormChange { it.copy(isPopular = v) } }
        SwitchRow("Yangi", form.isNew) { v -> onFormChange { it.copy(isNew = v) } }
        SwitchRow("Faol (sotuvda)", form.isActive) { v -> onFormChange { it.copy(isActive = v) } }

        error?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onSave,
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text(if (isEditing) "Saqlash" else "Qo'shish")
        }

        if (isEditing) {
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = onDeactivate,
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("O'chirish (sotuvdan chiqarish)", color = ErrorRed)
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

// DIQQAT: `menuAnchor(...)` imzosi Material3'ning turli versiyalarida farq qiladi (ba'zan
// argumentsiz `menuAnchor()`, ba'zan `MenuAnchorType` bilan). Compile qilishda xato chiqsa,
// Android Studio "Quick Fix" taklifi to'g'ri variantni ko'rsatadi.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(categories: List<Category>, selectedId: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = categories.firstOrNull { it.id == selectedId }?.name ?: ""

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Kategoriya") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuDefaults.OutlinedTextFieldType, true)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = { onSelect(category.id); expanded = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitDropdown(selected: MeasureUnit, onSelect: (MeasureUnit) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected.displayName(),
            onValueChange = {},
            readOnly = true,
            label = { Text("O'lchov birligi") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuDefaults.OutlinedTextFieldType, true)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            MeasureUnit.entries.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(unit.displayName()) },
                    onClick = { onSelect(unit); expanded = false }
                )
            }
        }
    }
}
