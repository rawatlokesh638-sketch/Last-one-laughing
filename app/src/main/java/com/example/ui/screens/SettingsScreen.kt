package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioManager
import com.example.data.ProgressionManager

@Composable
fun SettingsScreen(
    progression: ProgressionManager,
    audio: AudioManager,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var playerName by remember { mutableStateOf(progression.playerName) }
    var graphicsQuality by remember { mutableStateOf(progression.graphicsQuality) }
    var screenShake by remember { mutableStateOf(progression.screenShakeEnabled) }
    var sfxOn by remember { mutableStateOf(progression.sfxEnabled) }
    var bgmOn by remember { mutableStateOf(progression.bgmEnabled) }
    var kidsAssistOn by remember { mutableStateOf(progression.kidsAssistEnabled) }

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
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("settings_back_button")
                        .clip(CircleShape)
                        .background(Color(0xFF2B1954))
                        .border(1.dp, Color(0x668B5CF6), CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFFEADDFF))
                }

                Text(
                    text = "SETTINGS & GRAPHICS",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.size(44.dp))
            }

            // Player Nickname Editor
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF231545),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x668B5CF6), RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("PLAYER NICKNAME", color = Color(0xFFD0BCFF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    OutlinedTextField(
                        value = playerName,
                        onValueChange = {
                            playerName = it
                            progression.playerName = it
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF7C4DFF),
                            unfocusedBorderColor = Color(0x668B5CF6)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Graphics Quality
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF231545),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x668B5CF6), RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("GRAPHICS QUALITY", color = Color(0xFFD0BCFF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("LOW", "MEDIUM", "HIGH").forEach { q ->
                            val isSelected = graphicsQuality == q
                            Button(
                                onClick = {
                                    graphicsQuality = q
                                    progression.graphicsQuality = q
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) Color(0xFF7C4DFF) else Color(0xFF2B1954)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(q, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Accessibility & Kids Mode
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF231545),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (kidsAssistOn) Color(0xFF00E676) else Color(0x668B5CF6), RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("🛡️ ACCESSIBILITY & KIDS PLAY", color = Color(0xFFD0BCFF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Kids Safe Guard Rails", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Rainbow bumper fences prevent falling off arena", color = Color(0xFFD0BCFF), fontSize = 11.sp)
                        }
                        Switch(
                            checked = kidsAssistOn,
                            onCheckedChange = {
                                kidsAssistOn = it
                                progression.kidsAssistEnabled = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF00E676)
                            )
                        )
                    }
                }
            }

            // Audio & FX Settings
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF231545),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x668B5CF6), RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("AUDIO & FEEDBACK", color = Color(0xFFD0BCFF), fontWeight = FontWeight.Bold, fontSize = 12.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sound Effects (SFX)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Switch(
                            checked = sfxOn,
                            onCheckedChange = {
                                sfxOn = it
                                progression.sfxEnabled = it
                                audio.isSfxEnabled = it
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Party Music (BGM)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Switch(
                            checked = bgmOn,
                            onCheckedChange = {
                                bgmOn = it
                                progression.bgmEnabled = it
                                audio.isBgmEnabled = it
                                if (it) audio.startBgm() else audio.stopBgm()
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Screen Shake", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Switch(
                            checked = screenShake,
                            onCheckedChange = {
                                screenShake = it
                                progression.screenShakeEnabled = it
                            }
                        )
                    }
                }
            }

            // About
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF231545),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x448B5CF6), RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("LAST ONE LAUGHING 🎮", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    Text("Version 1.0.0 • Mobile-First Multiplayer Party Survival", color = Color(0xFFD0BCFF), fontSize = 12.sp)
                }
            }
        }
    }
}
