package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioManager
import com.example.data.ProgressionManager
import com.example.game.engine.MatchEngine
import com.example.model.GameState
import com.example.model.MatchMode
import com.example.ui.components.ArenaCanvas
import com.example.ui.components.GameplayHud
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private lateinit var audioManager: AudioManager
    private lateinit var progressionManager: ProgressionManager
    private lateinit var matchEngine: MatchEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        audioManager = AudioManager(this)
        progressionManager = ProgressionManager(this)
        matchEngine = MatchEngine(progressionManager, audioManager)

        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color(0xFF0F172A),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0)
                ) { innerPadding ->
                    GameAppRoot(
                        engine = matchEngine,
                        progression = progressionManager,
                        audio = audioManager,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        audioManager.stopBgm()
    }

    override fun onResume() {
        super.onResume()
        if (matchEngine.gameState.value == GameState.PLAYING) {
            audioManager.startBgm()
        }
    }
}

@Composable
fun GameAppRoot(
    engine: MatchEngine,
    progression: ProgressionManager,
    audio: AudioManager,
    modifier: Modifier = Modifier
) {
    val gameState by engine.gameState.collectAsState()
    var showPauseDialog by remember { mutableStateOf(false) }

    // 60 FPS Game Loop Engine
    LaunchedEffect(Unit) {
        var lastTime = System.nanoTime()
        while (true) {
            val now = System.nanoTime()
            val dt = ((now - lastTime) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
            lastTime = now
            if (!showPauseDialog) {
                engine.update(dt)
            }
            delay(16L)
        }
    }

    // Handle Android Back Navigation
    BackHandler(enabled = gameState != GameState.MAIN_MENU) {
        when (gameState) {
            GameState.SPLASH -> {}
            GameState.MAIN_MENU -> {}
            GameState.MODE_SELECT, GameState.LOBBY, GameState.FRIENDS_ROOM, GameState.SHOP, GameState.MISSIONS, GameState.SETTINGS -> {
                engine.setGameState(GameState.MAIN_MENU)
            }
            GameState.PLAYING, GameState.SPECTATING -> {
                showPauseDialog = true
            }
            GameState.RESULTS -> {
                engine.setGameState(GameState.MAIN_MENU)
            }
            else -> engine.setGameState(GameState.MAIN_MENU)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (gameState) {
            GameState.SPLASH -> {
                SplashScreen(
                    onSplashFinished = {
                        engine.setGameState(GameState.MAIN_MENU)
                    }
                )
            }
            GameState.MAIN_MENU -> {
                MainMenuScreen(
                    progression = progression,
                    onStartQuickPlay = {
                        engine.startMatchmaking(MatchMode.QUICK_CHAOS)
                    },
                    onOpenModes = {
                        engine.setGameState(GameState.MODE_SELECT)
                    },
                    onOpenFriends = {
                        engine.setGameState(GameState.FRIENDS_ROOM)
                    },
                    onStartPractice = {
                        engine.startMatchmaking(MatchMode.PRACTICE)
                    },
                    onOpenShop = {
                        engine.setGameState(GameState.SHOP)
                    },
                    onOpenMissions = {
                        engine.setGameState(GameState.MISSIONS)
                    },
                    onOpenSettings = {
                        engine.setGameState(GameState.SETTINGS)
                    }
                )
            }
            GameState.MODE_SELECT -> {
                ModeSelectScreen(
                    progression = progression,
                    onSelectMode = { selectedMode ->
                        engine.startMatchmaking(selectedMode)
                    },
                    onBack = {
                        engine.setGameState(GameState.MAIN_MENU)
                    }
                )
            }
            GameState.LOBBY -> {
                LobbyScreen(
                    engine = engine,
                    onStartMatch = {
                        engine.startCountdown()
                    },
                    onBack = {
                        engine.setGameState(GameState.MAIN_MENU)
                    }
                )
            }
            GameState.FRIENDS_ROOM -> {
                FriendsRoomScreen(
                    engine = engine,
                    onCreateRoom = {
                        engine.startMatchmaking(MatchMode.FRIENDS_ROOM)
                    },
                    onJoinRoom = { code ->
                        engine.startMatchmaking(MatchMode.FRIENDS_ROOM, code)
                    },
                    onBack = {
                        engine.setGameState(GameState.MAIN_MENU)
                    }
                )
            }
            GameState.MATCH_COUNTDOWN, GameState.PLAYING, GameState.SPECTATING, GameState.EVENT_ALERT -> {
                // Game World Canvas
                ArenaCanvas(engine = engine)

                // On-Screen HUD and Controls
                GameplayHud(
                    engine = engine,
                    onPauseClick = {
                        showPauseDialog = true
                    }
                )

                // Match Start Countdown Overlay (3, 2, 1, GO!)
                if (gameState == GameState.MATCH_COUNTDOWN) {
                    val count = (engine.countdownTimerSec.toInt() + 1).coerceAtLeast(1)
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xCC000000),
                            modifier = Modifier.size(140.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (count > 0) "$count" else "GO!",
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 64.sp
                                )
                            }
                        }
                    }
                }
            }
            GameState.RESULTS -> {
                ResultsScreen(
                    engine = engine,
                    onPlayAgain = {
                        engine.startMatchmaking(engine.matchMode.value)
                        engine.startCountdown()
                    },
                    onMainMenu = {
                        engine.setGameState(GameState.MAIN_MENU)
                    }
                )
            }
            GameState.SHOP, GameState.COSMETICS -> {
                ShopScreen(
                    progression = progression,
                    onBack = {
                        engine.setGameState(GameState.MAIN_MENU)
                    }
                )
            }
            GameState.MISSIONS -> {
                MissionsScreen(
                    progression = progression,
                    onBack = {
                        engine.setGameState(GameState.MAIN_MENU)
                    }
                )
            }
            GameState.SETTINGS -> {
                SettingsScreen(
                    progression = progression,
                    audio = audio,
                    onBack = {
                        engine.setGameState(GameState.MAIN_MENU)
                    }
                )
            }
            else -> {
                MainMenuScreen(
                    progression = progression,
                    onStartQuickPlay = { engine.startMatchmaking(MatchMode.QUICK_CHAOS) },
                    onOpenModes = { engine.setGameState(GameState.MODE_SELECT) },
                    onOpenFriends = { engine.setGameState(GameState.FRIENDS_ROOM) },
                    onStartPractice = { engine.startMatchmaking(MatchMode.PRACTICE) },
                    onOpenShop = { engine.setGameState(GameState.SHOP) },
                    onOpenMissions = { engine.setGameState(GameState.MISSIONS) },
                    onOpenSettings = { engine.setGameState(GameState.SETTINGS) }
                )
            }
        }

        // PAUSE MODAL DIALOG
        if (showPauseDialog) {
            AlertDialog(
                onDismissRequest = { showPauseDialog = false },
                title = {
                    Text("GAME PAUSED", fontWeight = FontWeight.Black, color = Color.White)
                },
                text = {
                    Text("Take a breather or head back to the main menu.", color = Color.White.copy(alpha = 0.8f))
                },
                confirmButton = {
                    Button(
                        onClick = { showPauseDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Text("RESUME", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showPauseDialog = false
                            engine.eventManager.endEvent()
                            engine.setGameState(GameState.MAIN_MENU)
                        }
                    ) {
                        Text("LEAVE MATCH", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Color(0xFF1E293B),
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}
