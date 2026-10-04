package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Product
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StoreViewModel

@Composable
fun HomeScreen(
    viewModel: StoreViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val products by viewModel.products.collectAsState()
    val gasBottles by viewModel.gasBottles.collectAsState()
    val settings by viewModel.appSettings.collectAsState()

    val domesticAppliances = remember(products) {
        products.filter { it.category in listOf("GEYSERS", "GAS STOVES", "HEATERS", "BRAAIS") }
    }

    val commercialAppliances = remember(products) {
        products.filter { it.category == "COMMERCIAL APPLIANCES" }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. Hero Promotional Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_delivery),
                            contentDescription = "GasOn Delivery Vehicle",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Gradient Overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Navy900.copy(alpha = 0.85f))
                                    )
                                )
                        )

                        // Top Pill
                        Surface(
                            color = DeliveryGreen,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .padding(12.dp)
                                .align(Alignment.TopStart)
                        ) {
                            Text(
                                text = "LOCAL MIDVAAL DELIVERY",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Running Out of Gas?",
                            color = FlameYellow,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "We've Got You Covered!",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            lineHeight = 28.sp
                        )
                        Text(
                            text = "Fast • Reliable • Local Gas Delivery",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.navigateTo(Screen.GAS_DELIVERY) },
                                colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("hero_order_gas_btn")
                            ) {
                                Text("Order Gas Now", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            Button(
                                onClick = { viewModel.navigateTo(Screen.SHOP) },
                                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("hero_shop_online_btn")
                            ) {
                                Text("Shop Online", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { viewModel.openWhatsApp(context) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .testTag("hero_whatsapp_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = null,
                                tint = Color(0xFF25D366),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WhatsApp Us Directly", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 2. Quick Navigation Shortcut Cards (Gas Delivery, Online Store, WhatsApp Sales, AI Assistant)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Card 1: Gas Delivery
                    QuickActionCard(
                        title = "Gas Delivery",
                        subtitle = "Fast & local refill",
                        icon = Icons.Default.LocalShipping,
                        iconTint = DeliveryGreen,
                        onClick = { viewModel.navigateTo(Screen.GAS_DELIVERY) },
                        modifier = Modifier.weight(1f)
                    )

                    // Card 2: Online Store
                    QuickActionCard(
                        title = "Online Store",
                        subtitle = "Geysers, stoves & braais",
                        icon = Icons.Default.Storefront,
                        iconTint = BluePrimary,
                        onClick = { viewModel.navigateTo(Screen.SHOP) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Card 3: WhatsApp Sales
                    QuickActionCard(
                        title = "WhatsApp Sales",
                        subtitle = "Chat for quick order",
                        icon = Icons.Default.Chat,
                        iconTint = Color(0xFF25D366),
                        onClick = { viewModel.openWhatsApp(context) },
                        modifier = Modifier.weight(1f)
                    )

                    // Card 4: AI Ordering
                    QuickActionCard(
                        title = "AI Ordering",
                        subtitle = "Smart cylinder selector",
                        icon = Icons.Default.SmartToy,
                        iconTint = FlameOrange,
                        onClick = { viewModel.toggleAiChat(true) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. Main Service Area Section
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = FlameOrange,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LP GAS DELIVERED TO YOUR DOOR",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Navy900
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "We deliver fast, reliable LP Gas directly to your home or business across:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AreaBadge("📍 Henley on Klip")
                        AreaBadge("📍 Meyerton")
                        AreaBadge("📍 Midvaal")
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✓ Free delivery threshold available • 🚚 Fast same-day dispatch",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = DeliveryGreen
                    )
                }
            }
        }

        // 4. Gas Cylinders Showcase
        item {
            Spacer(modifier = Modifier.height(22.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LP Gas Cylinders",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Navy900
                    )
                    Text(
                        text = "Exchange or buy new bottle with full gas",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                TextButton(onClick = { viewModel.navigateTo(Screen.GAS_DELIVERY) }) {
                    Text("View All", color = FlameOrange, fontWeight = FontWeight.Bold)
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = FlameOrange, modifier = Modifier.size(16.dp))
                }
            }
        }

        items(gasBottles.take(4)) { cylinder ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                GasCylinderCard(
                    product = cylinder,
                    viewModel = viewModel,
                    onProductClick = { viewModel.openProductDetail(it) }
                )
            }
        }

        // 5. Domestic Appliances Showcase Banner & Grid
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_appliances_showcase),
                        contentDescription = "Gas Appliances Showcase",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    )
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "DOMESTIC GAS APPLIANCES",
                            color = FlameYellow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Loadshedding Solutions for Home",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Geysers, 2-Burner Hobs, Space Heaters & Braais.",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(domesticAppliances) { appliance ->
                    Box(modifier = Modifier.width(220.dp)) {
                        ProductCard(
                            product = appliance,
                            viewModel = viewModel,
                            onProductClick = { viewModel.openProductDetail(it) }
                        )
                    }
                }
            }
        }

        // 6. Commercial Appliances Section
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Commercial Gas Appliances",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Navy900
                    )
                    Text(
                        text = "Heavy duty catering & restaurant equipment",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(commercialAppliances) { commercial ->
                    Box(modifier = Modifier.width(220.dp)) {
                        ProductCard(
                            product = commercial,
                            viewModel = viewModel,
                            onProductClick = { viewModel.openProductDetail(it) }
                        )
                    }
                }
            }
        }

        // 7. Why Choose GasOn
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Why choose GasOn?",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Navy900
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val benefits = listOf(
                        "✓ Quick local delivery to your door",
                        "✓ Safe, certified SANS approved cylinders",
                        "✓ Friendly, dependable Midvaal service",
                        "✓ Competitive pricing & transparent fees",
                        "✓ Convenient online & WhatsApp ordering",
                        "✓ Delivery and depot collection options"
                    )

                    benefits.forEach { benefit ->
                        Text(
                            text = benefit,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // 8. Gas Safety & Installation Services Quick Cards
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Safety Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.navigateTo(Screen.SAFETY) }
                        .testTag("home_safety_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = FlameRed, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Gas Safety", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FlameRed)
                        Text(text = "What to do if you smell gas & leak checks", fontSize = 11.sp, color = TextSecondary)
                    }
                }

                // Installation Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.navigateTo(Screen.INSTALLATION) }
                        .testTag("home_installation_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(imageVector = Icons.Default.Build, contentDescription = null, tint = DeliveryGreen, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Installation", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DeliveryGreen)
                        Text(text = "Certified gas geyser & stove installations", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }
        }

        // Admin Section Portal Card
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { viewModel.navigateTo(Screen.ADMIN) }
                    .testTag("home_admin_portal_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E3A8A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin Portal",
                                tint = FlameYellow,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Admin Section",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = FlameOrange,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "DEPOT PORTAL",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Manage orders, update stock & approve EFTs",
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 9. Contact Section
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Navy800)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Contact GasOn",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Phone: ${settings.contactPhone}", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                    Text(text = "WhatsApp: ${settings.whatsAppNumber}", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                    Text(text = "Email: ${settings.contactEmail}", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                    Text(text = "Depot: ${settings.depotAddress}", color = Color(0xFFCBD5E1), fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.makePhoneCall(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call Now", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.openWhatsApp(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("WhatsApp", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 10. Footer
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Navy900)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(4.dp),
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
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "On",
                        color = GasOnOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = "Fast. Reliable. Local Gas Delivery.",
                    color = FlameYellow,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Delivering to Henley on Klip, Meyerton, Midvaal & surroundings.\nFor gas installations or certification, contact a suitably qualified professional.\n© 2026 GasOn. All rights reserved.",
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    lineHeight = 15.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag("quick_action_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun AreaBadge(text: String) {
    Surface(
        color = Color(0xFFF1F5F9),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Navy900,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
