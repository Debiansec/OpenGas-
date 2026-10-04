package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StoreViewModel

@Composable
fun InstallationRequestScreen(
    viewModel: StoreViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("Johan van der Merwe") }
    var phone by remember { mutableStateOf("082 123 4567") }
    var email by remember { mutableStateOf("johan@example.co.za") }
    var address by remember { mutableStateOf("Henley on Klip / Meyerton") }
    var selectedType by remember { mutableStateOf("Gas Geyser Installation") }
    var preferredDate by remember { mutableStateOf("Next available weekday") }
    var message by remember { mutableStateOf("Need replacement of electric geyser with 12L outdoor gas geyser.") }
    var hasPhoto by remember { mutableStateOf(false) }
    var isSubmitted by remember { mutableStateOf(false) }

    val serviceTypes = listOf(
        "Gas Geyser Installation",
        "Gas Hob / Stove Installation",
        "Gas Braai Installation",
        "Gas Space Heater Line",
        "Commercial Catering Gas Line",
        "Safety Inspection & CoC Certificate"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Top Bar
        Surface(
            color = Navy900,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(Screen.HOME) }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Gas Installation Services",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "Certified SAQG registered installations & CoC certification",
                        color = FlameYellow,
                        fontSize = 11.sp
                    )
                }
            }
        }

        if (isSubmitted) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = DeliveryGreen, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(text = "Request Received!", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Navy900)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Our qualified gas installation team will contact you within 24 hours to confirm appointment details and provide a preliminary quotation.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                    ) {
                        Text("Return to Home")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "All installations are carried out according to SANS 10087 standards and include Certificate of Compliance (CoC) issuance.",
                            fontSize = 11.sp,
                            color = Navy900,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Text(text = "Select Service Type:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Navy900)
                serviceTypes.forEach { type ->
                    val isSelected = selectedType == type
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedType = type },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFFFEDD5) else Color.White),
                        border = if (isSelected) CardDefaults.outlinedCardBorder() else null
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = isSelected, onClick = { selectedType = type })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = type, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) FlameOrangeDark else TextPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Your Details:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Navy900)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name *") },
                    modifier = Modifier.fillMaxWidth().testTag("install_name_input"),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone / WhatsApp *") },
                    modifier = Modifier.fillMaxWidth().testTag("install_phone_input"),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Site Address (Meyerton / Henley on Klip / Midvaal)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = preferredDate,
                    onValueChange = { preferredDate = it },
                    label = { Text("Preferred Installation Date / Window") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Scope details or questions") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 3
                )

                // Simulated Photo Upload
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Attach Installation Site Photos", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = if (hasPhoto) "✓ 2 photos attached" else "Optional: helps with quoting", fontSize = 10.sp, color = TextMuted)
                    }
                    Button(
                        onClick = {
                            hasPhoto = !hasPhoto
                            Toast.makeText(context, if (hasPhoto) "Site photos attached" else "Photo removed", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (hasPhoto) DeliveryGreen else BluePrimary),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(if (hasPhoto) "Attached" else "Add Photos", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Buttons
                Button(
                    onClick = {
                        if (name.isBlank() || phone.isBlank()) {
                            Toast.makeText(context, "Please enter your name and phone number", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.submitInstallationRequest(
                            name = name,
                            phone = phone,
                            email = email,
                            address = address,
                            type = selectedType,
                            date = preferredDate,
                            message = message,
                            hasPhoto = hasPhoto,
                            onSuccess = { isSubmitted = true }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("install_submit_btn")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Submit Installation Request", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = {
                        val text = "Hello GasOn, I would like to request an installation quote for: $selectedType at $address."
                        viewModel.openWhatsApp(context, text)
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("WhatsApp Installation Enquiry", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
