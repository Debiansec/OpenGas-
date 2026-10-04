package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.model.Order
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoreViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrderTrackingScreen(
    viewModel: StoreViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val orders by viewModel.orders.collectAsState()
    val trackedOrder by viewModel.trackedOrder.collectAsState()
    val searchError by viewModel.orderSearchError.collectAsState()

    var orderIdInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }

    val activeOrder = trackedOrder ?: orders.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Search Header Bar with Order ID & Phone Number Fields
        Surface(
            color = Navy900,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Track Your Order",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Search by Order ID and Phone Number",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp
                        )
                    }

                    // Live Status Pill
                    if (activeOrder != null) {
                        Surface(
                            color = if (activeOrder.orderStatus in listOf("DELIVERED", "COMPLETED")) DeliveryGreen else FlameOrange,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = activeOrder.orderStatus,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Dual Inputs: Order ID and Phone Number
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = orderIdInput,
                            onValueChange = { orderIdInput = it },
                            placeholder = { Text("Order ID (e.g. GO-2026-00001)", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                            },
                            trailingIcon = {
                                if (orderIdInput.isNotBlank()) {
                                    IconButton(onClick = { orderIdInput = "" }, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(14.dp))
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("track_order_id_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = FlameOrange,
                                unfocusedBorderColor = BorderLight
                            )
                        )

                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            placeholder = { Text("Phone Number", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                            },
                            trailingIcon = {
                                if (phoneInput.isNotBlank()) {
                                    IconButton(onClick = { phoneInput = "" }, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(14.dp))
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("track_phone_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = FlameOrange,
                                unfocusedBorderColor = BorderLight
                            )
                        )
                    }

                    // Track Button
                    Button(
                        onClick = {
                            viewModel.searchOrder(orderId = orderIdInput, phoneNumber = phoneInput)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("track_search_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Track Order Now", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                // Quick Select Demo Order Chips
                if (orders.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item {
                            Text("Demo Orders:", fontSize = 10.sp, color = Color(0xFF94A3B8), modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp))
                        }
                        items(orders.take(3)) { ord ->
                            val isSelected = activeOrder?.orderNumber == ord.orderNumber
                            Surface(
                                color = if (isSelected) BluePrimary else Navy800,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.clickable {
                                    viewModel.trackedOrder.value = ord
                                    orderIdInput = ord.orderNumber
                                    phoneInput = ord.customerMobile
                                }
                            ) {
                                Text(
                                    text = "${ord.orderNumber} (${ord.customerName.take(6)})",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }

                // Error Notice if Search Fails
                if (searchError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFF7F1D1D),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFFECACA), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = searchError!!, color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (activeOrder != null) {
                // Tracking Details & Status Progress Bar Card
                item {
                    OrderTrackingCard(
                        order = activeOrder,
                        onOpenInvoice = { viewModel.openInvoice(activeOrder) },
                        onWhatsApp = {
                            val msg = "Hello GasOn, I would like an update on order ${activeOrder.orderNumber}."
                            viewModel.openWhatsApp(context, msg)
                        },
                        onCall = { viewModel.makePhoneCall(context) }
                    )
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.SearchOff, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                                Text(
                                    text = "No active orders found.\nEnter your Order ID and Phone Number above to find your order status.",
                                    color = TextSecondary,
                                    fontSize = 13.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Quick list of all recent orders
            if (orders.isNotEmpty()) {
                item {
                    Text(
                        text = "Your Orders History",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Navy900
                    )
                }

                items(orders, key = { it.orderNumber }) { order ->
                    val isSelected = activeOrder?.orderNumber == order.orderNumber
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.trackedOrder.value = order
                                orderIdInput = order.orderNumber
                                phoneInput = order.customerMobile
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFEFF6FF) else Color.White),
                        border = if (isSelected) CardDefaults.outlinedCardBorder() else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = order.orderNumber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) BluePrimary else TextPrimary
                                )
                                Text(
                                    text = "${if (order.isDelivery) "🚚 Delivery" else "📦 Collection"} • R${String.format("%.2f", order.total)}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            Surface(
                                color = if (order.orderStatus in listOf("DELIVERED", "COMPLETED")) Color(0xFFD1FAE5) else Color(0xFFFFEDD5),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = order.orderStatus,
                                    color = if (order.orderStatus in listOf("DELIVERED", "COMPLETED")) DeliveryGreen else FlameOrangeDark,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Order Tracking UI Component displaying:
 * - Status Progress Bar (horizontal linear progress with percentage)
 * - Estimated delivery time
 * - Detailed visual milestones
 * - Delivery destination and contact recap
 * - Tax invoice and WhatsApp communication actions
 */
@Composable
fun OrderTrackingCard(
    order: Order,
    onOpenInvoice: () -> Unit,
    onWhatsApp: () -> Unit,
    onCall: () -> Unit
) {
    val stages = listOf(
        "ORDER RECEIVED",
        "PAYMENT CONFIRMED",
        "ORDER PROCESSING",
        if (order.isDelivery) "OUT FOR DELIVERY" else "READY FOR COLLECTION",
        "DELIVERED"
    )

    val currentStageIndex = when (order.orderStatus) {
        "ORDER RECEIVED" -> 0
        "PAYMENT CONFIRMED" -> 1
        "ORDER PROCESSING" -> 2
        "OUT FOR DELIVERY", "READY FOR COLLECTION" -> 3
        "DELIVERED", "COMPLETED" -> 4
        else -> 1
    }

    val progressFraction = when (currentStageIndex) {
        0 -> 0.20f
        1 -> 0.40f
        2 -> 0.60f
        3 -> 0.85f
        4 -> 1.0f
        else -> 0.40f
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("order_tracking_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Order ${order.orderNumber}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Navy900)
                    Text(text = "Customer: ${order.customerName} • ${order.customerMobile}", fontSize = 11.sp, color = TextSecondary)
                }

                Surface(
                    color = if (order.orderStatus in listOf("DELIVERED", "COMPLETED")) DeliveryGreen else FlameOrange,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = order.orderStatus,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // STATUS PROGRESS BAR COMPONENT
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(12.dp)
                    .testTag("order_status_progress_bar")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (currentStageIndex >= 3) Icons.Default.LocalShipping else Icons.Default.PendingActions,
                            contentDescription = null,
                            tint = if (currentStageIndex == 4) DeliveryGreen else FlameOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Status: ${(progressFraction * 100).toInt()}% Complete",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                    }

                    Text(
                        text = when (currentStageIndex) {
                            0 -> "Estimated: 60 - 90 mins"
                            1 -> "Estimated: 45 - 60 mins"
                            2 -> "Dispatching soon"
                            3 -> "ETA: 15 - 30 mins"
                            4 -> "Delivered"
                            else -> "In Progress"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FlameOrangeDark
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Horizontal Status Progress Bar
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (currentStageIndex == 4) DeliveryGreen else FlameOrange,
                    trackColor = Color(0xFFE2E8F0)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Milestone Labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Placed", fontSize = 9.sp, color = TextMuted)
                    Text("Confirmed", fontSize = 9.sp, color = TextMuted)
                    Text("Processing", fontSize = 9.sp, color = TextMuted)
                    Text(if (order.isDelivery) "Dispatched" else "Ready", fontSize = 9.sp, color = TextMuted)
                    Text("Done", fontSize = 9.sp, color = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Detailed Visual Progress Timeline
            Text(text = "Delivery Milestones:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Navy900)
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                stages.forEachIndexed { index, stageName ->
                    val isDone = index <= currentStageIndex
                    val isCurrent = index == currentStageIndex

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isDone) (if (isCurrent) FlameOrange else DeliveryGreen) else BorderLight),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(
                                    imageVector = if (isCurrent) Icons.Default.DirectionsRun else Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = stageName,
                                fontSize = 12.sp,
                                fontWeight = if (isCurrent) FontWeight.Black else if (isDone) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCurrent) FlameOrange else if (isDone) Navy900 else TextMuted
                            )
                            if (isCurrent) {
                                Text(
                                    text = when (stageName) {
                                        "OUT FOR DELIVERY" -> "Driver dispatched to Henley on Klip / Meyerton area."
                                        "READY FOR COLLECTION" -> "Cylinders ready for pickup at Meyerton central depot."
                                        "ORDER PROCESSING" -> "Cylinders filled, safety-checked and loaded on delivery tuktuk."
                                        "PAYMENT CONFIRMED" -> "Payment verified. Preparing order."
                                        "DELIVERED" -> "Order completed successfully. Thank you for choosing GasOn!"
                                        else -> "Order received in system."
                                    },
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = BorderLight)
            Spacer(modifier = Modifier.height(12.dp))

            // Delivery Details & Items
            Text(text = "Destination & Order Items:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Navy900)
            Text(
                text = if (order.isDelivery) "🚚 ${order.deliveryAddress}, ${order.deliverySuburb}" else "📦 Depot Collection (12 Loch Street, Meyerton Central)",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Text(text = "Items: ${order.itemsSummary}", fontSize = 11.sp, color = TextPrimary)
            Text(text = "Total: R ${String.format("%.2f", order.total)} (${order.paymentMethod})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BluePrimary)

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons (Tax Invoice, WhatsApp Support, Call Depot)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenInvoice,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tax Invoice", fontSize = 11.sp)
                }

                Button(
                    onClick = onWhatsApp,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("WhatsApp", fontSize = 11.sp, color = Color.White)
                }

                IconButton(
                    onClick = onCall,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Navy900)
                ) {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = "Call", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
