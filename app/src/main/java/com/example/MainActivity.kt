package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.GasOnTheme
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StoreViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: StoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GasOnTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: StoreViewModel) {
    val context = LocalContext.current
    val showSplash by viewModel.showSplash.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val isAiChatOpen by viewModel.isAiChatOpen.collectAsState()
    val selectedProduct by viewModel.selectedProduct.collectAsState()
    val invoiceOrder by viewModel.selectedInvoiceOrder.collectAsState()

    // Show white animated Splash Screen initially
    if (showSplash) {
        SplashScreen(onDismiss = { viewModel.dismissSplash() })
        return
    }

    val cartItemCount = remember(cartItems) { cartItems.sumOf { it.quantity } }
    val cartSubtotal = remember(cartItems) { cartItems.sumOf { it.subtotal } }

    // Back handling for sub screens
    BackHandler(enabled = currentScreen != Screen.HOME || selectedProduct != null || isAiChatOpen || invoiceOrder != null) {
        when {
            invoiceOrder != null -> viewModel.closeInvoice()
            isAiChatOpen -> viewModel.toggleAiChat(false)
            selectedProduct != null -> viewModel.closeProductDetail()
            currentScreen == Screen.CHECKOUT -> {
                if (viewModel.checkoutStep.value > 1) {
                    viewModel.checkoutStep.value = viewModel.checkoutStep.value - 1
                } else {
                    viewModel.navigateTo(Screen.CART)
                }
            }
            currentScreen != Screen.HOME -> viewModel.navigateTo(Screen.HOME)
        }
    }

    val showBottomNav = currentScreen in listOf(
        Screen.HOME,
        Screen.SHOP,
        Screen.GAS_DELIVERY,
        Screen.ORDER_TRACKING,
        Screen.ACCOUNT
    ) && selectedProduct == null

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundLight,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            if (selectedProduct == null && currentScreen !in listOf(Screen.CHECKOUT, Screen.ADMIN, Screen.SAFETY, Screen.INSTALLATION)) {
                AppHeader(
                    viewModel = viewModel,
                    cartItemCount = cartItemCount,
                    cartSubtotal = cartSubtotal,
                    onOpenCart = { viewModel.navigateTo(Screen.CART) }
                )
            }
        },
        bottomBar = {
            if (showBottomNav) {
                BottomNavBar(
                    currentScreen = currentScreen,
                    onSelectScreen = { viewModel.navigateTo(it) }
                )
            }
        },
        floatingActionButton = {
            if (showBottomNav && !isAiChatOpen) {
                FloatingAiButton(
                    onClick = { viewModel.toggleAiChat(true) },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.HOME -> HomeScreen(viewModel = viewModel)
                Screen.SHOP -> ShopScreen(viewModel = viewModel)
                Screen.GAS_DELIVERY -> GasDeliveryScreen(viewModel = viewModel)
                Screen.CART -> CartScreen(viewModel = viewModel)
                Screen.CHECKOUT -> CheckoutScreen(viewModel = viewModel)
                Screen.ORDER_TRACKING -> OrderTrackingScreen(viewModel = viewModel)
                Screen.SAFETY -> GasSafetyScreen(viewModel = viewModel)
                Screen.INSTALLATION -> InstallationRequestScreen(viewModel = viewModel)
                Screen.ACCOUNT -> AccountScreen(viewModel = viewModel)
                Screen.ADMIN -> AdminDashboardScreen(viewModel = viewModel)
            }

            // Full-screen Product Detail overlay if opened
            if (selectedProduct != null) {
                ProductDetailScreen(
                    product = selectedProduct!!,
                    viewModel = viewModel,
                    onBack = { viewModel.closeProductDetail() }
                )
            }

            // AI Shopping Assistant Dialog
            if (isAiChatOpen) {
                AiAssistantDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.toggleAiChat(false) }
                )
            }

            // Invoice Dialog
            if (invoiceOrder != null) {
                InvoiceDialog(
                    order = invoiceOrder!!,
                    onDismiss = { viewModel.closeInvoice() },
                    onShareWhatsApp = { shareText ->
                        viewModel.openWhatsApp(context, shareText)
                    }
                )
            }
        }
    }
}
