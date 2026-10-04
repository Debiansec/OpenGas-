package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GasCylinderCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.StoreViewModel

@Composable
fun GasDeliveryScreen(
    viewModel: StoreViewModel,
    modifier: Modifier = Modifier
) {
    val gasBottles by viewModel.gasBottles.collectAsState()
    val selectedFilter by viewModel.selectedBottleSizeFilter.collectAsState()

    val sizeFilters = listOf("ALL", "3 kg", "5 kg", "9 kg", "11 kg", "14 kg", "19 kg", "48 kg")

    val filteredBottles = remember(gasBottles, selectedFilter) {
        if (selectedFilter == "ALL") {
            gasBottles
        } else {
            gasBottles.filter { it.bottleSize == selectedFilter }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Banner Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_cylinders_lineup),
                        contentDescription = "Gas Cylinders Lineup",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    )
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "LP GAS DELIVERY",
                            color = FlameYellow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Order LP Gas Online",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Reliable Gas Cylinders for Home & Business • Various sizes • Great prices",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Size Filter Chips
        item {
            Text(
                text = "Select Cylinder Size:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Navy900,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(sizeFilters) { size ->
                    val isSelected = selectedFilter == size
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setSelectedBottleSizeFilter(size) },
                        label = { Text(size, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BluePrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_size_${size.lowercase().replace(" ", "_")}")
                    )
                }
            }
        }

        // Cylinder Cards
        items(filteredBottles, key = { it.id }) { cylinder ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                GasCylinderCard(
                    product = cylinder,
                    viewModel = viewModel,
                    onProductClick = { viewModel.openProductDetail(it) }
                )
            }
        }

        // Information Callout
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ℹ️ How Exchange Works",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = BluePrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "When selecting 'Exchange Bottle', ensure you have an empty cylinder in safe, roadworthy condition ready for our delivery driver. If you don't have an empty cylinder, select 'New Bottle'.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
