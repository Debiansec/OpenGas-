package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.ui.components.ProductVisual
import com.example.ui.theme.*
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StoreViewModel

@Composable
fun CartScreen(
    viewModel: StoreViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cartItems by viewModel.cartItems.collectAsState()
    val couponCode by viewModel.couponCode.collectAsState()
    val couponDiscountPercent by viewModel.couponDiscountPercent.collectAsState()
    val isFreeDeliveryCoupon by viewModel.isFreeDeliveryCoupon.collectAsState()

    var couponInput by remember { mutableStateOf("") }

    val subtotal = remember(cartItems) { cartItems.sumOf { it.subtotal } }
    val deliveryFee = remember(subtotal, isFreeDeliveryCoupon) { viewModel.calculateDeliveryFee(subtotal) }
    val discount = remember(subtotal, couponDiscountPercent) { subtotal * (couponDiscountPercent / 100.0) }
    val grandTotal = remember(subtotal, deliveryFee, discount) { (subtotal + deliveryFee - discount).coerceAtLeast(0.0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo(Screen.SHOP) }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Shopping Cart (${cartItems.size})",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
            }

            if (cartItems.isNotEmpty()) {
                TextButton(onClick = { viewModel.removeFromCart("") /* or clear */ }) {
                    // Quick clear or info
                }
            }
        }

        if (cartItems.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.RemoveShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = TextMuted
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your Cart is Empty",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Browse our gas bottles and appliances to add items to your cart.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.navigateTo(Screen.GAS_DELIVERY) },
                        colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Order LP Gas Now", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Cart Items
                items(cartItems, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                ProductVisual(imageType = item.imageType, label = item.bottleSize ?: "GasOn", modifier = Modifier.fillMaxSize())
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.productName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 2
                                )
                                Text(
                                    text = "R ${String.format("%.2f", item.price)} each",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "Subtotal: R ${String.format("%.2f", item.subtotal)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BluePrimary
                                )
                            }

                            // Stepper & Delete
                            Column(horizontalAlignment = Alignment.End) {
                                IconButton(
                                    onClick = { viewModel.removeFromCart(item.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Remove",
                                        tint = FlameRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .border(1.dp, BorderLight, RoundedCornerShape(6.dp))
                                        .background(Color(0xFFF8FAFC))
                                ) {
                                    IconButton(
                                        onClick = { viewModel.updateCartQuantity(item.id, item.quantity - 1) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(12.dp))
                                    }
                                    Text(
                                        text = item.quantity.toString(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                    IconButton(
                                        onClick = { viewModel.updateCartQuantity(item.id, item.quantity + 1) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Coupon Code Box
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "Have a Promo Coupon?", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Navy900)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = couponInput,
                                    onValueChange = { couponInput = it },
                                    placeholder = { Text("e.g. WELCOME10", fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = FlameOrange,
                                        unfocusedBorderColor = BorderLight
                                    )
                                )
                                Button(
                                    onClick = {
                                        val res = viewModel.applyCoupon(couponInput)
                                        Toast.makeText(context, res, Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Apply", fontSize = 12.sp)
                                }
                            }

                            if (couponCode.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Active: $couponCode (${if (isFreeDeliveryCoupon) "Free Delivery" else "$couponDiscountPercent% Off"})",
                                        color = DeliveryGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    TextButton(onClick = { viewModel.removeCoupon() }) {
                                        Text("Remove", color = FlameRed, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Cost Breakdown Summary
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "Order Summary", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Navy900)
                            Divider(color = BorderLight, modifier = Modifier.padding(vertical = 4.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Subtotal", fontSize = 12.sp, color = TextSecondary)
                                Text(text = "R ${String.format("%.2f", subtotal)}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Delivery Fee (Est)", fontSize = 12.sp, color = TextSecondary)
                                Text(
                                    text = if (deliveryFee == 0.0) "FREE" else "R ${String.format("%.2f", deliveryFee)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (deliveryFee == 0.0) DeliveryGreen else TextPrimary
                                )
                            }

                            if (discount > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Coupon Discount", fontSize = 12.sp, color = DeliveryGreen)
                                    Text(text = "- R ${String.format("%.2f", discount)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeliveryGreen)
                                }
                            }

                            Divider(color = BorderLight, modifier = Modifier.padding(vertical = 4.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Grand Total", fontSize = 15.sp, fontWeight = FontWeight.Black, color = Navy900)
                                Text(text = "R ${String.format("%.2f", grandTotal)}", fontSize = 17.sp, fontWeight = FontWeight.Black, color = BluePrimary)
                            }
                        }
                    }
                }
            }

            // Fixed Bottom Checkout Actions
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Proceed to Checkout Button
                    Button(
                        onClick = { viewModel.navigateTo(Screen.CHECKOUT) },
                        colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("cart_proceed_to_checkout_btn")
                    ) {
                        Text("Proceed to Checkout • R ${String.format("%.2f", grandTotal)}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Continue Shopping Button
                        OutlinedButton(
                            onClick = { viewModel.navigateTo(Screen.SHOP) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Text("Continue Shopping", fontSize = 11.sp)
                        }

                        // Order via WhatsApp Button
                        Button(
                            onClick = {
                                val summary = cartItems.joinToString("\n") { "- ${it.quantity}x ${it.productName}: R${it.subtotal}" }
                                val msg = """
                                    Hello GasOn,

                                    I would like to place an order from my cart:
                                    $summary

                                    Estimated Total: R ${String.format("%.2f", grandTotal)}

                                    Please confirm stock and delivery to my area.
                                """.trimIndent()
                                viewModel.openWhatsApp(context, msg)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("WhatsApp", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
