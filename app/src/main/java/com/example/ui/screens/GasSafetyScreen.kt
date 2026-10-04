package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
fun GasSafetyScreen(
    viewModel: StoreViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategoryTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All Guidelines", "Cylinder Handling", "Leak Detection", "Appliance Care")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Top Header
        Surface(
            color = Navy900,
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        modifier = Modifier.testTag("safety_back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Gas Safety & Guidelines",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Safe handling, leak detection & appliance maintenance",
                            color = FlameYellow,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category Tabs Strip
                ScrollableTabRow(
                    selectedTabIndex = selectedCategoryTab,
                    containerColor = Navy900,
                    contentColor = Color.White,
                    edgePadding = 0.dp
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedCategoryTab == index,
                            onClick = { selectedCategoryTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedCategoryTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedCategoryTab == index) FlameOrange else Color(0xFFCBD5E1)
                                )
                            },
                            modifier = Modifier.testTag("safety_tab_$index")
                        )
                    }
                }
            }
        }

        // Scrollable Safety Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. MANDATORY LEGAL & PROFESSIONAL DISCLAIMER BANNER
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("safety_legal_disclaimer_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(BluePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = "Certified Professional Disclaimer",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "MANDATORY PROFESSIONAL DISCLAIMER",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = BluePrimary,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "South African SANS 10087 & SAQCC Gas Regulations",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "IMPORTANT NOTICE: For all gas appliance installations, internal piping alterations, regulator replacements, cylinder manifolds, or safety certifications (Certificate of Compliance - CoC), YOU MUST CONTACT A REGISTERED, SUITABLY QUALIFIED SAQCC GAS PROFESSIONAL.\n\nGasOn strictly advises against uncertified DIY repairs or installations. Uncertified work is illegal under South African OHS law and nullifies building fire insurance policies.",
                        fontSize = 11.sp,
                        color = Navy900,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 16.sp
                    )
                }
            }

            // 2. EMERGENCY RED BANNER: WHAT TO DO IF YOU SMELL GAS
            if (selectedCategoryTab in listOf(0, 2)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("smell_gas_emergency_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    border = BorderStroke(1.dp, FlameRed.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(FlameRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "WHAT TO DO IF YOU SMELL GAS",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = FlameRed
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        val emergencySteps = listOf(
                            "1. Extinguish all naked flames, cigarettes, braai coals, and pilot lights immediately.",
                            "2. Do NOT operate any electrical switches, light switches, plugs, or doorbells.",
                            "3. Do NOT use telephones or mobile phones anywhere near the suspected leak.",
                            "4. Close the gas cylinder valve tightly by turning the handwheel clockwise.",
                            "5. Open all doors and windows fully to allow fresh natural cross-ventilation.",
                            "6. Vacate the building immediately and call our emergency dispatch or fire services from outdoors."
                        )
                        emergencySteps.forEach { step ->
                            Text(
                                text = step,
                                fontSize = 12.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // 3. SECTION: CYLINDER HANDLING GUIDELINES
            if (selectedCategoryTab in listOf(0, 1)) {
                SafetySectionHeader(
                    title = "Cylinder Handling Guidelines",
                    subtitle = "Safe usage, transport, and outdoor storage standards"
                )

                DetailedSafetyCard(
                    icon = Icons.Default.VerticalAlignTop,
                    iconTint = BluePrimary,
                    title = "The Upright Rule (Always Standing)",
                    content = "LP Gas cylinders must ALWAYS be stored, transported, and operated in an upright position. Liquid petroleum gas resides under pressure at the bottom of the bottle. Lying a cylinder flat allows liquid LPG into the regulator and appliance, resulting in uncontrollable flaring and dangerous pressure surges."
                )

                DetailedSafetyCard(
                    icon = Icons.Default.FitnessCenter,
                    iconTint = FlameOrange,
                    title = "Safe Lifting & Carrying Technique",
                    content = "Always lift gas cylinders using your legs, not your back. Firmly grip the protective collar handle with both hands. Never roll cylinders horizontally along the ground, drag them by the valve, or throw them onto vehicle load beds."
                )

                DetailedSafetyCard(
                    icon = Icons.Default.Home,
                    iconTint = DeliveryGreen,
                    title = "Safe Storage Location & Clearance",
                    content = "Store cylinders outdoors in a secure, well-ventilated lockable cage on a level concrete base. Keep bottles at least 1 meter away from doors, windows, and electrical outlets, and at least 2 meters from unsealed drains, gullies, or basement pits, as LPG vapor is heavier than air and sinks."
                )

                DetailedSafetyCard(
                    icon = Icons.Default.DirectionsCar,
                    iconTint = Navy900,
                    title = "Safe Vehicle Transporting",
                    content = "When collecting or transporting cylinders in a car or bakkie, secure the bottle upright using straps. Keep windows cracked open for constant airflow. Never transport cylinders lying down or leave them unattended inside a closed, hot vehicle boot."
                )

                DetailedSafetyCard(
                    icon = Icons.Default.Lock,
                    iconTint = TextSecondary,
                    title = "Valve Closure & Empty Bottle Protocol",
                    content = "Always turn cylinder handwheels clockwise until firmly seated when shutting off appliances. Treat empty cylinders with the exact same care as full cylinders—empty bottles still contain flammable residual vapor under pressure."
                )
            }

            // 4. SECTION: LEAK DETECTION GUIDELINES
            if (selectedCategoryTab in listOf(0, 2)) {
                SafetySectionHeader(
                    title = "Leak Detection Procedures",
                    subtitle = "Identifying leaks safely without creating ignition hazards"
                )

                DetailedSafetyCard(
                    icon = Icons.Default.Air,
                    iconTint = FlameOrange,
                    title = "The Rotten Egg Odor Warning",
                    content = "Pure LPG has no natural odor. An odorant chemical (Ethyl Mercaptan) is intentionally added during refining to produce a sharp, pungent 'rotten cabbage' or sulfur smell so even minute leaks can be detected by human smell immediately."
                )

                DetailedSafetyCard(
                    icon = Icons.Default.BubbleChart,
                    iconTint = BluePrimary,
                    title = "The Official Soapy Water Bubble Test",
                    content = "Step-by-Step Test:\n1. Mix water with dishwashing liquid soap in a small bowl or spray bottle.\n2. Brush or spray the soapy solution over the cylinder valve connection, regulator nut, bullnose O-ring, and hose joints.\n3. Open the cylinder valve.\n4. If bubbles form and grow, there is a gas leak.\n5. Turn off valve immediately, tighten the connection or replace perished seal, and re-test."
                )

                DetailedSafetyCard(
                    icon = Icons.Default.LocalFireDepartment,
                    iconTint = FlameRed,
                    title = "STRICT RULE: Never Use an Open Flame",
                    content = "NEVER use matches, candles, cigarette lighters, or open sparks to test for gas leaks! Doing so can ignite concentrated vapor pockets and cause an immediate explosion or flash fire."
                )
            }

            // 5. SECTION: APPLIANCE MAINTENANCE
            if (selectedCategoryTab in listOf(0, 3)) {
                SafetySectionHeader(
                    title = "Appliance Maintenance Guidelines",
                    subtitle = "Keeping geysers, hobs, heaters, and regulators in top condition"
                )

                DetailedSafetyCard(
                    icon = Icons.Default.Whatshot,
                    iconTint = BluePrimary,
                    title = "Flame Color & Combustion Health",
                    content = "A healthy LPG flame is crisp, sharp, and solid BLUE with light blue inner cones. A lazy, yellow, or sooty flame indicates incomplete combustion, clogged burner jets, or improper air-fuel mixture, generating toxic Carbon Monoxide (CO). If flame turns yellow, shut off appliance and call a technician."
                )

                DetailedSafetyCard(
                    icon = Icons.Default.WaterDamage,
                    iconTint = DeliveryGreen,
                    title = "Gas Geyser (Water Heater) Servicing",
                    content = "Outdoor gas geysers must be inspected annually. Check the flue exhaust for bird nests, soot, or debris. Clean the burner assembly and ensure the pressure relief valve and freeze-protection drain plugs function properly before winter."
                )

                DetailedSafetyCard(
                    icon = Icons.Default.Kitchen,
                    iconTint = FlameOrange,
                    title = "Gas Hobs & Stoves Cleaning",
                    content = "Keep burner caps and jet ports free of boiled-over food, grease, and carbon buildup. Regularly verify that the Flame Failure Device (FFD) works: if a burner flame blows out, the gas supply should automatically cut off within 30 seconds."
                )

                DetailedSafetyCard(
                    icon = Icons.Default.Thermostat,
                    iconTint = Navy900,
                    title = "Rollabout Gas Heaters (Winter Safety)",
                    content = "Vacuum dust and pet hair from ceramic plaques before winter. Ensure the Oxygen Depletion Sensor (ODS) and tilt-safety cut-off mechanism operate correctly. Never sleep with a portable gas heater operating in an unventilated bedroom."
                )

                DetailedSafetyCard(
                    icon = Icons.Default.Loop,
                    iconTint = FlameRed,
                    title = "Hoses & Regulators Replacement Intervals",
                    content = "Only use SANS-certified orange LPG hose. Inspect hoses every 3 months for micro-cracks, stiffness, or perishing. Orange rubber hose MUST be replaced every 3 to 5 years. Domestic regulators must be replaced every 5 to 10 years or whenever the rubber diaphragm hardens."
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 6. Action Cards: Book Certified Professional & Emergency Call
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Need Certified Gas Assistance?",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Book registered SAQCC gas technicians for geyser installations, stove connections, or emergency leak response in Midvaal.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.navigateTo(Screen.INSTALLATION) },
                            colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("safety_book_installation_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Book Tech", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.makePhoneCall(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("safety_emergency_call_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call Depot", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun SafetySectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = Navy900
        )
        Text(
            text = subtitle,
            fontSize = 11.sp,
            color = TextSecondary
        )
    }
}

@Composable
fun DetailedSafetyCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Navy900
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = content,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp
                )
            }
        }
    }
}
