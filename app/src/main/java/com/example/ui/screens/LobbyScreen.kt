package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.engine.MatchEngine
import com.example.model.GameState
import com.example.model.MatchMode

@Composable
fun LobbyScreen(
    engine: MatchEngine,
    onStartMatch: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val players = engine.players
    val roomCode = engine.roomCode.collectAsState().value
    val mode = engine.matchMode.collectAsState().value

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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TOP BAR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("lobby_back_button")
                        .clip(CircleShape)
                        .background(Color(0xFF2B1954))
                        .border(1.dp, Color(0x668B5CF6), CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFFEADDFF))
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${mode.iconEmoji} ${mode.title.uppercase()} LOBBY",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "ROOM CODE: $roomCode",
                        color = Color(0xFFFFD54F),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF7C4DFF)
                ) {
                    Text(
                        text = "${players.size}/10",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ARENA PREVIEW CARD
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF231545),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x668B5CF6), RoundedCornerShape(18.dp))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("🏟️", fontSize = 32.sp)
                    Column {
                        Text(
                            text = "MAP: CHAOS STADIUM",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "15 Random Hazards • Bumper Pads • 2.5D Physics",
                            color = Color(0xFFD0BCFF),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // PLAYER ROSTER GRID
            Text(
                text = "PLAYERS JOINED",
                color = Color(0xFFEADDFF),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(players) { p ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (p.isLocalPlayer) Color(0xFF3B2474) else Color(0xFF231545),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (p.isLocalPlayer) 2.dp else 1.dp,
                                color = if (p.isLocalPlayer) Color(0xFFFF4081) else Color(0x448B5CF6),
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(p.baseColorHex)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(p.avatarEmoji, fontSize = 18.sp)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = p.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1
                                )
                                Text(
                                    text = if (p.isLocalPlayer) "YOU (HOST)" else if (p.isBot) "BOT [${p.botDifficulty.name}]" else "READY",
                                    color = if (p.isLocalPlayer) Color(0xFFFFD54F) else Color(0xFF10B981),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // START BUTTON (Vibrant CTA)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFF7C4DFF), Color(0xFFEC4899))
                        )
                    )
                    .clickable { onStartMatch() }
                    .testTag("start_match_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Start", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("START MATCH NOW!", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}
