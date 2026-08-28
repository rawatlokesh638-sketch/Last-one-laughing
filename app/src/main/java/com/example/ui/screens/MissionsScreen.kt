package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
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
import com.example.data.ProgressionManager

@Composable
fun MissionsScreen(
    progression: ProgressionManager,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Missions, 1 = Achievements, 2 = Daily Gift
    var userCoins by remember { mutableStateOf(progression.coins) }
    var dailyRewardClaimedNotice by remember { mutableStateOf<String?>(null) }

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
                        .testTag("missions_back_button")
                        .clip(CircleShape)
                        .background(Color(0xFF2B1954))
                        .border(1.dp, Color(0x668B5CF6), CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFFEADDFF))
                }

                Text(
                    text = "QUESTS & REWARDS",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFFFB300)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("🪙", fontSize = 16.sp)
                        Text(
                            text = "$userCoins",
                            color = Color(0xFF3E2723),
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TAB SELECTOR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Daily Quests", "Achievements", "Daily Gift").forEachIndexed { index, title ->
                    val isSelected = selectedTab == index
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) Color(0xFF7C4DFF) else Color(0xFF231545),
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFFD0BCFF) else Color(0x338B5CF6),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedTab = index }
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) Color.White else Color(0xFFD0BCFF),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> {
                    // DAILY MISSIONS
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(progression.dailyMissions) { mission ->
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = Color(0xFF231545),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = 1.dp,
                                        color = if (mission.isCompleted && !mission.isClaimed) Color(0xFFFF4081) else Color(0x448B5CF6),
                                        shape = RoundedCornerShape(18.dp)
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = mission.title,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = mission.description,
                                            color = Color(0xFFD0BCFF),
                                            fontSize = 12.sp
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Progress bar
                                        val progress = (mission.currentCount.toFloat() / mission.targetCount).coerceIn(0f, 1f)
                                        LinearProgressIndicator(
                                            progress = { progress },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(CircleShape),
                                            color = Color(0xFF10B981),
                                            trackColor = Color(0xFF3B2474)
                                        )

                                        Text(
                                            text = "${mission.currentCount} / ${mission.targetCount}",
                                            color = Color(0xFF10B981),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    if (mission.isClaimed) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color(0xFF3B2474)
                                        ) {
                                            Text(
                                                text = "CLAIMED",
                                                color = Color(0xFFD0BCFF),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    } else if (mission.isCompleted) {
                                        Button(
                                            onClick = {
                                                mission.isClaimed = true
                                                progression.addCoins(mission.rewardCoins)
                                                progression.addXp(mission.rewardXP)
                                                userCoins = progression.coins
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081)),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("CLAIM 🪙${mission.rewardCoins}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                        }
                                    } else {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color(0xFF3B2474)
                                        ) {
                                            Text(
                                                text = "🪙 ${mission.rewardCoins}",
                                                color = Color(0xFFFFD54F),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // ACHIEVEMENTS
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(progression.achievements) { ach ->
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = Color(0xFF231545),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0x448B5CF6), RoundedCornerShape(18.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(ach.icon, fontSize = 28.sp)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = ach.title,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = ach.description,
                                            color = Color(0xFFD0BCFF),
                                            fontSize = 11.sp
                                        )
                                        val prog = (ach.currentCount.toFloat() / ach.targetCount).coerceIn(0f, 1f)
                                        LinearProgressIndicator(
                                            progress = { prog },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .padding(top = 4.dp)
                                                .clip(CircleShape),
                                            color = Color(0xFF7C4DFF),
                                            trackColor = Color(0xFF3B2474)
                                        )
                                    }
                                    Text("🪙 ${ach.rewardCoins}", color = Color(0xFFFFD54F), fontWeight = FontWeight.Black, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // DAILY LOGIN REWARD
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🎁", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "DAILY CHAOS GIFT",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Claim +300 Coins and +150 XP every 24 hours!",
                            color = Color(0xFFD0BCFF),
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(54.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        listOf(Color(0xFF7C4DFF), Color(0xFFEC4899))
                                    )
                                )
                                .clickable {
                                    val claimed = progression.claimDailyReward()
                                    if (claimed) {
                                        userCoins = progression.coins
                                        dailyRewardClaimedNotice = "🎉 Claimed +300 Coins & +150 XP!"
                                    } else {
                                        dailyRewardClaimedNotice = "⏰ Already claimed today! Come back tomorrow."
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("CLAIM DAILY REWARD", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
                        }

                        dailyRewardClaimedNotice?.let { msg ->
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(msg, color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
