package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.ui.components.ProductCard
import com.example.ui.components.ProductListItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StoreViewModel

enum class CatalogViewMode {
    LIST,
    GRID
}

@Composable
fun ShopScreen(
    viewModel: StoreViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val cartSubtotal by viewModel.cartSubtotal.collectAsState()
    val cartItemCount by viewModel.cartItemCount.collectAsState()

    var viewMode by remember { mutableStateOf(CatalogViewMode.LIST) }

    val categories = listOf(
        "ALL",
        "GAS BOTTLES",
        "GEYSERS",
        "GAS STOVES",
        "HEATERS",
        "BRAAIS",
        "COMMERCIAL APPLIANCES",
        "REGULATORS",
        "HOSES",
        "ACCESSORIES",
        "SPECIAL OFFERS"
    )

    val quickSearches = listOf(
        "9kg",
        "19kg",
        "Geyser",
        "Heater",
        "Braai",
        "Stove",
        "Regulator"
    )

    val filteredProducts = remember(products, searchQuery, selectedCategory) {
        products.filter { product ->
            val matchesCategory = when (selectedCategory) {
                "ALL" -> true
                "SPECIAL OFFERS" -> product.isSpecialOffer
                else -> product.category.equals(selectedCategory, ignoreCase = true)
            }

            val matchesSearch = if (searchQuery.isBlank()) true else {
                product.name.contains(searchQuery, ignoreCase = true) ||
                product.description.contains(searchQuery, ignoreCase = true) ||
                product.category.contains(searchQuery, ignoreCase = true) ||
                product.sku.contains(searchQuery, ignoreCase = true) ||
                (product.bottleSize?.contains(searchQuery, ignoreCase = true) == true)
            }

            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Global Search Bar & View Mode Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search products, cylinders, appliances...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = if (searchQuery.isNotBlank()) FlameOrange else TextSecondary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(
                            onClick = { viewModel.setSearchQuery("") },
                            modifier = Modifier.testTag("clear_search_btn")
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = TextSecondary
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("shop_search_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = FlameOrange,
                    unfocusedBorderColor = BorderLight
                ),
                singleLine = true
            )

            // View Mode Switcher
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(10.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(modifier = Modifier.padding(2.dp)) {
                    IconButton(
                        onClick = { viewMode = CatalogViewMode.LIST },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (viewMode == CatalogViewMode.LIST) Color(0xFFFFEDD5) else Color.Transparent)
                            .testTag("view_mode_list_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewList,
                            contentDescription = "List View",
                            tint = if (viewMode == CatalogViewMode.LIST) BrandOrange else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewMode = CatalogViewMode.GRID },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (viewMode == CatalogViewMode.GRID) Color(0xFFFFEDD5) else Color.Transparent)
                            .testTag("view_mode_grid_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = "Grid View",
                            tint = if (viewMode == CatalogViewMode.GRID) BrandOrange else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Quick Instant Search Keywords Bar
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 4.dp)) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Instant:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                }
            }
            items(quickSearches) { term ->
                val isSelected = searchQuery.equals(term, ignoreCase = true)
                Surface(
                    color = if (isSelected) FlameOrange else Color.White,
                    shape = RoundedCornerShape(16.dp),
                    border = if (isSelected) null else CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .clickable {
                            if (isSelected) viewModel.setSearchQuery("") else viewModel.setSearchQuery(term)
                        }
                        .testTag("quick_search_$term")
                ) {
                    Text(
                        text = term,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else Navy900,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Category Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setSelectedCategory(cat) },
                    label = { Text(cat, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = FlameOrange,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("shop_cat_${cat.lowercase().replace(" ", "_")}")
                )
            }
        }

        // Active Search & Filter Indicator Bar
        if (searchQuery.isNotBlank() || selectedCategory != "ALL") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Active Filters:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    if (searchQuery.isNotBlank()) {
                        Surface(
                            color = Color(0xFFFFEDD5),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("\"$searchQuery\"", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = FlameOrangeDark)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove query",
                                    tint = FlameOrangeDark,
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clickable { viewModel.setSearchQuery("") }
                                )
                            }
                        }
                    }
                    if (selectedCategory != "ALL") {
                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(selectedCategory, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove category",
                                    tint = BluePrimary,
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clickable { viewModel.setSelectedCategory("ALL") }
                                )
                            }
                        }
                    }
                }

                TextButton(
                    onClick = {
                        viewModel.setSearchQuery("")
                        viewModel.setSelectedCategory("ALL")
                    },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("Clear All", fontSize = 11.sp, color = FlameOrange)
                }
            }
        }

        // Subtotal & Product Count Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredProducts.size} Products Available",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )

            if (cartSubtotal > 0) {
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(Screen.CART) }
                        .testTag("shop_cart_quick_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = BluePrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Cart: R ${String.format("%.2f", cartSubtotal)} ($cartItemCount)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Catalog Content (List View or Grid View)
        if (filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Text(
                        text = if (searchQuery.isNotBlank()) "No products match \"$searchQuery\"" else "No products found in $selectedCategory",
                        color = Navy900,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Try searching for 9kg gas, geysers, braais or clear your active filters.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            viewModel.setSearchQuery("")
                            viewModel.setSelectedCategory("ALL")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("reset_search_btn")
                    ) {
                        Text("Reset Search & Filters", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else if (viewMode == CatalogViewMode.LIST) {
            // LIST VIEW
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .testTag("catalog_list_view"),
                contentPadding = PaddingValues(top = 4.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredProducts, key = { it.id }) { product ->
                    ProductListItem(
                        product = product,
                        viewModel = viewModel,
                        onProductClick = { viewModel.openProductDetail(it) }
                    )
                }
            }
        } else {
            // GRID VIEW
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 90.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("catalog_grid_view")
            ) {
                items(filteredProducts, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        viewModel = viewModel,
                        onProductClick = { viewModel.openProductDetail(it) }
                    )
                }
            }
        }
    }
}
