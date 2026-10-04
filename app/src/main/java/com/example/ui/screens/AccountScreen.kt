package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StoreViewModel

@Composable
fun AccountScreen(
    viewModel: StoreViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val customerName by viewModel.customerName.collectAsState()
    val customerMobile by viewModel.customerMobile.collectAsState()
    val customerEmail by viewModel.customerEmail.collectAsState()
    val orders by viewModel.orders.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Profile Banner
        item {
            Surface(
                color = Navy900,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(FlameOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = customerName.take(1).uppercase(),
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = customerName,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "$customerMobile • $customerEmail",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = Navy700,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "📍 Meyerton & Henley on Klip Zone",
                            color = DeliveryGreenLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Account Menu List
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "My Activity", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Navy900)

                AccountMenuItem(
                    icon = Icons.Default.LocalShipping,
                    iconTint = BluePrimary,
                    title = "My Orders (${orders.size})",
                    subtitle = "Track active gas deliveries & view past invoices",
                    onClick = { viewModel.navigateTo(Screen.ORDER_TRACKING) },
                    testTag = "account_my_orders"
                )

                AccountMenuItem(
                    icon = Icons.Default.ShoppingCart,
                    iconTint = FlameOrange,
                    title = "My Shopping Cart",
                    subtitle = "Review bottles and appliances in your cart",
                    onClick = { viewModel.navigateTo(Screen.CART) },
                    testTag = "account_my_cart"
                )

                AccountMenuItem(
                    icon = Icons.Default.Build,
                    iconTint = DeliveryGreen,
                    title = "Installation Requests",
                    subtitle = "Gas geysers, hobs & CoC certification bookings",
                    onClick = { viewModel.navigateTo(Screen.INSTALLATION) },
                    testTag = "account_installations"
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Safety & Support", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Navy900)

                AccountMenuItem(
                    icon = Icons.Default.Shield,
                    iconTint = FlameRed,
                    title = "Gas Safety Guidelines",
                    subtitle = "Emergency steps, leak checks & safe cylinder handling",
                    onClick = { viewModel.navigateTo(Screen.SAFETY) },
                    testTag = "account_safety"
                )

                AccountMenuItem(
                    icon = Icons.Default.Phone,
                    iconTint = BluePrimary,
                    title = "Call GasOn Depot",
                    subtitle = "016 362 5555 • Local assistance",
                    onClick = { viewModel.makePhoneCall(context) },
                    testTag = "account_call"
                )

                AccountMenuItem(
                    icon = Icons.Default.Chat,
                    iconTint = Color(0xFF25D366),
                    title = "WhatsApp Customer Support",
                    subtitle = "Instant order inquiries & delivery scheduling",
                    onClick = { viewModel.openWhatsApp(context) },
                    testTag = "account_whatsapp"
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Administration & Management", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Navy900)

                // Admin Dashboard Link
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(Screen.ADMIN) }
                        .testTag("account_admin_portal_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Navy900)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = FlameYellow, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "Admin Dashboard", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "Manage products, orders, EFT verification & zones", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                            }
                        }
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .clickable { viewModel.openSplash() }
                            .padding(4.dp)
                            .testTag("account_replay_splash_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_gason_logo),
                            contentDescription = "GasOn Logo",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Gas",
                            color = Navy900,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "On",
                            color = GasOnOrange,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }
                    Text(
                        text = "Fast • Reliable • Local Gas Delivery",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                    Text(
                        text = "Meyerton • Henley on Klip • Midvaal",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun AccountMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                    Text(text = subtitle, fontSize = 11.sp, color = TextSecondary)
                }
            }
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
        }
    }
}
