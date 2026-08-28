package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
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
import com.example.model.MatchMode

@Composable
fun FriendsRoomScreen(
    engine: MatchEngine,
    onJoinRoom: (String) -> Unit,
    onCreateRoom: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var roomInput by remember { mutableStateOf("") }

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
                        .testTag("friends_back_button")
                        .clip(CircleShape)
                        .background(Color(0xFF2B1954))
                        .border(1.dp, Color(0x668B5CF6), CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFFEADDFF))
                }

                Text(
                    text = "PLAY WITH FRIENDS",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.size(44.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("👥 🎉", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "PRIVATE PARTY ROOM",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Host a room or join your friends with a room code!",
                color = Color(0xFFD0BCFF),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // CREATE ROOM CARD
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF231545),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x668B5CF6), RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("HOST A NEW ROOM", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    Text("Generate a unique code and invite up to 11 players.", color = Color(0xFFD0BCFF), fontSize = 12.sp, textAlign = TextAlign.Center)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(Color(0xFF7C4DFF), Color(0xFFEC4899))
                                )
                            )
                            .clickable { onCreateRoom() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("CREATE PRIVATE ROOM", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // JOIN ROOM CARD
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF231545),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x668B5CF6), RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("JOIN EXISTING ROOM", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    
                    OutlinedTextField(
                        value = roomInput,
                        onValueChange = { if (it.length <= 8) roomInput = it.uppercase() },
                        placeholder = { Text("ENTER ROOM CODE (e.g. CHAOS1)", color = Color(0x99D0BCFF)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF7C4DFF),
                            unfocusedBorderColor = Color(0x668B5CF6)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { if (roomInput.isNotBlank()) onJoinRoom(roomInput) },
                        enabled = roomInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("JOIN ROOM", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
