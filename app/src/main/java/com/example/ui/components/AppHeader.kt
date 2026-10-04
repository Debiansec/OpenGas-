package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
fun AppHeader(
    viewModel: StoreViewModel,
    cartItemCount: Int,
    cartSubtotal: Double = 0.0,
    onOpenCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Surface(
        color = Navy900,
        shadowElevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Top Row: Brand Logo, Emergency Button, Cart Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // GasOn Logo & Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(Screen.HOME) }
                        .testTag("header_logo")
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_gason_logo),
                            contentDescription = "GasOn Logo",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
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
                            text = "LP GAS STORE & DELIVERY",
                            color = GasOnYellow,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                // Action Icons (Search, Phone, WhatsApp, Cart)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Global Search Button
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.SHOP) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Navy800)
                            .testTag("header_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Products",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Quick Call
                    IconButton(
                        onClick = { viewModel.makePhoneCall(context) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Navy800)
                            .testTag("quick_call_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Call GasOn",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Quick WhatsApp
                    IconButton(
                        onClick = { viewModel.openWhatsApp(context) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF25D366))
                            .testTag("header_whatsapp_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "WhatsApp GasOn",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Cart with Badge and Live Subtotal
                    Surface(
                        color = if (cartSubtotal > 0) BrandOrange else Navy700,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .clickable(onClick = onOpenCart)
                            .testTag("header_cart_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            BadgedBox(
                                badge = {
                                    if (cartItemCount > 0) {
                                        Badge(
                                            containerColor = if (cartSubtotal > 0) Color.White else BrandOrange,
                                            contentColor = if (cartSubtotal > 0) BrandOrange else Color.White
                                        ) {
                                            Text(text = cartItemCount.toString(), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Shopping Cart",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            if (cartSubtotal > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "R ${String.format("%.2f", cartSubtotal)}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sub-strip: Delivery Area indicator & Emergency "NEED GAS NOW?"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Navy800)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Delivery Area",
                        tint = DeliveryGreenLight,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Delivering: Meyerton • Henley • Midvaal",
                        color = Color(0xFFE2E8F0),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Emergency Quick Order Pill
                Surface(
                    color = FlameRed,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(Screen.GAS_DELIVERY) }
                        .testTag("need_gas_now_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Emergency Gas",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "NEED GAS NOW?",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}
