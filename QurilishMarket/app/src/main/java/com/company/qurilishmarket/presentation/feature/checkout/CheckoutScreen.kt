package com.company.qurilishmarket.presentation.feature.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.company.qurilishmarket.domain.model.AuthState
import com.company.qurilishmarket.domain.model.CartItem
import com.company.qurilishmarket.domain.model.DeliveryType
import com.company.qurilishmarket.domain.model.PaymentMethod
import com.company.qurilishmarket.presentation.common.LoadingIndicator
import com.company.qurilishmarket.presentation.common.SummaryRow
import com.company.qurilishmarket.presentation.common.displayName
import com.company.qurilishmarket.presentation.common.toSomText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onOrderPlaced: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val deliveryType by viewModel.deliveryType.collectAsStateWithLifecycle()
    val address by viewModel.address.collectAsStateWithLifecycle()
    val paymentMethod by viewModel.paymentMethod.collectAsStateWithLifecycle()
    val comment by viewModel.comment.collectAsStateWithLifecycle()
    val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    // Login qilinmagan bo'lsa — Login ekraniga o'tadi, u yerdan muvaffaqiyatli kirgach
    // popBackStack() shu ekranga qaytaradi, va authState reaktiv ravishda yangilanadi (§11)
    LaunchedEffect(authState) {
        if (authState is AuthState.LoggedOut) onNavigateToLogin()
    }

    LaunchedEffect(Unit) {
        viewModel.orderPlaced.collect { orderId -> onOrderPlaced(orderId) }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Buyurtma berish") }) }) { padding ->
        when (authState) {
            AuthState.Loading, AuthState.LoggedOut -> LoadingIndicator(modifier = Modifier.padding(padding))
            is AuthState.LoggedIn -> CheckoutContent(
                modifier = Modifier.padding(padding),
                cartItems = cartItems,
                deliveryType = deliveryType,
                address = address,
                paymentMethod = paymentMethod,
                comment = comment,
                deliveryFee = viewModel.deliveryFee,
                isSubmitting = isSubmitting,
                error = error,
                onDeliveryTypeChange = viewModel::setDeliveryType,
                onAddressChange = viewModel::setAddress,
                onPaymentMethodChange = viewModel::setPaymentMethod,
                onCommentChange = viewModel::setComment,
                onSubmit = viewModel::submitOrder
            )
        }
    }
}

@Composable
private fun CheckoutContent(
    modifier: Modifier = Modifier,
    cartItems: List<CartItem>,
    deliveryType: DeliveryType,
    address: String,
    paymentMethod: PaymentMethod,
    comment: String,
    deliveryFee: Long,
    isSubmitting: Boolean,
    error: String?,
    onDeliveryTypeChange: (DeliveryType) -> Unit,
    onAddressChange: (String) -> Unit,
    onPaymentMethodChange: (PaymentMethod) -> Unit,
    onCommentChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val subtotal = cartItems.sumOf { it.lineTotal }
    val total = subtotal + deliveryFee

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Yetkazib berish turi", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DeliveryType.entries.forEach { type ->
                FilterChip(
                    selected = deliveryType == type,
                    onClick = { onDeliveryTypeChange(type) },
                    label = { Text(type.displayName()) }
                )
            }
        }

        if (deliveryType == DeliveryType.YETKAZIB_BERISH) {
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = address,
                onValueChange = onAddressChange,
                label = { Text("Yetkazib berish manzili") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(24.dp))
        Text("To'lov usuli", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Column {
            PaymentMethod.entries.forEach { method ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(selected = paymentMethod == method, onClick = { onPaymentMethodChange(method) })
                        .padding(vertical = 8.dp)
                ) {
                    RadioButton(selected = paymentMethod == method, onClick = { onPaymentMethodChange(method) })
                    Spacer(Modifier.width(8.dp))
                    Text(method.displayName())
                }
            }
        }
        // Payme/Click tanlansa haqiqiy to'lov WebView orqali ochiladi — bu keyingi
        // bosqichda, to'lov integratsiyasini ulaganda qo'shiladi (§11'da qayd etilgan)

        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = comment,
            onValueChange = onCommentChange,
            label = { Text("Izoh (ixtiyoriy)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(12.dp))
        SummaryRow("Mahsulotlar", subtotal.toSomText())
        SummaryRow("Yetkazib berish", if (deliveryFee == 0L) "Bepul" else deliveryFee.toSomText())
        Spacer(Modifier.height(8.dp))
        SummaryRow("Jami", total.toSomText(), emphasized = true)

        error?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onSubmit,
            enabled = !isSubmitting && cartItems.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(52.dp) // §4: min 52dp
        ) {
            if (isSubmitting) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
            else Text("Buyurtmani tasdiqlash")
        }
    }
}
