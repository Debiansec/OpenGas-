package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation states
    var startAnimation by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1.0f else 0.4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logo_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "logo_alpha"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )

    // Trigger animation on launch and auto dismiss after ~2 seconds
    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2200)
        onDismiss()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable { onDismiss() }
            .testTag("white_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Animated App Icon & Logo container
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(160.dp)
                    .scale(scale)
            ) {
                // Outer Pulse Ring
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .scale(glowScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    GasOnOrange.copy(alpha = 0.25f),
                                    GasOnBlue.copy(alpha = 0.12f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // White Card containing the GasOn Logo & App Icon
                Surface(
                    shape = RoundedCornerShape(32.dp),
                    color = Color.White,
                    shadowElevation = 10.dp,
                    tonalElevation = 2.dp,
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.size(120.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_gason_logo),
                            contentDescription = "GasOn Logo and App Icon",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(20.dp))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Animated Typography Brand Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .scale(scale)
            ) {
                Text(
                    text = "Gas",
                    color = GasOnBlueDeep,
                    fontWeight = FontWeight.Black,
                    fontSize = 38.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "On",
                    color = GasOnOrange,
                    fontWeight = FontWeight.Black,
                    fontSize = 38.sp,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Tagline
            Text(
                text = "LP GAS STORE & FAST DELIVERY",
                color = GasOnBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Service Region Pill
            Surface(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Text(
                    text = "📍 Meyerton • Henley on Klip • Midvaal",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Sleek Loading Progress Indicator
            LinearProgressIndicator(
                color = GasOnOrange,
                trackColor = Color(0xFFF1F5F9),
                modifier = Modifier
                    .width(140.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Tap anywhere to continue",
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}
