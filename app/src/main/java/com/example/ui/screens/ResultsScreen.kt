package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.engine.MatchEngine
import com.example.game.engine.MatchResult
import com.example.model.GameState
import com.example.model.MatchMode

@Composable
fun ResultsScreen(
    engine: MatchEngine,
    onPlayAgain: () -> Unit,
    onMainMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val result = engine.lastMatchResult ?: return
    val context = LocalContext.current
    val isWinner = result.localPlayerRank == 1
    val winner = result.winner

    val gradientColors = if (isWinner) {
        listOf(Color(0xFF2A0845), Color(0xFF4A148C), Color(0xFF1E0A3C))
    } else {
        listOf(Color(0xFF13092A), Color(0xFF261245), Color(0xFF0F071D))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(gradientColors))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // TITLE HEADER & TROPHY
            if (isWinner) {
                Text("👑 🏆 👑", fontSize = 36.sp)
                Text(
                    text = "VICTORY!",
                    color = Color(0xFFFFD54F),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "I WAS THE LAST ONE LAUGHING! 😂",
                    color = Color(0xFFF3E8FF),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "#${result.localPlayerRank} ELIMINATED",
                    color = Color(0xFFFF4081),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Winner: ${winner?.name ?: "Opponent"} ${winner?.avatarEmoji ?: "👑"}",
                    color = Color(0xFFD0BCFF),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // "YOUR CHAOS MOMENT" VIRAL CARD (Share Ready!)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF231545),
                shadowElevation = 16.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFFFF4081), Color(0xFF7C4DFF), Color(0xFFFFD54F))
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .testTag("chaos_moment_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(result.bestMoment.type.badge, fontSize = 24.sp)
                        Text(
                            text = "YOUR CHAOS MOMENT",
                            color = Color(0xFFFFD54F),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }

                    Text(
                        text = result.bestMoment.title,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = result.bestMoment.description,
                        color = Color(0xFFF3E8FF),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    // Viral Stat Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFD8E4)
                    ) {
                        Text(
                            text = "HIGHLIGHT STAT: ${result.bestMoment.statValue}",
                            color = Color(0xFF31111D),
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    // Share Button
                    OutlinedButton(
                        onClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "🎮 I survived ${result.totalPlayers - result.localPlayerRank} players in LAST ONE LAUGHING! My Chaos Moment: ${result.bestMoment.title} - ${result.bestMoment.description} 😂🔥"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Chaos Moment"))
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF4081)),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(Color(0xFFFF4081), Color(0xFF7C4DFF)))),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("share_highlight_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SHARE CHAOS HIGHLIGHT", fontWeight = FontWeight.Black)
                    }
                }
            }

            // REWARDS & PROGRESSION SUMMARY
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF231545),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x448B5CF6), RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // XP Earned
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⭐", fontSize = 24.sp)
                        Text("+${result.xpEarned} XP", color = Color(0xFFD0BCFF), fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Text("Level ${engine.progression.level}", color = Color(0xFFD0BCFF).copy(alpha = 0.7f), fontSize = 12.sp)
                    }

                    Divider(
                        modifier = Modifier
                            .height(40.dp)
                            .width(1.dp),
                        color = Color(0x448B5CF6)
                    )

                    // Coins Earned
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🪙", fontSize = 24.sp)
                        Text("+${result.coinsEarned} COINS", color = Color(0xFFFFD54F), fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Text("${engine.progression.coins} Total", color = Color(0xFFD0BCFF).copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                }
            }

            if (result.leveledUp) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF7C4DFF),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("🎉 LEVEL UP! You reached Level ${engine.progression.level}! (+200 bonus coins)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ACTION BUTTONS: REPLAY CTA (Vibrant Gradient)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFF7C4DFF), Color(0xFFEC4899))
                        )
                    )
                    .clickable { onPlayAgain() }
                    .testTag("play_again_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play Again", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PLAY AGAIN (REPLAY)", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
                }
            }

            OutlinedButton(
                onClick = onMainMenu,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEADDFF)),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(Color(0x668B5CF6), Color(0x66D0BCFF)))),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("main_menu_button")
            ) {
                Icon(Icons.Default.Home, contentDescription = "Main Menu")
                Spacer(modifier = Modifier.width(8.dp))
                Text("MAIN MENU", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
