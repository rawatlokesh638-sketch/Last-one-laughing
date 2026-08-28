package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CosmeticsCatalog
import com.example.data.ProgressionManager
import com.example.model.MatchMode

@Composable
fun MainMenuScreen(
    progression: ProgressionManager,
    onStartQuickPlay: () -> Unit,
    onOpenModes: () -> Unit,
    onOpenFriends: () -> Unit,
    onStartPractice: () -> Unit,
    onOpenShop: () -> Unit,
    onOpenMissions: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val equippedChar = CosmeticsCatalog.characters.find { it.id == progression.equippedCharacterId }
    val equippedOutfit = CosmeticsCatalog.outfits.find { it.id == progression.equippedOutfitId }

    // Breathing pulse animation for Play button
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "play_pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF13092A), Color(0xFF261245), Color(0xFF0F071D))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // TOP STATUS BAR: Profile, Level, Win Streak, Coins, Settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile & Level Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF2B1954),
                    modifier = Modifier
                        .border(1.dp, Color(0x668B5CF6), RoundedCornerShape(20.dp))
                        .clickable { onOpenMissions() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(equippedChar?.primaryColorHex ?: 0xFFFF5722)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(equippedChar?.iconEmoji ?: "😃", fontSize = 16.sp)
                        }
                        Column {
                            Text(
                                text = progression.playerName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "LVL ${progression.level}",
                                color = Color(0xFFD0BCFF),
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Win Streak Flame
                if (progression.winStreak > 0) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFF4081)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🔥", fontSize = 14.sp)
                            Text(
                                text = "${progression.winStreak} STREAK",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Coins & Settings
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFFFB300),
                        modifier = Modifier.clickable { onOpenShop() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🪙", fontSize = 14.sp)
                            Text(
                                text = "${progression.coins}",
                                color = Color(0xFF3E2723),
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("main_settings_button")
                            .clip(CircleShape)
                            .background(Color(0xFF3B2474))
                            .border(1.dp, Color(0x668B5CF6), CircleShape)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFFEADDFF))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // GAME LOGO & TITLE
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎮 😂 👑", fontSize = 34.sp)
                Text(
                    text = "LAST ONE",
                    color = Color(0xFFFFD54F),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "LAUGHING",
                    color = Color(0xFFFF4081),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "PARTY SURVIVAL CHAOS",
                    color = Color(0xFFD0BCFF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }

            // HERO CHARACTER PREVIEW CARD
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF231545),
                shadowElevation = 10.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, Color(0x668B5CF6), RoundedCornerShape(24.dp))
                    .clickable { onOpenShop() }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .shadow(12.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color(equippedChar?.primaryColorHex ?: 0xFFFF5722)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(equippedChar?.iconEmoji ?: "😃", fontSize = 48.sp)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = equippedChar?.name ?: "Laughing King",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Outfit: ${equippedOutfit?.name ?: "Standard Tee"}",
                            color = Color(0xFFFFD54F),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF3B2474)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Checkroom, contentDescription = "Wardrobe", tint = Color(0xFFEADDFF), modifier = Modifier.size(14.dp))
                            Text("CUSTOMIZE LOOK", color = Color(0xFFF3E8FF), fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            // PRIMARY ACTIONS: QUICK PLAY BUTTON (Vibrant Gradient CTA)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .scale(scalePulse)
                    .shadow(16.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFF7C4DFF), Color(0xFFEC4899))
                        )
                    )
                    .clickable { onStartQuickPlay() }
                    .testTag("quick_play_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PLAY QUICK CHAOS", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                }
            }

            // GAME MODES HUB BUTTON (Candy Rush, Color Dance, Crown Chase, etc.)
            Button(
                onClick = onOpenModes,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(8.dp, RoundedCornerShape(18.dp))
                    .testTag("open_modes_hub_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("🎮", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SELECT GAME MODE (7 MODES)", color = Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Black)
                }
            }

            // SECONDARY GAME MODES: FRIENDS & PRACTICE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onOpenFriends,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("friends_mode_button")
                ) {
                    Icon(Icons.Default.Group, contentDescription = "Friends", tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("FRIENDS", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onStartPractice,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B2474)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .border(1.dp, Color(0x668B5CF6), RoundedCornerShape(16.dp))
                        .testTag("practice_mode_button")
                ) {
                    Icon(Icons.Default.SmartToy, contentDescription = "Practice", tint = Color(0xFFEADDFF), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("PRACTICE", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            // BOTTOM SHORTCUTS: WARDROBE & QUESTS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF231545),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color(0x448B5CF6), RoundedCornerShape(16.dp))
                        .clickable { onOpenShop() }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🛍️", fontSize = 20.sp)
                        Column {
                            Text("SHOP", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                            Text("Skins & FX", color = Color(0xFFD0BCFF), fontSize = 10.sp)
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF231545),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color(0x448B5CF6), RoundedCornerShape(16.dp))
                        .clickable { onOpenMissions() }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🎁", fontSize = 20.sp)
                        Column {
                            Text("QUESTS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                            Text("Daily Gifts", color = Color(0xFFD0BCFF), fontSize = 10.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
