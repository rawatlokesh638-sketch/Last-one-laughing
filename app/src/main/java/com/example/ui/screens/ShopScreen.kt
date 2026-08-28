package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingBag
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
import com.example.data.CosmeticsCatalog
import com.example.data.ProgressionManager
import com.example.model.CosmeticItem
import com.example.model.CosmeticType

@Composable
fun ShopScreen(
    progression: ProgressionManager,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(CosmeticType.CHARACTER) }
    var userCoins by remember { mutableStateOf(progression.coins) }
    var selectedItemForPreview by remember { mutableStateOf<CosmeticItem?>(null) }

    val itemsToShow = when (selectedTab) {
        CosmeticType.CHARACTER -> CosmeticsCatalog.characters
        CosmeticType.OUTFIT -> CosmeticsCatalog.outfits
        CosmeticType.TRAIL -> CosmeticsCatalog.trails
        CosmeticType.EMOTE -> CosmeticsCatalog.emotes
        CosmeticType.VICTORY_EFFECT -> CosmeticsCatalog.victoryEffects
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
                        .testTag("shop_back_button")
                        .clip(CircleShape)
                        .background(Color(0xFF2B1954))
                        .border(1.dp, Color(0x668B5CF6), CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFFEADDFF))
                }

                Text(
                    text = "CHAOS WARDROBE",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )

                // Coins pill
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

            Spacer(modifier = Modifier.height(14.dp))

            // CATEGORY TABS
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val tabs = listOf(
                    CosmeticType.CHARACTER to "Characters",
                    CosmeticType.OUTFIT to "Outfits",
                    CosmeticType.TRAIL to "Trails",
                    CosmeticType.EMOTE to "Emotes",
                    CosmeticType.VICTORY_EFFECT to "Victory FX"
                )
                items(tabs.size) { idx ->
                    val (tabType, tabTitle) = tabs[idx]
                    val isSelected = selectedTab == tabType
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) Color(0xFF7C4DFF) else Color(0xFF231545),
                        modifier = Modifier
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFFD0BCFF) else Color(0x338B5CF6),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                selectedTab = tabType
                                selectedItemForPreview = null
                            }
                    ) {
                        Text(
                            text = tabTitle,
                            color = if (isSelected) Color.White else Color(0xFFD0BCFF),
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // NO PAY-TO-WIN NOTICE
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF2B1954),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x448B5CF6), RoundedCornerShape(12.dp))
            ) {
                Text(
                    text = "✨ 100% Cosmetic Only! Pure fun, zero pay-to-win advantages.",
                    color = Color(0xFFD0BCFF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ITEMS GRID
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(itemsToShow) { item ->
                    val isUnlocked = progression.isCosmeticUnlocked(item.id)
                    val isEquipped = when (item.type) {
                        CosmeticType.CHARACTER -> progression.equippedCharacterId == item.id
                        CosmeticType.OUTFIT -> progression.equippedOutfitId == item.id
                        CosmeticType.TRAIL -> progression.equippedTrailId == item.id
                        CosmeticType.EMOTE -> progression.equippedEmoteId == item.id
                        CosmeticType.VICTORY_EFFECT -> progression.equippedVictoryEffectId == item.id
                    }

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (isEquipped) Color(0xFF3B2474) else Color(0xFF231545),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isEquipped) 2.dp else 1.dp,
                                color = if (isEquipped) Color(0xFFFF4081) else Color(0x448B5CF6),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable {
                                selectedItemForPreview = item
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(item.primaryColorHex)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(item.iconEmoji, fontSize = 28.sp)
                            }

                            Text(
                                text = item.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = item.description,
                                color = Color(0xFFD0BCFF),
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 2
                            )

                            if (isEquipped) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF10B981)
                                ) {
                                    Text(
                                        text = "EQUIPPED",
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            } else if (isUnlocked) {
                                Button(
                                    onClick = {
                                        when (item.type) {
                                            CosmeticType.CHARACTER -> progression.equippedCharacterId = item.id
                                            CosmeticType.OUTFIT -> progression.equippedOutfitId = item.id
                                            CosmeticType.TRAIL -> progression.equippedTrailId = item.id
                                            CosmeticType.EMOTE -> progression.equippedEmoteId = item.id
                                            CosmeticType.VICTORY_EFFECT -> progression.equippedVictoryEffectId = item.id
                                        }
                                        userCoins = progression.coins // trigger recompose
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().height(36.dp)
                                ) {
                                    Text("EQUIP", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = {
                                        if (progression.coins >= item.price) {
                                             progression.coins -= item.price
                                            progression.unlockCosmetic(item.id)
                                            userCoins = progression.coins
                                        }
                                    },
                                    enabled = progression.coins >= item.price,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().height(36.dp)
                                ) {
                                    Text("🪙 ${item.price}", color = Color(0xFF3E2723), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
