package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoreViewModel

@Composable
fun GasCylinderCard(
    product: Product,
    viewModel: StoreViewModel,
    onProductClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isExchange by remember { mutableStateOf(true) }
    var quantity by remember { mutableIntStateOf(1) }

    val currentPrice = if (isExchange) product.price else (product.newBottlePrice ?: product.price)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("gas_cylinder_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onProductClick(product) },
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Visual Graphic
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    ProductVisual(
                        imageType = product.imageType,
                        label = product.bottleSize ?: "GasOn",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Info
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = product.bottleSize ?: "LP Gas",
                                color = BluePrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        if (product.stock > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(DeliveryGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "In Stock (${product.stock})",
                                    color = DeliveryGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = product.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = product.tagline.ifBlank { product.description },
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Price
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "R ${String.format("%.0f", currentPrice)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = BluePrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isExchange) "Refill / Exchange" else "Full Bottle + Gas",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Exchange vs New Toggle Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Exchange Option
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isExchange) BluePrimary else Color.Transparent)
                        .clickable { isExchange = true }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Exchange Bottle (R${String.format("%.0f", product.price)})",
                        color = if (isExchange) Color.White else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isExchange) FontWeight.Bold else FontWeight.Normal
                    )
                }

                // New Bottle Option
                val newPrice = product.newBottlePrice ?: (product.price + 500)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (!isExchange) FlameOrange else Color.Transparent)
                        .clickable { isExchange = false }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "New Bottle (R${String.format("%.0f", newPrice)})",
                        color = if (!isExchange) Color.White else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (!isExchange) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action row: Stepper, Add to Cart, WhatsApp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity Stepper
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
                        .background(Color(0xFFF8FAFC))
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = TextPrimary, modifier = Modifier.size(14.dp))
                    }
                    Text(
                        text = quantity.toString(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                    IconButton(
                        onClick = { quantity++ },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = TextPrimary, modifier = Modifier.size(14.dp))
                    }
                }

                // Add to Cart Button
                Button(
                    onClick = {
                        viewModel.addToCart(product, isExchange, quantity)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("add_to_cart_cylinder_${product.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Add to Cart", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // WhatsApp Order Icon
                IconButton(
                    onClick = {
                        val option = if (isExchange) "Exchange" else "New Bottle"
                        val msg = "Hello GasOn, I would like to order: $quantity x ${product.name} ($option, R${currentPrice * quantity}). Please confirm delivery."
                        viewModel.openWhatsApp(context, msg)
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF25D366))
                        .testTag("whatsapp_order_cylinder_${product.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "Order via WhatsApp",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    viewModel: StoreViewModel,
    onProductClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProductClick(product) }
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Visual header with badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                ProductVisual(
                    imageType = product.imageType,
                    label = product.bottleSize ?: "GasOn",
                    modifier = Modifier.fillMaxSize()
                )

                if (product.isSpecialOffer && product.salePrice != null) {
                    Surface(
                        color = FlameRed,
                        shape = RoundedCornerShape(bottomEnd = 8.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "SPECIAL",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.category,
                    color = FlameOrange,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = product.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Price Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val displayPrice = product.salePrice ?: product.price
                        Text(
                            text = "R ${String.format("%.0f", displayPrice)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = BluePrimary
                        )
                        if (product.salePrice != null) {
                            Text(
                                text = "R ${String.format("%.0f", product.price)}",
                                fontSize = 11.sp,
                                color = TextMuted,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }

                    // Add Button
                    IconButton(
                        onClick = {
                            viewModel.addToCart(product, isExchange = true, quantity = 1)
                        },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(FlameOrange)
                            .testTag("add_to_cart_btn_${product.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "Add to Cart",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductListItem(
    product: Product,
    viewModel: StoreViewModel,
    onProductClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val inCartItem = remember(cartItems, product.id) {
        cartItems.firstOrNull { it.productId == product.id }
    }
    val inCartQuantity = inCartItem?.quantity ?: 0
    val displayPrice = product.salePrice ?: product.price

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProductClick(product) }
            .testTag("product_list_item_${product.id}"),
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
            // Product Visual / Image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                ProductVisual(
                    imageType = product.imageType,
                    label = product.bottleSize ?: "GasOn",
                    modifier = Modifier.fillMaxSize()
                )

                if (product.isSpecialOffer && product.salePrice != null) {
                    Surface(
                        color = FlameRed,
                        shape = RoundedCornerShape(bottomEnd = 6.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "SALE",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details Column
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = product.category,
                        color = BluePrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = product.tagline.ifBlank { product.description },
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "R ${String.format("%.2f", displayPrice)}",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = BluePrimary
                    )

                    if (product.salePrice != null) {
                        Text(
                            text = "R ${String.format("%.0f", product.price)}",
                            fontSize = 11.sp,
                            color = TextMuted,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }

                    if (product.stock > 0) {
                        Text(
                            text = "• In Stock (${product.stock})",
                            fontSize = 10.sp,
                            color = DeliveryGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // State-managed Cart Actions (Add / Stepper / Remove)
            if (inCartQuantity > 0 && inCartItem != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
                            .background(Color(0xFFF8FAFC))
                    ) {
                        IconButton(
                            onClick = {
                                if (inCartQuantity > 1) {
                                    viewModel.updateCartQuantity(inCartItem.id, inCartQuantity - 1)
                                } else {
                                    viewModel.removeFromCart(inCartItem.id)
                                }
                            },
                            modifier = Modifier
                                .size(30.dp)
                                .testTag("list_decrease_cart_${product.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease",
                                tint = TextPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Text(
                            text = inCartQuantity.toString(),
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )

                        IconButton(
                            onClick = {
                                viewModel.updateCartQuantity(inCartItem.id, inCartQuantity + 1)
                            },
                            modifier = Modifier
                                .size(30.dp)
                                .testTag("list_increase_cart_${product.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = TextPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Text(
                        text = "In Cart",
                        fontSize = 9.sp,
                        color = DeliveryGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Button(
                    onClick = {
                        viewModel.addToCart(product, isExchange = true, quantity = 1)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("list_add_to_cart_${product.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Add", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

