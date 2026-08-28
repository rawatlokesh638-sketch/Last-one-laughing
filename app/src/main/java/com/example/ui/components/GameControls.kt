package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Vector2D
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun VirtualJoystick(
    modifier: Modifier = Modifier,
    onMove: (Vector2D) -> Unit
) {
    var thumbOffset by remember { mutableStateOf(Offset.Zero) }
    val maxRadiusPx = 110f

    Box(
        modifier = modifier
            .size(140.dp)
            .testTag("virtual_joystick")
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x882B1954), Color(0xCC13092A))
                )
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val delta = offset - center
                        val dist = delta.getDistance()
                        val clampedDist = dist.coerceAtMost(maxRadiusPx)
                        val angle = kotlin.math.atan2(delta.y, delta.x)
                        thumbOffset = Offset(cos(angle) * clampedDist, sin(angle) * clampedDist)
                        onMove(Vector2D(thumbOffset.x / maxRadiusPx, thumbOffset.y / maxRadiusPx))
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val newOffset = thumbOffset + dragAmount
                        val dist = newOffset.getDistance()
                        val clampedDist = dist.coerceAtMost(maxRadiusPx)
                        val angle = kotlin.math.atan2(newOffset.y, newOffset.x)
                        thumbOffset = Offset(cos(angle) * clampedDist, sin(angle) * clampedDist)
                        onMove(Vector2D(thumbOffset.x / maxRadiusPx, thumbOffset.y / maxRadiusPx))
                    },
                    onDragEnd = {
                        thumbOffset = Offset.Zero
                        onMove(Vector2D(0f, 0f))
                    },
                    onDragCancel = {
                        thumbOffset = Offset.Zero
                        onMove(Vector2D(0f, 0f))
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Inner Guide Ring
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0x338B5CF6))
        )

        // Thumb Knob
        Box(
            modifier = Modifier
                .offset { IntOffset(thumbOffset.x.toInt(), thumbOffset.y.toInt()) }
                .size(56.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFEC4899), Color(0xFF7C4DFF))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Gamepad,
                contentDescription = "Joystick Thumb",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ActionButtons(
    dashCooldownProgress: Float, // 0 to 1
    onJump: () -> Unit,
    onDash: () -> Unit,
    onOpenEmotes: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDashReady = dashCooldownProgress <= 0f

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        // Emote Wheel Button
        IconButton(
            onClick = onOpenEmotes,
            modifier = Modifier
                .size(48.dp)
                .testTag("emote_button")
                .clip(CircleShape)
                .background(Color(0xFF2B1954))
                .border(1.5.dp, Color(0x888B5CF6), CircleShape)
                .shadow(6.dp, CircleShape)
        ) {
            Text("😂", fontSize = 22.sp)
        }

        // Dash / Push Button with Radial Cooldown Ring
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(72.dp)
                .testTag("dash_button")
        ) {
            if (dashCooldownProgress > 0f) {
                CircularProgressIndicator(
                    progress = { dashCooldownProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFFF4081),
                    trackColor = Color(0x33FFFFFF),
                    strokeWidth = 4.dp
                )
            }

            Surface(
                shape = CircleShape,
                color = if (isDashReady) Color(0xFFFF4081) else Color(0xFF3B2474),
                shadowElevation = if (isDashReady) 8.dp else 2.dp,
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .clickable(enabled = isDashReady) { onDash() }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(if (isDashReady) "💨" else "⏳", fontSize = 20.sp)
                        Text(
                            text = if (isDashReady) "DASH" else "READY",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // Jump Button
        Surface(
            shape = CircleShape,
            color = Color.Transparent,
            shadowElevation = 10.dp,
            modifier = Modifier
                .size(78.dp)
                .testTag("jump_button")
                .clip(CircleShape)
                .clickable { onJump() }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF7C4DFF), Color(0xFF4A148C))
                        )
                    )
                    .border(2.dp, Color(0x88D0BCFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Jump",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "JUMP",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
