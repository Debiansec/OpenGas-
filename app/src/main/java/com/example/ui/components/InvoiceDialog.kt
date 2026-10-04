package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Order
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InvoiceDialog(
    order: Order,
    onDismiss: () -> Unit,
    onShareWhatsApp: (String) -> Unit
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(order.createdAt))

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(20.dp))
                .testTag("invoice_dialog"),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Top Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TAX INVOICE",
                        color = Navy900,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Brand Header Box
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "GasOn (Pty) Ltd",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = BluePrimary
                                )
                                Text(
                                    text = "LP Gas Online Store & Delivery",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "Meyerton & Midvaal Depot",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = order.orderNumber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = dateStr,
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Customer Info
                Text(text = "BILLED TO:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                Text(text = order.customerName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = "Phone / WhatsApp: ${order.customerMobile}", fontSize = 12.sp, color = TextSecondary)
                Text(text = "Email: ${order.customerEmail}", fontSize = 12.sp, color = TextSecondary)

                if (order.isDelivery) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "DELIVERY ADDRESS:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text(text = "${order.deliveryAddress}, ${order.deliverySuburb}, ${order.deliveryCity} (${order.deliveryPostalCode})", fontSize = 12.sp, color = TextSecondary)
                    if (order.deliveryInstructions.isNotBlank()) {
                        Text(text = "Note: ${order.deliveryInstructions}", fontSize = 11.sp, color = FlameOrange)
                    }
                } else {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "COLLECTION AT DEPOT:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text(text = "12 Loch Street, Meyerton Central (${order.collectionSlot})", fontSize = 12.sp, color = TextSecondary)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Items summary
                Text(text = "ORDER ITEMS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Navy900)
                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = order.itemsSummary,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Financial Breakdown
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF8FAFC))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Subtotal:", fontSize = 13.sp, color = TextSecondary)
                        Text(text = "R ${String.format("%.2f", order.subtotal)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Delivery Fee:", fontSize = 13.sp, color = TextSecondary)
                        Text(text = "R ${String.format("%.2f", order.deliveryFee)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }

                    if (order.discountAmount > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Discount Applied:", fontSize = 13.sp, color = DeliveryGreen)
                            Text(text = "- R ${String.format("%.2f", order.discountAmount)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DeliveryGreen)
                        }
                    }

                    Divider(color = BorderLight, modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "TOTAL AMOUNT:", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Navy900)
                        Text(text = "R ${String.format("%.2f", order.total)}", fontSize = 17.sp, fontWeight = FontWeight.Black, color = BluePrimary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Payment & Order Status Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Payment: ${order.paymentMethod}", fontSize = 12.sp, color = TextSecondary)
                    Surface(
                        color = if (order.paymentStatus == "CONFIRMED") DeliveryGreen else FlameOrange,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = order.paymentStatus,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Print & Share Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "Invoice #${order.orderNumber} saved to downloads", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("download_invoice_button")
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Download PDF")
                    }

                    Button(
                        onClick = {
                            val shareText = "Tax Invoice: ${order.orderNumber}\nCustomer: ${order.customerName}\nTotal: R${String.format("%.2f", order.total)}\nStatus: ${order.orderStatus}"
                            onShareWhatsApp(shareText)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_invoice_whatsapp")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share", color = Color.White)
                    }
                }
            }
        }
    }
}
