package com.example.ui.screens

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
import com.example.data.model.Product
import com.example.ui.components.ProductVisual
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoreViewModel

@Composable
fun ProductDetailScreen(
    product: Product,
    viewModel: StoreViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isExchange by remember { mutableStateOf(true) }
    var quantity by remember { mutableIntStateOf(1) }

    val currentPrice = if (product.bottleSize != null) {
        if (isExchange) product.price else (product.newBottlePrice ?: product.price)
    } else {
        product.salePrice ?: product.price
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("detail_back_button")
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Text(
                text = "Product Details",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Navy900
            )

            IconButton(
                onClick = {
                    val msg = "Hello GasOn, I would like to enquire about: ${product.name} (SKU: ${product.sku})."
                    viewModel.openWhatsApp(context, msg)
                }
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = TextSecondary)
            }
        }

        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Visual Image Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                ProductVisual(
                    imageType = product.imageType,
                    label = product.bottleSize ?: "GasOn",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category & Stock
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFFFEDD5),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = product.category,
                        color = FlameOrangeDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "✓ In Stock (${product.stock} units)",
                    color = DeliveryGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = product.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Navy900
            )

            Text(
                text = "SKU: ${product.sku}",
                fontSize = 11.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Price
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "R ${String.format("%.2f", currentPrice)}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = BluePrimary
                )
                if (product.bottleSize != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isExchange) "(Exchange Price)" else "(Full Cylinder + Gas)",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            // Cylinder option selector if cylinder
            if (product.bottleSize != null) {
                Spacer(modifier = Modifier.height(14.dp))
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
                            .background(if (isExchange) BluePrimary else Color.Transparent)
                            .clickable { isExchange = true }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Exchange Bottle\nR${String.format("%.0f", product.price)}",
                            color = if (isExchange) Color.White else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    val newPrice = product.newBottlePrice ?: (product.price + 500)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!isExchange) FlameOrange else Color.Transparent)
                            .clickable { isExchange = false }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "New Bottle\nR${String.format("%.0f", newPrice)}",
                            color = if (!isExchange) Color.White else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Trust Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TrustBadge(Icons.Default.VerifiedUser, "Safe &\nCertified", DeliveryGreen)
                TrustBadge(Icons.Default.LocalShipping, "Fast\nDelivery", BluePrimary)
                TrustBadge(Icons.Default.ThumbUp, "High\nQuality", FlameOrange)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(text = "Description", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Navy900)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = product.description,
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 19.sp
            )

            if (product.specifications.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(text = "Specifications", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Navy900)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.specifications,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // Bottom Fixed Bar: Stepper & CTA
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quantity Stepper
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                            .background(Color(0xFFF8FAFC))
                    ) {
                        IconButton(onClick = { if (quantity > 1) quantity-- }, modifier = Modifier.size(42.dp)) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease")
                        }
                        Text(
                            text = quantity.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(onClick = { quantity++ }, modifier = Modifier.size(42.dp)) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Increase")
                        }
                    }

                    // Add to Cart Button
                    Button(
                        onClick = {
                            viewModel.addToCart(product, isExchange, quantity)
                            onBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("detail_add_to_cart_btn")
                    ) {
                        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add to Cart • R${String.format("%.0f", currentPrice * quantity)}", fontWeight = FontWeight.Bold)
                    }
                }

                // WhatsApp Enquiry Button
                OutlinedButton(
                    onClick = {
                        val option = if (product.bottleSize != null) (if (isExchange) "Exchange" else "New Bottle") else "Standard"
                        val msg = "Hello GasOn, I would like to order $quantity x ${product.name} ($option, R${currentPrice * quantity}). Please confirm availability."
                        viewModel.openWhatsApp(context, msg)
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("detail_whatsapp_btn")
                ) {
                    Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Chat / Order on WhatsApp", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TrustBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Navy900,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 12.sp
        )
    }
}
