package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.engine.MatchEngine
import com.example.model.GameState
import kotlin.math.sin

@Composable
fun GameplayHud(
    engine: MatchEngine,
    onPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val aliveCount = engine.alivePlayers.size
    val totalCount = engine.players.size
    val currentEvent = engine.eventManager.currentEvent
    val isWarning = engine.eventManager.isWarningPhase
    val isEventActive = engine.eventManager.isEventActive
    val eventTimer = engine.eventManager.eventTimer

    var showEmoteWheel by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        // TOP HUD BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Alive Player Badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xDD231545),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("👥", fontSize = 16.sp)
                    Text(
                        text = "$aliveCount / $totalCount ALIVE",
                        color = if (aliveCount <= 2) Color(0xFFFF4081) else Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }
            }

            // Match Time
            val minutes = (engine.matchTimerSec / 60).toInt()
            val seconds = (engine.matchTimerSec % 60).toInt()
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xDD231545),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("⏱️", fontSize = 14.sp)
                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        color = Color(0xFFD0BCFF),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Pause Button
            IconButton(
                onClick = onPauseClick,
                modifier = Modifier
                    .size(42.dp)
                    .testTag("pause_button")
                    .clip(CircleShape)
                    .background(Color(0xFF2B1954))
            ) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "Pause",
                    tint = Color(0xFFEADDFF)
                )
            }
        }

        val eliminationList by engine.eliminationFeed.collectAsState()

        // TOP RIGHT: LIVE ELIMINATION FEED (KILL FEED)
        AnimatedVisibility(
            visible = eliminationList.isNotEmpty(),
            enter = fadeIn() + slideInHorizontally { it / 2 },
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 58.dp, end = 16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                eliminationList.take(3).forEach { item ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (item.isLocalVictim) Color(0xEEB71C1C) else Color(0xCC1A1033),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (item.isLocalVictim) Color(0xFFFF5252) else Color(0x448B5CF6)
                        ),
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(item.victimEmoji, fontSize = 14.sp)
                            Text(
                                text = item.victimName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            Text(
                                text = item.reason,
                                color = Color(0xFFFFD54F),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // EVENT ALERT BANNER OR MODE OBJECTIVE BANNER (TOP CENTER)
        val currentMode = engine.matchMode.collectAsState().value
        val localPlayer = engine.players.find { it.isLocalPlayer }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 62.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Mode Specific Objective Card
            when (currentMode) {
                com.example.model.MatchMode.CANDY_RUSH -> {
                    val myCandies = localPlayer?.candyScore ?: 0
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFE91E63),
                        shadowElevation = 8.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("🍬", fontSize = 22.sp)
                            Column {
                                Text(
                                    text = "$myCandies / 15 SWEETS COLLECTED!",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                                LinearProgressIndicator(
                                    progress = { (myCandies / 15f).coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .width(160.dp)
                                        .height(6.dp)
                                        .clip(CircleShape),
                                    color = Color(0xFFFFD54F),
                                    trackColor = Color(0x66000000)
                                )
                            }
                        }
                    }
                }

                com.example.model.MatchMode.COLOR_DANCE -> {
                    val colorName = when (engine.arena.activeDanceColorTag) {
                        1 -> "RED 🟥"
                        2 -> "BLUE 🟦"
                        3 -> "GREEN 🟩"
                        else -> "YELLOW 🟨"
                    }
                    val timer = engine.arena.danceWarningTimer
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (timer <= 0f) Color(0xFF9C27B0) else Color(0xFF00E676),
                        shadowElevation = 8.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🎨", fontSize = 20.sp)
                            Text(
                                text = if (timer > 0f) "DANCE ON: $colorName (${timer.toInt() + 1}s)" else "FLOOR DROPPING! STAY ON $colorName",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                com.example.model.MatchMode.CROWN_CHASE -> {
                    val crownHolder = engine.players.find { it.hasCrown }
                    val holdSec = crownHolder?.crownHoldSeconds ?: 0f
                    val isMe = crownHolder?.isLocalPlayer == true
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isMe) Color(0xFFFFB300) else Color(0xFF4A148C),
                        shadowElevation = 8.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("👑", fontSize = 20.sp)
                            Text(
                                text = if (isMe) "YOU ARE KING! HOLD CROWN: ${holdSec.toInt()}s / 20s"
                                else "CHASE ${crownHolder?.name ?: "KING"}! (${holdSec.toInt()}s/20s)",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                com.example.model.MatchMode.BOMB_PARTY -> {
                    val bombHolder = engine.players.find { it.hasBomb }
                    val isMe = bombHolder?.isLocalPlayer == true
                    val bombSec = bombHolder?.bombTimerSec ?: 0f
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isMe) Color(0xFFFF1744) else Color(0xFF37474F),
                        shadowElevation = 8.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("💣", fontSize = 20.sp)
                            Text(
                                text = if (isMe) "🚨 YOU HAVE TNT! BONK SOMEONE! (${String.format("%.1f", bombSec)}s)"
                                else "RUN FROM ${bombHolder?.name ?: "BOMB"}! (${String.format("%.1f", bombSec)}s)",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                else -> {}
            }

            // Kids Assist Banner if active
            if (engine.arena.guardRailsActive) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x9900E676)
                ) {
                    Text(
                        text = "🛡️ Kids Bouncy Assist Active",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }

            // Hazard Event Alert (if active during standard/classic survival)
            AnimatedVisibility(
                visible = currentEvent != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                currentEvent?.let { evt ->
                    val bannerColor = if (isWarning) Color(0xFFFF9800) else Color(evt.themeColorHex)
                    
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = bannerColor,
                        shadowElevation = 10.dp,
                        modifier = Modifier.widthIn(max = 380.dp).padding(horizontal = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(evt.iconEmoji, fontSize = 24.sp)
                                Text(
                                    text = if (isWarning) "⚠️ WARNING: ${evt.displayName}" else evt.displayName,
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "${eventTimer.toInt() + 1}s",
                                    color = Color(0xFFFFEB3B),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                            }
                            
                            Text(
                                text = evt.shortInstruction,
                                color = Color.White.copy(alpha = 0.95f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Event progress bar
                            val maxTime = if (isWarning) engine.eventManager.warningDuration else engine.eventManager.eventDuration
                            val progress = (eventTimer / maxTime).coerceIn(0f, 1f)
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(CircleShape),
                                color = Color.White,
                                trackColor = Color(0x44000000)
                            )
                        }
                    }
                }
            }
        }

        // SPECTATOR NOTIFICATION
        if (engine.gameState.collectAsState().value == GameState.SPECTATING) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xDD000000),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp)
            ) {
                Text(
                    text = "👻 YOU WERE ELIMINATED! Spectating Remaining Players...",
                    color = Color(0xFFFFD54F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        // QUICK EMOTE PICKER WHEEL OVERLAY
        AnimatedVisibility(
            visible = showEmoteWheel,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 120.dp, end = 24.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFA231545),
                shadowElevation = 12.dp,
                modifier = Modifier.border(1.dp, Color(0x668B5CF6), RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val emotes = listOf("😂", "😭", "🤬", "💪", "💃", "👑", "❓", "🔥")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        emotes.take(4).forEach { emoji ->
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2B1954))
                                    .clickable {
                                        engine.triggerLocalEmote(emoji)
                                        showEmoteWheel = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 22.sp)
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        emotes.drop(4).forEach { emoji ->
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2B1954))
                                    .clickable {
                                        engine.triggerLocalEmote(emoji)
                                        showEmoteWheel = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 22.sp)
                            }
                        }
                    }
                }
            }
        }

        // BOTTOM ON-SCREEN TOUCH CONTROLS & PLAYER STATUS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            val localPlayer = engine.players.find { it.isLocalPlayer }

            // Left Side: Joystick + Player Status Badge
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (localPlayer != null && localPlayer.isAlive) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xDD1E1238),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x448B5CF6)),
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(localPlayer.avatarEmoji, fontSize = 14.sp)
                            Text(
                                text = if (localPlayer.isStunned) "STUNNED 💫"
                                else if (!localPlayer.isGrounded) "AIRBORNE 🪂"
                                else if (localPlayer.isDashing) "DASHING ⚡"
                                else "READY 🛡️",
                                color = if (localPlayer.isStunned) Color(0xFFFF5252)
                                else if (localPlayer.isDashing) Color(0xFFFF4081)
                                else if (!localPlayer.isGrounded) Color(0xFF00E5FF)
                                else Color(0xFF00E676),
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Virtual Joystick (Left)
                VirtualJoystick(
                    onMove = { dir ->
                        engine.localMoveInput = dir
                    }
                )
            }

            // Action Buttons (Right)
            val dashCd = localPlayer?.dashCooldownTimer ?: 0f
            val dashProgress = (dashCd / 1.4f).coerceIn(0f, 1f)

            ActionButtons(
                dashCooldownProgress = dashProgress,
                onJump = {
                    engine.localJumpPressed = true
                },
                onDash = {
                    engine.localDashPressed = true
                },
                onOpenEmotes = {
                    showEmoteWheel = !showEmoteWheel
                }
            )
        }
    }
}
