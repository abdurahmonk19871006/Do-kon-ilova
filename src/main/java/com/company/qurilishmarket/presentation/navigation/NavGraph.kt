package com.company.qurilishmarket.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.company.qurilishmarket.presentation.feature.addresses.AddressesScreen
import com.company.qurilishmarket.presentation.feature.admin.AdminDashboardScreen
import com.company.qurilishmarket.presentation.feature.admin.AdminLoginScreen
import com.company.qurilishmarket.presentation.feature.admin.AdminOrderDetailScreen
import com.company.qurilishmarket.presentation.feature.admin.AdminOrdersScreen
import com.company.qurilishmarket.presentation.feature.admin.AdminProductFormScreen
import com.company.qurilishmarket.presentation.feature.admin.AdminProductsScreen
import com.company.qurilishmarket.presentation.feature.auth.LoginScreen
import com.company.qurilishmarket.presentation.feature.cart.CartScreen
import com.company.qurilishmarket.presentation.feature.categories.CategoriesScreen
import com.company.qurilishmarket.presentation.feature.categoryproducts.CategoryProductsScreen
import com.company.qurilishmarket.presentation.feature.checkout.CheckoutScreen
import com.company.qurilishmarket.presentation.feature.favorites.FavoritesScreen
import com.company.qurilishmarket.presentation.feature.home.HomeScreen
import com.company.qurilishmarket.presentation.feature.orderconfirmation.OrderConfirmationScreen
import com.company.qurilishmarket.presentation.feature.orderdetail.OrderDetailScreen
import com.company.qurilishmarket.presentation.feature.orders.OrdersScreen
import com.company.qurilishmarket.presentation.feature.productdetail.ProductDetailScreen
import com.company.qurilishmarket.presentation.feature.profile.ProfileScreen
import com.company.qurilishmarket.presentation.feature.search.SearchScreen
import com.company.qurilishmarket.presentation.feature.settings.SettingsScreen

@Composable
fun QurilishMarketNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    // Bottom nav faqat 5 ta asosiy bo'limda ko'rinadi — checkout, mahsulot tafsilotlari kabi
    // ichki ekranlarda yashiriladi, xaridni chalg'itmasligi uchun (§3)
    val showBottomBar = bottomNavScreens.any { screen ->
        currentDestination?.hierarchy?.any { it.hasRoute(screen::class) } == true
    }

    Scaffold(
        bottomBar = { if (showBottomBar) QurilishMarketBottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Screen.Home> {
                HomeScreen(
                    onProductClick = { productId -> navController.navigate(Screen.ProductDetail(productId)) },
                    onCategoryClick = { categoryId -> navController.navigate(Screen.CategoryProducts(categoryId)) },
                    onSearchClick = { navController.navigate(Screen.Search) }
                )
            }
            composable<Screen.Categories> {
                CategoriesScreen(
                    onCategoryClick = { categoryId -> navController.navigate(Screen.CategoryProducts(categoryId)) }
                )
            }
            composable<Screen.Cart> {
                CartScreen(
                    onCheckout = { navController.navigate(Screen.Checkout) },
                    onBrowseProducts = { navController.navigate(Screen.Home) }
                )
            }
            composable<Screen.Orders> {
                OrdersScreen(
                    onOrderClick = { orderId -> navController.navigate(Screen.OrderDetail(orderId)) },
                    onNavigateToLogin = { navController.navigate(Screen.Login) }
                )
            }
            composable<Screen.Profile> {
                ProfileScreen(
                    onNavigateToLogin = { navController.navigate(Screen.Login) },
                    onNavigateToAddresses = { navController.navigate(Screen.Addresses) },
                    onNavigateToFavorites = { navController.navigate(Screen.Favorites) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings) }
                )
            }
            composable<Screen.Search> {
                SearchScreen(
                    onBack = { navController.popBackStack() },
                    onProductClick = { productId -> navController.navigate(Screen.ProductDetail(productId)) }
                )
            }
            composable<Screen.Login> {
                LoginScreen(
                    onLoginSuccess = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }

            composable<Screen.CategoryProducts> {
                CategoryProductsScreen(
                    onBack = { navController.popBackStack() },
                    onProductClick = { productId -> navController.navigate(Screen.ProductDetail(productId)) }
                )
            }
            composable<Screen.ProductDetail> {
                ProductDetailScreen(onBack = { navController.popBackStack() })
            }

            composable<Screen.Checkout> {
                CheckoutScreen(
                    onOrderPlaced = { orderId ->
                        navController.navigate(Screen.OrderConfirmation(orderId)) {
                            // Buyurtma tugagach "orqaga" bosilganda Checkout/Cart'ga emas,
                            // Bosh sahifaga qaytsin
                            popUpTo(Screen.Home) { inclusive = false }
                        }
                    },
                    onNavigateToLogin = { navController.navigate(Screen.Login) }
                )
            }
            composable<Screen.OrderConfirmation> { entry ->
                val args = entry.toRoute<Screen.OrderConfirmation>()
                OrderConfirmationScreen(
                    orderId = args.orderId,
                    onBackToHome = {
                        navController.navigate(Screen.Home) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<Screen.OrderDetail> {
                OrderDetailScreen(onBack = { navController.popBackStack() })
            }
            composable<Screen.Addresses> {
                AddressesScreen(onBack = { navController.popBackStack() })
            }
            composable<Screen.Favorites> {
                FavoritesScreen(
                    onProductClick = { productId -> navController.navigate(Screen.ProductDetail(productId)) },
                    onNavigateToLogin = { navController.navigate(Screen.Login) }
                )
            }
            composable<Screen.Settings> {
                SettingsScreen(onNavigateToAdmin = { navController.navigate(Screen.AdminLogin) })
            }

            // §6/§11: bu to'rttasi hech qanday menyu yoki bottom nav'da ro'yxatga olinmaydi —
            // faqat Sozlamalar'dagi yashirin trigger orqali kelinadi
            composable<Screen.AdminLogin> {
                AdminLoginScreen(
                    onAccessGranted = {
                        navController.navigate(Screen.AdminDashboard) {
                            popUpTo(Screen.AdminLogin) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.navigate(Screen.Login) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable<Screen.AdminDashboard> {
                AdminDashboardScreen(
                    onNavigateToProducts = { navController.navigate(Screen.AdminProducts) },
                    onNavigateToOrders = { navController.navigate(Screen.AdminOrders) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable<Screen.AdminOrders> {
                AdminOrdersScreen(
                    onBack = { navController.popBackStack() },
                    onOrderClick = { orderId -> navController.navigate(Screen.AdminOrderDetail(orderId)) }
                )
            }
            composable<Screen.AdminOrderDetail> {
                AdminOrderDetailScreen(onBack = { navController.popBackStack() })
            }
            composable<Screen.AdminProducts> {
                AdminProductsScreen(
                    onBack = { navController.popBackStack() },
                    onProductClick = { productId -> navController.navigate(Screen.AdminProductForm(productId)) },
                    onAddNew = { navController.navigate(Screen.AdminProductForm()) }
                )
            }
            composable<Screen.AdminProductForm> {
                AdminProductFormScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun QurilishMarketBottomBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    NavigationBar {
        bottomNavItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.hasRoute(item.screen::class) } == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.screen) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

private data class BottomNavItem(val screen: Screen, val label: String, val icon: ImageVector)

private val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Bosh sahifa", Icons.Filled.Home),
    BottomNavItem(Screen.Categories, "Kategoriyalar", Icons.Filled.Category),
    BottomNavItem(Screen.Cart, "Savatcha", Icons.Filled.ShoppingCart),
    BottomNavItem(Screen.Orders, "Buyurtmalar", Icons.Filled.ListAlt),
    BottomNavItem(Screen.Profile, "Profil", Icons.Filled.Person)
)

/** Vaqtinchalik — har bir ekran keyingi bosqichda haqiqiy composable bilan almashtiriladi. */
@Composable
private fun PlaceholderScreen(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("$name — keyingi bosqichda to'ldiriladi")
    }
}
