package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettings
import com.example.data.model.DeliveryZone
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.ui.theme.*
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StoreViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: StoreViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val orders by viewModel.orders.collectAsState()
    val products by viewModel.adminProducts.collectAsState()
    val zones by viewModel.deliveryZones.collectAsState()
    val settings by viewModel.appSettings.collectAsState()
    val marketingEvents by viewModel.marketingEvents.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Orders", "Products", "Zones", "Settings", "Analytics")

    // Edit Product Dialog State
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var editPriceText by remember { mutableStateOf("") }
    var editStockText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Admin Top Header
        Surface(
            color = Navy900,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.navigateTo(Screen.ACCOUNT) }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Admin Dashboard",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "GasOn • Meyerton & Midvaal Management",
                                color = FlameYellow,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Surface(
                        color = if (settings.isDemoMode) Color(0xFFFEF3C7) else Color(0xFFD1FAE5),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (settings.isDemoMode) "DEMO MODE" else "LIVE MODE",
                            color = if (settings.isDemoMode) Color(0xFF92400E) else Color(0xFF065F46),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Admin Tab Strip
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Navy900,
                    contentColor = Color.White,
                    edgePadding = 0.dp
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) FlameOrange else Color(0xFFCBD5E1)
                                )
                            },
                            modifier = Modifier.testTag("admin_tab_$index")
                        )
                    }
                }
            }
        }

        // Tab Content
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // TAB 0: OVERVIEW & STATS
                    item {
                        Text(text = "Business Metrics Today", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Navy900)
                    }

                    item {
                        val totalRevenue = orders.filter { it.paymentStatus == "CONFIRMED" }.sumOf { it.total }
                        val pendingEftCount = orders.count { it.paymentStatus == "PENDING_EFT" }
                        val activeDeliveries = orders.count { it.orderStatus == "OUT FOR DELIVERY" }
                        val collections = orders.count { it.orderStatus == "READY FOR COLLECTION" }

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                StatCard("Confirmed Revenue", "R ${String.format("%.0f", totalRevenue)}", BluePrimary, Modifier.weight(1f))
                                StatCard("Total Orders", "${orders.size}", Navy900, Modifier.weight(1f))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                StatCard("Pending EFT Approval", "$pendingEftCount", FlameRed, Modifier.weight(1f))
                                StatCard("Out For Delivery", "$activeDeliveries", DeliveryGreen, Modifier.weight(1f))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                StatCard("Ready for Collection", "$collections", FlameOrange, Modifier.weight(1f))
                                StatCard("Active Products", "${products.size}", Navy700, Modifier.weight(1f))
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Quick Actions & Pending EFTs", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Navy900)
                    }

                    val pendingEfts = orders.filter { it.paymentStatus == "PENDING_EFT" }
                    if (pendingEfts.isEmpty()) {
                        item {
                            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                Text(
                                    text = "✓ No EFT payments currently pending verification.",
                                    modifier = Modifier.padding(14.dp),
                                    color = DeliveryGreen,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    } else {
                        items(pendingEfts) { order ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "EFT: ${order.orderNumber} • R${String.format("%.2f", order.total)}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(text = "Customer: ${order.customerName} (${order.customerMobile})", fontSize = 11.sp, color = TextSecondary)
                                        if (order.proofOfPaymentNote != null) {
                                            Text(text = order.proofOfPaymentNote, fontSize = 10.sp, color = BluePrimary)
                                        }
                                    }
                                    Button(
                                        onClick = {
                                            viewModel.adminConfirmEftPayment(order.orderNumber)
                                            Toast.makeText(context, "EFT Approved for ${order.orderNumber}", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = DeliveryGreen),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("Approve EFT", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: ORDERS MANAGEMENT
                    item {
                        Text(text = "Order Management (${orders.size} orders)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Navy900)
                    }

                    items(orders) { order ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = order.orderNumber, fontWeight = FontWeight.Black, fontSize = 14.sp, color = BluePrimary)
                                        Text(text = "${order.customerName} • ${order.customerMobile}", fontSize = 11.sp, color = TextSecondary)
                                    }
                                    Surface(
                                        color = if (order.paymentStatus == "CONFIRMED") DeliveryGreen else FlameOrange,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = order.orderStatus,
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "Items: ${order.itemsSummary}", fontSize = 12.sp, color = TextPrimary)
                                Text(text = "Total: R ${String.format("%.2f", order.total)} (${order.paymentMethod})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Navy900)

                                Spacer(modifier = Modifier.height(10.dp))

                                // Status Changer Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.adminUpdateOrderStatus(order.orderNumber, "OUT FOR DELIVERY") },
                                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Out for Delivery", fontSize = 10.sp)
                                    }

                                    Button(
                                        onClick = { viewModel.adminUpdateOrderStatus(order.orderNumber, "DELIVERED") },
                                        colors = ButtonDefaults.buttonColors(containerColor = DeliveryGreen),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Delivered", fontSize = 10.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { viewModel.openInvoice(order) },
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        modifier = Modifier.weight(0.8f)
                                    ) {
                                        Text("Invoice", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: PRODUCTS MANAGEMENT
                    item {
                        Text(text = "Product Catalog & Live Pricing", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Navy900)
                        Text(text = "All prices and stock levels can be edited directly without hard-coding.", fontSize = 11.sp, color = TextSecondary)
                    }

                    items(products) { product ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = "Category: ${product.category} • SKU: ${product.sku}", fontSize = 10.sp, color = TextMuted)
                                    Text(text = "Price: R ${String.format("%.2f", product.price)} • Stock: ${product.stock}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = BluePrimary)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = {
                                            editingProduct = product
                                            editPriceText = product.price.toString()
                                            editStockText = product.stock.toString()
                                        },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("Edit", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // TAB 3: DELIVERY ZONES
                    item {
                        Text(text = "Configurable Delivery Zones", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Navy900)
                        Text(text = "Configure delivery fees and free-delivery thresholds for Henley on Klip, Meyerton & Midvaal.", fontSize = 11.sp, color = TextSecondary)
                    }

                    items(zones) { zone ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "📍 ${zone.name}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Navy900)
                                    Text(text = "Delivery Fee: R ${String.format("%.0f", zone.deliveryFee)}", fontWeight = FontWeight.Bold, color = BluePrimary, fontSize = 13.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Free Delivery Threshold: R ${String.format("%.0f", zone.freeDeliveryThreshold)}", fontSize = 12.sp, color = DeliveryGreen)
                                Text(text = "Minimum Order: R ${String.format("%.0f", zone.minimumOrder)}", fontSize = 11.sp, color = TextSecondary)
                                Text(text = "Estimated Time: ${zone.estimatedDeliveryTime}", fontSize = 11.sp, color = TextMuted)
                            }
                        }
                    }
                }

                4 -> {
                    // TAB 4: SETTINGS & SECURE CONFIG
                    item {
                        Text(text = "Business Configuration & Credentials", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Navy900)
                    }

                    item {
                        var whatsAppInput by remember { mutableStateOf(settings.whatsAppNumber) }
                        var phoneInput by remember { mutableStateOf(settings.contactPhone) }
                        var bankNameInput by remember { mutableStateOf(settings.bankName) }
                        var bankAccInput by remember { mutableStateOf(settings.bankAccountNumber) }
                        var isDemo by remember { mutableStateOf(settings.isDemoMode) }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                // Demo mode toggle
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "Demo Mode Testing", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(text = "Allows full order testing without charging real money", fontSize = 11.sp, color = TextSecondary)
                                    }
                                    Switch(checked = isDemo, onCheckedChange = { isDemo = it })
                                }

                                Divider(color = BorderLight)

                                OutlinedTextField(
                                    value = whatsAppInput,
                                    onValueChange = { whatsAppInput = it },
                                    label = { Text("WhatsApp Business Number") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                OutlinedTextField(
                                    value = phoneInput,
                                    onValueChange = { phoneInput = it },
                                    label = { Text("Depot Phone Number") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                OutlinedTextField(
                                    value = bankNameInput,
                                    onValueChange = { bankNameInput = it },
                                    label = { Text("EFT Bank Name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                OutlinedTextField(
                                    value = bankAccInput,
                                    onValueChange = { bankAccInput = it },
                                    label = { Text("EFT Account Number") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Secure API Keys (Stored Server-Side / Environment):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Navy900
                                )
                                Text(
                                    text = "• PAYFAST_MERCHANT_ID: ${settings.payFastMerchantId}\n• PAYPAL_CLIENT_ID: ${settings.payPalClientId}",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )

                                Button(
                                    onClick = {
                                        val updated = settings.copy(
                                            whatsAppNumber = whatsAppInput,
                                            contactPhone = phoneInput,
                                            bankName = bankNameInput,
                                            bankAccountNumber = bankAccInput,
                                            isDemoMode = isDemo
                                        )
                                        viewModel.adminSaveSettings(updated)
                                        Toast.makeText(context, "Settings saved successfully!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Save Configuration")
                                }
                            }
                        }
                    }
                }

                5 -> {
                    // TAB 5: ANALYTICS & DIGITAL MARKETING
                    item {
                        Text(text = "Digital Marketing Events Log", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Navy900)
                        Text(text = "Tracks: page_view, product_view, add_to_cart, begin_checkout, purchase, whatsapp_click", fontSize = 11.sp, color = TextSecondary)
                    }

                    items(marketingEvents) { event ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = event.eventName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BluePrimary)
                                Text(text = event.details, fontSize = 11.sp, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }

        // Product Edit Dialog
        if (editingProduct != null) {
            val p = editingProduct!!
            AlertDialog(
                onDismissRequest = { editingProduct = null },
                title = { Text("Edit ${p.name}") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editPriceText,
                            onValueChange = { editPriceText = it },
                            label = { Text("Price (ZAR)") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = editStockText,
                            onValueChange = { editStockText = it },
                            label = { Text("Stock Quantity") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newPrice = editPriceText.toDoubleOrNull() ?: p.price
                            val newStock = editStockText.toIntOrNull() ?: p.stock
                            viewModel.adminUpdateProductPriceAndStock(p.id, newPrice, newStock)
                            editingProduct = null
                            Toast.makeText(context, "Product updated", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingProduct = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun StatCard(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = label, fontSize = 11.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = valueColor)
        }
    }
}
