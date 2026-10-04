package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.ui.components.ProductVisual
import com.example.ui.theme.*
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StoreViewModel

@Composable
fun CheckoutScreen(
    viewModel: StoreViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val step by viewModel.checkoutStep.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val deliveryZones by viewModel.deliveryZones.collectAsState()
    val settings by viewModel.appSettings.collectAsState()
    val couponDiscountPercent by viewModel.couponDiscountPercent.collectAsState()

    // Form states
    val name by viewModel.customerName.collectAsState()
    val mobile by viewModel.customerMobile.collectAsState()
    val email by viewModel.customerEmail.collectAsState()
    val whatsApp by viewModel.customerWhatsApp.collectAsState()

    val isDelivery by viewModel.isDeliveryMethod.collectAsState()
    val address by viewModel.deliveryAddress.collectAsState()
    val suburb by viewModel.deliverySuburb.collectAsState()
    val city by viewModel.deliveryCity.collectAsState()
    val postalCode by viewModel.deliveryPostalCode.collectAsState()
    val selectedZoneId by viewModel.selectedDeliveryZoneId.collectAsState()
    val instructions by viewModel.deliveryInstructions.collectAsState()

    val collectionDate by viewModel.collectionDate.collectAsState()
    val collectionTimeSlot by viewModel.collectionTimeSlot.collectAsState()

    val scheduleType by viewModel.scheduleType.collectAsState()
    val scheduledDate by viewModel.scheduledDate.collectAsState()
    val scheduledTimeSlot by viewModel.scheduledTimeSlot.collectAsState()

    val paymentMethod by viewModel.selectedPaymentMethod.collectAsState()
    val proofUploaded by viewModel.proofOfPaymentUploaded.collectAsState()

    val subtotal = remember(cartItems) { cartItems.sumOf { it.subtotal } }
    val deliveryFee = remember(subtotal, isDelivery, selectedZoneId) { viewModel.calculateDeliveryFee(subtotal) }
    val discountAmount = remember(subtotal, couponDiscountPercent) {
        if (couponDiscountPercent > 0) subtotal * (couponDiscountPercent / 100.0) else 0.0
    }
    val grandTotal = remember(subtotal, deliveryFee, discountAmount) {
        (subtotal + deliveryFee - discountAmount).coerceAtLeast(0.0)
    }

    var placedOrder by remember { mutableStateOf<Order?>(null) }
    var isOrderSummaryExpanded by remember { mutableStateOf(false) }

    // If cart is empty and no order has been placed, show empty state
    if (cartItems.isEmpty() && placedOrder == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFEDD5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = FlameOrange,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Text(
                    text = "Your Cart is Empty",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
                Text(
                    text = "Add gas bottles or appliances to your cart before proceeding to checkout.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.navigateTo(Screen.SHOP) },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Browse Gas Store", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Multi-Step Checkout Header
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
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
                        IconButton(
                            onClick = {
                                if (step > 1 && placedOrder == null) {
                                    viewModel.checkoutStep.value = step - 1
                                } else {
                                    viewModel.navigateTo(Screen.CART)
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = if (placedOrder != null) "Order Placed Successfully" else "Multi-Step Checkout",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Navy900
                            )
                            if (placedOrder == null) {
                                val stepTitle = when (step) {
                                    1 -> "Step 1: Contact Details"
                                    2 -> "Step 2: Delivery & Address"
                                    3 -> "Step 3: Delivery Scheduling"
                                    4 -> "Step 4: Review & Payment"
                                    else -> "Checkout"
                                }
                                Text(
                                    text = stepTitle,
                                    fontSize = 12.sp,
                                    color = FlameOrange,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    if (placedOrder == null) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Total",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "R ${String.format("%.2f", grandTotal)}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = BluePrimary
                            )
                        }
                    }
                }

                if (placedOrder == null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    // Step Progress Indicator with Labels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (i in 1..4) {
                            val isCompleted = i < step
                            val isCurrent = i == step
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        when {
                                            isCurrent -> FlameOrange
                                            isCompleted -> DeliveryGreen
                                            else -> BorderLight
                                        }
                                    )
                                    .clickable(enabled = isCompleted) {
                                        viewModel.checkoutStep.value = i
                                    }
                            )
                        }
                    }

                    // Collapsible Cart Order Summary Bar (accessible during steps 1-3)
                    if (step < 4) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(8.dp),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isOrderSummaryExpanded = !isOrderSummaryExpanded }
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.ShoppingCart,
                                            contentDescription = null,
                                            tint = BluePrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Cart Summary (${cartItems.sumOf { it.quantity }} items)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Navy900
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "R ${String.format("%.2f", subtotal)}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BluePrimary
                                        )
                                        Icon(
                                            imageVector = if (isOrderSummaryExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            tint = TextSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                if (isOrderSummaryExpanded) {
                                    Divider(color = BorderLight, modifier = Modifier.padding(vertical = 6.dp))
                                    cartItems.forEach { item ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 2.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${item.quantity}x ${item.productName}",
                                                fontSize = 11.sp,
                                                color = TextSecondary,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = "R ${String.format("%.2f", item.subtotal)}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = TextPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (placedOrder != null) {
            // ORDER SUCCESS SCREEN
            val order = placedOrder!!
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = DeliveryGreen,
                    modifier = Modifier.size(72.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Thank You For Your Order!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Navy900
                )
                Text(
                    text = "Order Number: ${order.orderNumber}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "Order Summary", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Navy900)
                        Divider(color = BorderLight)
                        Text(text = "Customer: ${order.customerName}", fontSize = 13.sp)
                        Text(text = "Delivery / Collection: ${if (order.isDelivery) "🚚 Delivery to ${order.deliverySuburb}" else "📦 Depot Collection"}", fontSize = 13.sp)
                        Text(text = "Total Amount: R ${String.format("%.2f", order.total)}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BluePrimary)
                        Text(text = "Payment Method: ${order.paymentMethod}", fontSize = 13.sp)
                        Text(text = "Status: ${order.orderStatus}", color = if (order.paymentStatus == "CONFIRMED") DeliveryGreen else FlameOrange, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                if (order.paymentMethod == "DIRECT_EFT") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = "Direct EFT Payment Instructions", fontWeight = FontWeight.Bold, color = BluePrimary, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "Account Name: ${settings.bankAccountName}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Bank: ${settings.bankName}", fontSize = 12.sp)
                            Text(text = "Account: ${settings.bankAccountNumber}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Branch Code: ${settings.bankBranchCode}", fontSize = 12.sp)
                            Text(text = "Reference: ${order.orderNumber}", fontWeight = FontWeight.Bold, color = FlameOrange, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Our admin will verify your payment and dispatch your gas quickly.", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // WhatsApp Confirmation Button
                Button(
                    onClick = {
                        val msg = viewModel.generateWhatsAppOrderText(order)
                        viewModel.openWhatsApp(context, msg)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("success_send_whatsapp_btn")
                ) {
                    Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send Order via WhatsApp", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // View Invoice Button
                OutlinedButton(
                    onClick = { viewModel.openInvoice(order) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View / Print Tax Invoice")
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Track Order Button
                Button(
                    onClick = {
                        viewModel.navigateTo(Screen.ORDER_TRACKING)
                        viewModel.trackedOrder.value = order
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Track Order Live")
                }
            }
        } else {
            // STEP WIZARD
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (step) {
                    1 -> {
                        // STEP 1: Customer Contact Details
                        Text(text = "Customer Contact Details", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Navy900)
                        Text(text = "We will send your invoice and delivery tracking updates to these details.", fontSize = 12.sp, color = TextSecondary)

                        OutlinedTextField(
                            value = name,
                            onValueChange = { viewModel.customerName.value = it },
                            label = { Text("Full Name *") },
                            modifier = Modifier.fillMaxWidth().testTag("checkout_name_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = mobile,
                            onValueChange = { viewModel.customerMobile.value = it },
                            label = { Text("Mobile Phone Number *") },
                            modifier = Modifier.fillMaxWidth().testTag("checkout_mobile_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { viewModel.customerEmail.value = it },
                            label = { Text("Email Address *") },
                            modifier = Modifier.fillMaxWidth().testTag("checkout_email_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = whatsApp,
                            onValueChange = { viewModel.customerWhatsApp.value = it },
                            label = { Text("WhatsApp Number (for driver arrival alerts)") },
                            modifier = Modifier.fillMaxWidth().testTag("checkout_whatsapp_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }

                    2 -> {
                        // STEP 2: Delivery Details Collection
                        Text(text = "Delivery Method & Address", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Navy900)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE2E8F0))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isDelivery) BluePrimary else Color.Transparent)
                                    .clickable { viewModel.isDeliveryMethod.value = true }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, tint = if (isDelivery) Color.White else TextSecondary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("🚚 DELIVERY", color = if (isDelivery) Color.White else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (!isDelivery) FlameOrange else Color.Transparent)
                                    .clickable { viewModel.isDeliveryMethod.value = false }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Storefront, contentDescription = null, tint = if (!isDelivery) Color.White else TextSecondary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("📦 COLLECTION", color = if (!isDelivery) Color.White else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }

                        if (isDelivery) {
                            // Delivery Address Form
                            Text(text = "Select Delivery Zone:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Navy900)

                            deliveryZones.forEach { zone ->
                                val isSelected = selectedZoneId == zone.id
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.selectedDeliveryZoneId.value = zone.id },
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
                                            Text(text = zone.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (isSelected) BluePrimary else TextPrimary)
                                            Text(text = "Est. Delivery: ${zone.estimatedDeliveryTime} • Min order R${zone.minimumOrder}", fontSize = 11.sp, color = TextSecondary)
                                        }
                                        Text(text = "R ${String.format("%.0f", zone.deliveryFee)}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BluePrimary)
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = address,
                                onValueChange = { viewModel.deliveryAddress.value = it },
                                label = { Text("Street Address *") },
                                modifier = Modifier.fillMaxWidth().testTag("checkout_address_input"),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = suburb,
                                    onValueChange = { viewModel.deliverySuburb.value = it },
                                    label = { Text("Suburb *") },
                                    modifier = Modifier.weight(1.5f),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = postalCode,
                                    onValueChange = { viewModel.deliveryPostalCode.value = it },
                                    label = { Text("Postal Code") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true
                                )
                            }

                            OutlinedTextField(
                                value = instructions,
                                onValueChange = { viewModel.deliveryInstructions.value = it },
                                label = { Text("Gate code / Delivery instructions") },
                                placeholder = { Text("e.g. Ring gate bell, dog is friendly") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                        } else {
                            // Depot Collection details
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(text = "Depot Collection Address:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Navy900)
                                    Text(text = settings.depotAddress, fontSize = 12.sp, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(text = "Operating Hours: Mon - Sat: 07:30 - 17:30 | Sun: 08:30 - 13:00", fontSize = 11.sp, color = DeliveryGreen)
                                }
                            }

                            OutlinedTextField(
                                value = collectionDate,
                                onValueChange = { viewModel.collectionDate.value = it },
                                label = { Text("Preferred Collection Date") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = collectionTimeSlot,
                                onValueChange = { viewModel.collectionTimeSlot.value = it },
                                label = { Text("Preferred Collection Time Window") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                        }
                    }

                    3 -> {
                        // STEP 3: Delivery Scheduling
                        Text(text = "Delivery Schedule & Timing", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Navy900)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.scheduleType.value = "ASAP" },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = if (scheduleType == "ASAP") Color(0xFFEFF6FF) else Color.White),
                            border = if (scheduleType == "ASAP") CardDefaults.outlinedCardBorder() else null
                        ) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = scheduleType == "ASAP", onClick = { viewModel.scheduleType.value = "ASAP" })
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = "⚡ AS SOON AS POSSIBLE (ASAP)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Navy900)
                                    Text(text = "Fastest dispatch! Usually delivered within 45 to 90 minutes.", fontSize = 11.sp, color = TextSecondary)
                                }
                            }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.scheduleType.value = "SCHEDULED" },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = if (scheduleType == "SCHEDULED") Color(0xFFEFF6FF) else Color.White),
                            border = if (scheduleType == "SCHEDULED") CardDefaults.outlinedCardBorder() else null
                        ) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = scheduleType == "SCHEDULED", onClick = { viewModel.scheduleType.value = "SCHEDULED" })
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = "📅 SCHEDULED DELIVERY", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Navy900)
                                    Text(text = "Choose a specific date and time window that suits you.", fontSize = 11.sp, color = TextSecondary)
                                }
                            }
                        }

                        if (scheduleType == "SCHEDULED") {
                            OutlinedTextField(
                                value = scheduledDate,
                                onValueChange = { viewModel.scheduledDate.value = it },
                                label = { Text("Delivery Date (e.g. Today / Tomorrow / 27 Sep)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )

                            Text(text = "Select Time Window:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
                            val windows = listOf("Morning: 08:00 - 11:00", "Midday: 11:00 - 14:00", "Afternoon: 14:00 - 17:00")
                            windows.forEach { window ->
                                val isSelected = scheduledTimeSlot == window
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.scheduledTimeSlot.value = window },
                                    label = { Text(window, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BluePrimary, selectedLabelColor = Color.White),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    4 -> {
                        // STEP 4: Full Order Summary & Payment
                        Text(text = "Order Summary & Payment", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Navy900)

                        // Full Comprehensive Order Summary Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_order_summary_card"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Items in Order (${cartItems.sumOf { it.quantity }})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Navy900)
                                    Text(
                                        text = "Edit Cart",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BluePrimary,
                                        modifier = Modifier.clickable { viewModel.navigateTo(Screen.CART) }
                                    )
                                }
                                Divider(color = BorderLight)

                                cartItems.forEach { item ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                            ) {
                                                ProductVisual(
                                                    imageType = item.imageType,
                                                    label = item.bottleSize ?: "GasOn",
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = item.productName,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = TextPrimary
                                                )
                                                Text(
                                                    text = "${item.quantity} x R${String.format("%.2f", item.price)}",
                                                    fontSize = 10.sp,
                                                    color = TextSecondary
                                                )
                                            }
                                        }

                                        Text(
                                            text = "R ${String.format("%.2f", item.subtotal)}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Navy900
                                        )
                                    }
                                }

                                Divider(color = BorderLight)

                                // Delivery destination recap
                                Text(text = "Delivery Destination:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                Text(
                                    text = if (isDelivery) "$address, $suburb ($city)" else "Customer Collection at GasOn Depot (Meyerton)",
                                    fontSize = 12.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Recipient: $name • $mobile",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )

                                Divider(color = BorderLight)

                                // Price Breakdown
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Subtotal", fontSize = 12.sp, color = TextSecondary)
                                    Text(text = "R ${String.format("%.2f", subtotal)}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Delivery Fee", fontSize = 12.sp, color = TextSecondary)
                                    Text(
                                        text = if (deliveryFee == 0.0) "FREE" else "R ${String.format("%.2f", deliveryFee)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (deliveryFee == 0.0) DeliveryGreen else TextPrimary
                                    )
                                }
                                if (discountAmount > 0) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = "Discount Applied ($couponDiscountPercent%)", fontSize = 12.sp, color = DeliveryGreen)
                                        Text(text = "- R ${String.format("%.2f", discountAmount)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeliveryGreen)
                                    }
                                }
                                Divider(color = BorderLight)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Total to Pay", fontSize = 15.sp, fontWeight = FontWeight.Black, color = Navy900)
                                    Text(text = "R ${String.format("%.2f", grandTotal)}", fontSize = 17.sp, fontWeight = FontWeight.Black, color = BluePrimary)
                                }
                            }
                        }

                        // Payment Methods
                        Text(text = "Select Payment Method:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Navy900)

                        // 1. Direct EFT
                        PaymentMethodCard(
                            title = "Direct EFT (Bank Transfer)",
                            subtitle = "Standard Bank South Africa • Instant verification",
                            isSelected = paymentMethod == "DIRECT_EFT",
                            onClick = { viewModel.selectedPaymentMethod.value = "DIRECT_EFT" }
                        )

                        if (paymentMethod == "DIRECT_EFT") {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = "Banking Details for GasOn:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Navy900)
                                    Text(text = "Account Name: ${settings.bankAccountName}", fontSize = 12.sp)
                                    Text(text = "Bank: ${settings.bankName}", fontSize = 12.sp)
                                    Text(text = "Account: ${settings.bankAccountNumber}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "Branch: ${settings.bankBranchCode}", fontSize = 12.sp)
                                    Text(text = "Reference: (Generated upon order submission)", fontSize = 11.sp, color = FlameOrange)

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (proofUploaded) "✓ Proof of payment attached" else "Attach proof of payment (optional)",
                                            fontSize = 11.sp,
                                            color = if (proofUploaded) DeliveryGreen else TextSecondary,
                                            fontWeight = if (proofUploaded) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Button(
                                            onClick = {
                                                viewModel.proofOfPaymentUploaded.value = true
                                                viewModel.proofOfPaymentFileName.value = "EFT_Deposit_slip_2026.pdf"
                                                Toast.makeText(context, "Proof of payment attached successfully", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(6.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = if (proofUploaded) DeliveryGreen else BluePrimary),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(if (proofUploaded) "Attached" else "Simulate Upload", fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // 2. PayFast
                        PaymentMethodCard(
                            title = "PayFast (South Africa)",
                            subtitle = "Instant EFT, Visa, Mastercard, SnapScan",
                            isSelected = paymentMethod == "PAYFAST",
                            onClick = { viewModel.selectedPaymentMethod.value = "PAYFAST" }
                        )

                        // 3. PayPal
                        PaymentMethodCard(
                            title = "PayPal",
                            subtitle = "International & Credit Card",
                            isSelected = paymentMethod == "PAYPAL",
                            onClick = { viewModel.selectedPaymentMethod.value = "PAYPAL" }
                        )
                    }
                }
            }

            // Fixed Bottom Action Bar
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (step > 1) {
                        OutlinedButton(
                            onClick = { viewModel.checkoutStep.value = step - 1 },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            Text("Back")
                        }
                    }

                    Button(
                        onClick = {
                            if (step < 4) {
                                if (step == 1 && name.isBlank()) {
                                    Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (step == 1 && mobile.isBlank()) {
                                    Toast.makeText(context, "Please enter your phone number", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (step == 2 && isDelivery && address.isBlank()) {
                                    Toast.makeText(context, "Please enter your delivery address", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                viewModel.checkoutStep.value = step + 1
                            } else {
                                // Final Step: Submit Order
                                viewModel.submitOrder { order ->
                                    placedOrder = order
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (step == 4) DeliveryGreen else FlameOrange),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(46.dp)
                            .testTag("checkout_continue_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (step == 4) "Place Order • R${String.format("%.2f", grandTotal)}" else "Continue to Step ${step + 1}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = if (step == 4) Icons.Default.Check else Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("payment_method_${title.lowercase().take(6)}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFEFF6FF) else Color.White),
        border = if (isSelected) CardDefaults.outlinedCardBorder() else null
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = isSelected, onClick = onClick)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Navy900)
                Text(text = subtitle, fontSize = 11.sp, color = TextSecondary)
            }
        }
    }
}
