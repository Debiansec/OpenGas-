package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun ProductVisual(
    imageType: String,
    modifier: Modifier = Modifier,
    label: String = ""
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF1F5F9)),
        contentAlignment = Alignment.Center
    ) {
        when (imageType) {
            "CYLINDER_BLUE" -> GasCylinderVector(baseColor = Color(0xFF0052CC), highlightColor = Color(0xFF4C9AFF), label = label.ifBlank { "GasOn" })
            "CYLINDER_SILVER" -> GasCylinderVector(baseColor = Color(0xFF64748B), highlightColor = Color(0xFFCBD5E1), label = label.ifBlank { "GasOn" })
            "CYLINDER_GREEN" -> GasCylinderVector(baseColor = Color(0xFF065F46), highlightColor = Color(0xFF34D399), label = label.ifBlank { "GasOn" })
            "GEYSER" -> GasGeyserVector()
            "HOB" -> GasHobVector()
            "HEATER" -> GasHeaterVector()
            "GRILLER" -> GasGrillerVector()
            "REGULATOR" -> GasRegulatorVector()
            else -> GasCylinderVector(baseColor = Color(0xFF0052CC), highlightColor = Color(0xFF4C9AFF), label = label.ifBlank { "GasOn" })
        }
    }
}

@Composable
fun GasCylinderVector(
    baseColor: Color,
    highlightColor: Color,
    label: String = "GasOn"
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val centerX = w / 2f

            // Shroud / Collar Top Handle
            val collarWidth = w * 0.46f
            val collarHeight = h * 0.16f
            val collarTop = h * 0.15f
            drawRoundRect(
                color = baseColor.copy(alpha = 0.9f),
                topLeft = Offset(centerX - collarWidth / 2, collarTop),
                size = Size(collarWidth, collarHeight),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
            // Handle cut-out slot
            drawRoundRect(
                color = Color(0xFFF1F5F9),
                topLeft = Offset(centerX - collarWidth * 0.28f, collarTop + collarHeight * 0.25f),
                size = Size(collarWidth * 0.56f, collarHeight * 0.45f),
                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )

            // Brass Valve in Center
            drawRect(
                color = Color(0xFFF59E0B),
                topLeft = Offset(centerX - 6.dp.toPx(), collarTop + collarHeight * 0.35f),
                size = Size(12.dp.toPx(), 14.dp.toPx())
            )

            // Main Cylinder Body with gradient
            val bodyWidth = w * 0.62f
            val bodyHeight = h * 0.56f
            val bodyTop = collarTop + collarHeight * 0.7f
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        baseColor,
                        highlightColor,
                        baseColor
                    ),
                    startX = centerX - bodyWidth / 2,
                    endX = centerX + bodyWidth / 2
                ),
                topLeft = Offset(centerX - bodyWidth / 2, bodyTop),
                size = Size(bodyWidth, bodyHeight),
                cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx())
            )

            // Welded Seam Lines
            drawLine(
                color = Color.White.copy(alpha = 0.3f),
                start = Offset(centerX - bodyWidth / 2 + 4.dp.toPx(), bodyTop + bodyHeight * 0.22f),
                end = Offset(centerX + bodyWidth / 2 - 4.dp.toPx(), bodyTop + bodyHeight * 0.22f),
                strokeWidth = 2.dp.toPx()
            )
            drawLine(
                color = Color.White.copy(alpha = 0.3f),
                start = Offset(centerX - bodyWidth / 2 + 4.dp.toPx(), bodyTop + bodyHeight * 0.78f),
                end = Offset(centerX + bodyWidth / 2 - 4.dp.toPx(), bodyTop + bodyHeight * 0.78f),
                strokeWidth = 2.dp.toPx()
            )

            // Flame Emblem on Cylinder
            val flamePath = Path().apply {
                val fx = centerX
                val fy = bodyTop + bodyHeight * 0.45f
                moveTo(fx, fy - 16.dp.toPx())
                cubicTo(fx + 10.dp.toPx(), fy - 4.dp.toPx(), fx + 12.dp.toPx(), fy + 12.dp.toPx(), fx, fy + 14.dp.toPx())
                cubicTo(fx - 12.dp.toPx(), fy + 12.dp.toPx(), fx - 10.dp.toPx(), fy - 4.dp.toPx(), fx, fy - 16.dp.toPx())
                close()
            }
            drawPath(flamePath, color = FlameOrange)

            // Foot Ring Base
            val footWidth = bodyWidth * 0.82f
            val footHeight = h * 0.08f
            val footTop = bodyTop + bodyHeight - 2.dp.toPx()
            drawRoundRect(
                color = baseColor.copy(alpha = 0.85f),
                topLeft = Offset(centerX - footWidth / 2, footTop),
                size = Size(footWidth, footHeight),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
        }

        // GasOn Label centered on cylinder
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 18.dp)
        ) {
            Text(
                text = "GasOn",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "LP GAS",
                color = FlameYellow,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GasGeyserVector() {
    Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        val w = size.width
        val h = size.height
        val cx = w / 2f

        // Geyser Stainless Housing
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1), Color(0xFF94A3B8))
            ),
            topLeft = Offset(cx - w * 0.35f, h * 0.1f),
            size = Size(w * 0.7f, h * 0.75f),
            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
        )
        // Digital display
        drawRoundRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(cx - 24.dp.toPx(), h * 0.25f),
            size = Size(48.dp.toPx(), 26.dp.toPx()),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )
        // Control knobs
        drawCircle(color = Color(0xFF475569), radius = 10.dp.toPx(), center = Offset(cx - 16.dp.toPx(), h * 0.65f))
        drawCircle(color = Color(0xFF475569), radius = 10.dp.toPx(), center = Offset(cx + 16.dp.toPx(), h * 0.65f))
        // Inlet pipes bottom
        drawRect(color = Color(0xFFB45309), topLeft = Offset(cx - 22.dp.toPx(), h * 0.85f), size = Size(10.dp.toPx(), 18.dp.toPx()))
        drawRect(color = FlameOrange, topLeft = Offset(cx - 5.dp.toPx(), h * 0.85f), size = Size(10.dp.toPx(), 18.dp.toPx()))
        drawRect(color = Color(0xFF0284C7), topLeft = Offset(cx + 12.dp.toPx(), h * 0.85f), size = Size(10.dp.toPx(), 18.dp.toPx()))
    }
}

@Composable
fun GasHobVector() {
    Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        // Stainless Steel Base
        drawRoundRect(
            brush = Brush.linearGradient(listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8))),
            topLeft = Offset(cx - w * 0.42f, cy - h * 0.28f),
            size = Size(w * 0.84f, h * 0.56f),
            cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
        )
        // Left Burner
        drawCircle(color = Color(0xFF1E293B), radius = 22.dp.toPx(), center = Offset(cx - w * 0.2f, cy))
        drawCircle(color = Color(0xFFF59E0B), radius = 12.dp.toPx(), center = Offset(cx - w * 0.2f, cy))
        drawCircle(color = Color(0xFF0284C7), radius = 6.dp.toPx(), center = Offset(cx - w * 0.2f, cy))

        // Right Burner
        drawCircle(color = Color(0xFF1E293B), radius = 22.dp.toPx(), center = Offset(cx + w * 0.2f, cy))
        drawCircle(color = Color(0xFFF59E0B), radius = 12.dp.toPx(), center = Offset(cx + w * 0.2f, cy))
        drawCircle(color = Color(0xFF0284C7), radius = 6.dp.toPx(), center = Offset(cx + w * 0.2f, cy))
    }
}

@Composable
fun GasHeaterVector() {
    Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        val w = size.width
        val h = size.height
        val cx = w / 2f

        // Dark Cabinet
        drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = Offset(cx - w * 0.35f, h * 0.12f),
            size = Size(w * 0.7f, h * 0.76f),
            cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
        )
        // Orange ceramic heating panels
        drawRoundRect(
            color = FlameOrange,
            topLeft = Offset(cx - w * 0.25f, h * 0.26f),
            size = Size(w * 0.5f, h * 0.38f),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )
        // Wire Guard
        for (i in 1..4) {
            val y = h * 0.26f + (h * 0.38f * (i / 5f))
            drawLine(
                color = Color.White.copy(alpha = 0.5f),
                start = Offset(cx - w * 0.25f, y),
                end = Offset(cx + w * 0.25f, y),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}

@Composable
fun GasGrillerVector() {
    Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        // Griller hood / body
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFF334155), Color(0xFF0F172A))),
            topLeft = Offset(cx - w * 0.4f, cy - h * 0.3f),
            size = Size(w * 0.8f, h * 0.6f),
            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
        )
        // Red flame glow inside grill
        drawRect(
            color = FlameRed.copy(alpha = 0.8f),
            topLeft = Offset(cx - w * 0.32f, cy - h * 0.05f),
            size = Size(w * 0.64f, h * 0.15f)
        )
        // Chrome Control knobs
        for (i in 0..2) {
            val kx = cx - w * 0.22f + (i * w * 0.22f)
            drawCircle(color = Color(0xFFCBD5E1), radius = 8.dp.toPx(), center = Offset(kx, cy + h * 0.18f))
        }
    }
}

@Composable
fun GasRegulatorVector() {
    Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        // Round regulator dome
        drawCircle(color = Color(0xFF64748B), radius = w * 0.28f, center = Offset(cx, cy))
        drawCircle(color = Color(0xFF94A3B8), radius = w * 0.22f, center = Offset(cx, cy))

        // Brass connection nozzle left
        drawRect(color = Color(0xFFD97706), topLeft = Offset(cx - w * 0.45f, cy - 8.dp.toPx()), size = Size(w * 0.2f, 16.dp.toPx()))
        // Orange hose nozzle right
        drawRect(color = FlameOrange, topLeft = Offset(cx + w * 0.25f, cy - 6.dp.toPx()), size = Size(w * 0.2f, 12.dp.toPx()))

        // Pressure Gauge
        drawCircle(color = Color.White, radius = 14.dp.toPx(), center = Offset(cx, cy - w * 0.15f))
        drawCircle(color = Color(0xFF1E293B), radius = 14.dp.toPx(), center = Offset(cx, cy - w * 0.15f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx()))
        // Gauge needle
        drawLine(
            color = FlameRed,
            start = Offset(cx, cy - w * 0.15f),
            end = Offset(cx + 6.dp.toPx(), cy - w * 0.15f - 8.dp.toPx()),
            strokeWidth = 2.dp.toPx()
        )
    }
}
