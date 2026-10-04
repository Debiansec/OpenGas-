package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.Screen

data class NavItem(
    val screen: Screen,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun BottomNavBar(
    currentScreen: Screen,
    onSelectScreen: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem(Screen.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home),
        NavItem(Screen.SHOP, "Shop", Icons.Filled.Storefront, Icons.Outlined.Storefront),
        NavItem(Screen.GAS_DELIVERY, "Gas", Icons.Filled.LocalFireDepartment, Icons.Outlined.LocalFireDepartment),
        NavItem(Screen.ORDER_TRACKING, "Orders", Icons.Filled.LocalShipping, Icons.Outlined.LocalShipping),
        NavItem(Screen.ACCOUNT, "Account", Icons.Filled.Person, Icons.Outlined.Person)
    )

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentScreen == item.screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelectScreen(item.screen) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title,
                        tint = if (isSelected) FlameOrange else TextSecondary
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) FlameOrange else TextSecondary
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFFFFEDD5)
                ),
                modifier = Modifier.testTag("nav_item_${item.title.lowercase()}")
            )
        }
    }
}

@Composable
fun FloatingAiButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        containerColor = FlameOrange,
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
        modifier = modifier.testTag("floating_ai_assistant_button")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = "🔥 Ask GasOn AI",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}
