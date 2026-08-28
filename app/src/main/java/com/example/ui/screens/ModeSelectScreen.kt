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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
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
import com.example.data.ProgressionManager
import com.example.model.MatchMode

@Composable
fun ModeSelectScreen(
    progression: ProgressionManager,
    onSelectMode: (MatchMode) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = All, 1 = Kids Easy, 2 = Action & Survival
    var kidsAssistState by remember { mutableStateOf(progression.kidsAssistEnabled) }

    val allModes = listOf(
        MatchMode.CANDY_RUSH,
        MatchMode.COLOR_DANCE,
        MatchMode.CROWN_CHASE,
        MatchMode.QUICK_CHAOS,
        MatchMode.BOMB_PARTY,
        MatchMode.LAVA_RUN,
        MatchMode.PRACTICE
    )

    val displayedModes = when (selectedTab) {
        1 -> allModes.filter { it.isKidsFriendly }
        2 -> allModes.filter { !it.isKidsFriendly || it == MatchMode.BOMB_PARTY || it == MatchMode.LAVA_RUN }
        else -> allModes
    }

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
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // HEADER BAR
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2B1954))
                        .testTag("mode_select_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "GAME MODES",
                        color = Color(0xFFFFD54F),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Choose your party style!",
                        color = Color(0xFFD0BCFF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Balance spacer
                Spacer(modifier = Modifier.size(42.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // KIDS ASSIST BUMPER TOGGLE CARD
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF231545),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (kidsAssistState) Color(0xFF00E676) else Color(0x448B5CF6),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        kidsAssistState = !kidsAssistState
                        progression.kidsAssistEnabled = kidsAssistState
                    }
                    .testTag("kids_assist_toggle_card")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (kidsAssistState) Color(0xFF00E676) else Color(0xFF3B2474)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(if (kidsAssistState) "🛡️" else "🛡️", fontSize = 18.sp)
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Kids Safe Guard Rails",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                                if (kidsAssistState) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF00E676)
                                    ) {
                                        Text(
                                            text = "ACTIVE",
                                            color = Color.Black,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Bouncy rainbow fences keep young players on the arena!",
                                color = Color(0xFFD0BCFF),
                                fontSize = 10.sp
                            )
                        }
                    }

                    Switch(
                        checked = kidsAssistState,
                        onCheckedChange = {
                            kidsAssistState = it
                            progression.kidsAssistEnabled = it
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF00E676),
                            uncheckedThumbColor = Color(0xFF8B5CF6),
                            uncheckedTrackColor = Color(0xFF2B1954)
                        ),
                        modifier = Modifier.testTag("kids_assist_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // CATEGORY FILTER CHIPS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tabs = listOf(
                    "🌟 All Modes" to 0,
                    "🍬 Kids & Young" to 1,
                    "🔥 Fast & Crazy" to 2
                )
                tabs.forEach { (label, index) ->
                    val isSelected = selectedTab == index
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) Color(0xFF7C4DFF) else Color(0xFF231545),
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFFFFD54F) else Color(0x338B5CF6),
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedTab = index }
                            .testTag("mode_tab_$index")
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else Color(0xFFD0BCFF),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // MODES LIST
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(displayedModes) { mode ->
                    ModeCard(
                        mode = mode,
                        onPlay = { onSelectMode(mode) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ModeCard(
    mode: MatchMode,
    onPlay: () -> Unit
) {
    val themeColor = Color(mode.themeColorHex)

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF231545),
        shadowElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, themeColor.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            .clickable { onPlay() }
            .testTag("mode_card_${mode.name.lowercase()}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Emoji Icon + Title + Age Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(themeColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(mode.iconEmoji, fontSize = 24.sp)
                    }

                    Column {
                        Text(
                            text = mode.title,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = if (mode.isKidsFriendly) "⭐ Super Easy & Fun" else "⚡ Fast-Paced Action",
                            color = Color(0xFFFFD54F),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = themeColor.copy(alpha = 0.25f),
                    modifier = Modifier.border(1.dp, themeColor, RoundedCornerShape(12.dp))
                ) {
                    Text(
                        text = mode.tagLabel,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Description
            Text(
                text = mode.description,
                color = Color(0xFFEADDFF),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            // Play CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⏱️", fontSize = 12.sp)
                    Text("1-3 Min Match", color = Color(0xFFB39DDB), fontSize = 11.sp)
                }

                Button(
                    onClick = onPlay,
                    colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "PLAY",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
