package com.example.game.engine

import com.example.audio.AudioManager
import com.example.audio.SoundEffect
import com.example.data.CosmeticsCatalog
import com.example.data.ProgressionManager
import com.example.game.arena.ArenaManager
import com.example.game.bot.BotManager
import com.example.game.events.EventManager
import com.example.game.physics.ParticleManager
import com.example.game.physics.PhysicsEngine
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.cos
import kotlin.math.sin

data class MatchResult(
    val winner: Player?,
    val localPlayerRank: Int,
    val totalPlayers: Int,
    val survivalTimeSec: Float,
    val xpEarned: Int,
    val coinsEarned: Int,
    val leveledUp: Boolean,
    val bestMoment: ChaosMoment
)

class MatchEngine(
    val progression: ProgressionManager,
    val audio: AudioManager
) {
    val arena = ArenaManager()
    val particles = ParticleManager()
    val physics = PhysicsEngine(arena, particles, audio)
    val bots = BotManager(arena)
    val eventManager = EventManager(arena, physics, particles, audio)
    val viralTracker = ViralMomentTracker()

    private val _gameState = MutableStateFlow(GameState.SPLASH)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _matchMode = MutableStateFlow(MatchMode.QUICK_CHAOS)
    val matchMode: StateFlow<MatchMode> = _matchMode.asStateFlow()

    private val _roomCode = MutableStateFlow("CHAOS1")
    val roomCode: StateFlow<String> = _roomCode.asStateFlow()

    private val _eliminationFeed = MutableStateFlow<List<EliminationFeedItem>>(emptyList())
    val eliminationFeed: StateFlow<List<EliminationFeedItem>> = _eliminationFeed.asStateFlow()

    val players = mutableListOf<Player>()
    val alivePlayers: List<Player> get() = players.filter { it.isAlive }

    var matchTimerSec: Float = 0f
    var countdownTimerSec: Float = 3.5f
    var nextEventIntervalTimer: Float = 6f
    var roundNumber: Int = 1
    var isMatchFinished: Boolean = false
    var winnerPlayer: Player? = null
    var lastMatchResult: MatchResult? = null

    // Local Player Controls
    var localMoveInput = Vector2D(0f, 0f)
    var localJumpPressed = false
    var localDashPressed = false

    fun setGameState(state: GameState) {
        _gameState.value = state
        if (state == GameState.PLAYING) {
            audio.startBgm()
        } else if (state == GameState.MAIN_MENU || state == GameState.RESULTS) {
            // keep ambient
        }
    }

    fun startMatchmaking(mode: MatchMode, customCode: String? = null) {
        _matchMode.value = mode
        if (customCode != null) {
            _roomCode.value = customCode
        } else {
            val randomCodes = listOf("LOL88", "CHAOS", "SURV7", "GIGGL", "BOOM4", "HAH99")
            _roomCode.value = randomCodes.random()
        }
        setGameState(GameState.LOBBY)
        setupLobbyPlayers(mode)
    }

    private fun setupLobbyPlayers(mode: MatchMode) {
        players.clear()
        val totalCount = if (mode == MatchMode.PRACTICE) 8 else 10

        // Local Player
        val localCosmeticChar = CosmeticsCatalog.characters.find { it.id == progression.equippedCharacterId }
        val localPlayer = Player(
            id = "player_local",
            name = progression.playerName,
            isLocalPlayer = true,
            isBot = false,
            characterId = progression.equippedCharacterId,
            outfitId = progression.equippedOutfitId,
            trailId = progression.equippedTrailId,
            emoteId = progression.equippedEmoteId,
            avatarEmoji = localCosmeticChar?.iconEmoji ?: "😃",
            baseColorHex = localCosmeticChar?.primaryColorHex ?: 0xFFFF5722
        )
        players.add(localPlayer)

        // Bot Players with funny party names
        val botNames = listOf(
            "GiggleMaster", "BananaSlip", "ChuckleNinja", "ChaosGremlin",
            "SpeedyToes", "BumperKing", "LavaDiver", "SirLaughsALot",
            "EpicBonker", "ClutchGod", "TsunamiSurfer", "OopsIFell"
        ).shuffled()

        val availableAvatars = CosmeticsCatalog.characters.shuffled()

        for (i in 1 until totalCount) {
            val charItem = availableAvatars[i % availableAvatars.size]
            val diff = when (i % 4) {
                0 -> BotDifficulty.HARD
                1 -> BotDifficulty.NORMAL
                2 -> BotDifficulty.CHAOTIC
                else -> BotDifficulty.EASY
            }
            players.add(
                Player(
                    id = "bot_$i",
                    name = botNames[i % botNames.size],
                    isLocalPlayer = false,
                    isBot = true,
                    botDifficulty = diff,
                    characterId = charItem.id,
                    avatarEmoji = charItem.iconEmoji,
                    baseColorHex = charItem.primaryColorHex
                )
            )
        }

        bots.registerBots(players)
    }

    fun startCountdown() {
        setGameState(GameState.MATCH_COUNTDOWN)
        countdownTimerSec = 3.2f
        audio.playSound(SoundEffect.COUNTDOWN_TICK)
    }

    fun launchMatch() {
        val currentMode = _matchMode.value
        arena.setupForMode(currentMode, progression.kidsAssistEnabled)
        particles.clear()
        viralTracker.clear()
        eventManager.endEvent()
        _eliminationFeed.value = emptyList()

        matchTimerSec = 0f
        roundNumber = 1
        isMatchFinished = false
        winnerPlayer = null
        nextEventIntervalTimer = 4.5f

        // Position players in circle around center platform
        val count = players.size
        for (i in players.indices) {
            val angle = (i.toFloat() / count) * (Math.PI * 2).toFloat()
            val radius = 95f
            val p = players[i]
            p.position = Vector2D(cos(angle) * radius, sin(angle) * radius)
            p.velocity = Vector2D(0f, 0f)
            p.heightZ = 0f
            p.velocityZ = 0f
            p.isGrounded = true
            p.isEliminated = false
            p.eliminationRank = 0
            p.eliminationReason = ""
            p.eliminationTimeSec = 0f
            p.eliminationsScored = 0
            p.nearMissCount = 0
            p.pushesLanded = 0
            p.timesPushed = 0
            p.isStunned = false
            p.isFrozen = false
            p.hasCrown = false
            p.hasBomb = false
            p.bombTimerSec = 0f
            p.candyScore = 0
            p.crownHoldSeconds = 0f
        }

        // Mode specific starters
        if (currentMode == MatchMode.CROWN_CHASE && players.isNotEmpty()) {
            players.random().hasCrown = true
        } else if (currentMode == MatchMode.BOMB_PARTY && players.isNotEmpty()) {
            val bombStart = players.random()
            bombStart.hasBomb = true
            bombStart.bombTimerSec = 12f
        }

        audio.playSound(SoundEffect.COUNTDOWN_GO)
        particles.spawnDustPuff(Vector2D(0f, 0f), 20)
        setGameState(GameState.PLAYING)
    }

    fun update(deltaTimeSec: Float) {
        val currentState = _gameState.value

        if (currentState == GameState.MATCH_COUNTDOWN) {
            val oldSec = countdownTimerSec.toInt()
            countdownTimerSec -= deltaTimeSec
            val newSec = countdownTimerSec.toInt()
            if (newSec < oldSec && newSec > 0) {
                audio.playSound(SoundEffect.COUNTDOWN_TICK)
            }
            if (countdownTimerSec <= 0f) {
                launchMatch()
            }
            return
        }

        if (currentState != GameState.PLAYING && currentState != GameState.SPECTATING) return

        matchTimerSec += deltaTimeSec
        arena.update(deltaTimeSec)
        particles.update(deltaTimeSec)

        // Event scheduler
        if (!eventManager.isWarningPhase && !eventManager.isEventActive) {
            nextEventIntervalTimer -= deltaTimeSec
            if (nextEventIntervalTimer <= 0f) {
                val nextEvt = eventManager.pickNextEvent(roundNumber)
                eventManager.startWarning(nextEvt)
                roundNumber++
                nextEventIntervalTimer = 8f // interval after event ends
            }
        }

        // Update active event
        eventManager.update(deltaTimeSec, alivePlayers) { victim, reason ->
            eliminatePlayer(victim, reason)
        }

        // Update Bot actions
        bots.updateBots(
            deltaTimeSec,
            eventManager.currentEvent,
            eventManager.isEventActive,
            eventManager.activeSafeColorTag,
            players
        ) { bot, moveInput, jump, dash ->
            physics.updatePlayer(bot, moveInput, jump, dash, deltaTimeSec) { victim, reason ->
                eliminatePlayer(victim, reason)
            }
        }

        // Update Local Player
        val local = players.find { it.isLocalPlayer }
        if (local != null && local.isAlive) {
            physics.updatePlayer(
                local,
                localMoveInput,
                localJumpPressed,
                localDashPressed,
                deltaTimeSec
            ) { victim, reason ->
                eliminatePlayer(victim, reason)
            }
        }

        // Reset frame triggers
        localJumpPressed = false
        localDashPressed = false

        // Resolve Player-to-Player collisions
        physics.resolvePlayerCollisions(players)

        val currentMode = _matchMode.value
        val currentAlive = alivePlayers

        // MODE 1: Candy Rush Win Condition
        if (currentMode == MatchMode.CANDY_RUSH && !isMatchFinished) {
            val candyWinner = currentAlive.find { it.candyScore >= 15 }
            if (candyWinner != null) {
                handleMatchWinner(candyWinner)
                return
            }
            if (matchTimerSec >= 45f) {
                // Time up! Highest candy score wins
                val bestScorer = currentAlive.maxByOrNull { it.candyScore } ?: currentAlive.firstOrNull()
                if (bestScorer != null) {
                    handleMatchWinner(bestScorer)
                    return
                }
            }
        }

        // MODE 2: Crown Chase Win Condition
        if (currentMode == MatchMode.CROWN_CHASE && !isMatchFinished) {
            val crownCarrier = currentAlive.find { it.hasCrown }
            if (crownCarrier != null) {
                crownCarrier.crownHoldSeconds += deltaTimeSec
                if (crownCarrier.crownHoldSeconds >= 20f) {
                    handleMatchWinner(crownCarrier)
                    return
                }
            } else if (currentAlive.isNotEmpty()) {
                // If nobody has crown, give to a random alive player
                currentAlive.random().hasCrown = true
            }
        }

        // MODE 3: Bomb Party Ticking Fuse & Explode
        if (currentMode == MatchMode.BOMB_PARTY && !isMatchFinished) {
            val bombCarrier = currentAlive.find { it.hasBomb }
            if (bombCarrier != null) {
                bombCarrier.bombTimerSec -= deltaTimeSec
                if (bombCarrier.bombTimerSec <= 0f) {
                    // BOOM!
                    eliminatePlayer(bombCarrier, "Bomb Exploded! 💣💥")
                    val nextAlive = alivePlayers
                    if (nextAlive.isNotEmpty()) {
                        val nextVictim = nextAlive.random()
                        nextVictim.hasBomb = true
                        nextVictim.bombTimerSec = (8f + Math.random() * 4f).toFloat()
                    }
                }
            } else if (currentAlive.size > 1) {
                val nextVictim = currentAlive.random()
                nextVictim.hasBomb = true
                nextVictim.bombTimerSec = 10f
            }
        }

        // Standard Survival Royale Win Condition
        if (currentAlive.size == 1 && !isMatchFinished) {
            // WINNER FOUND!
            handleMatchWinner(currentAlive.first())
        } else if (currentAlive.size == 2 && currentMode != MatchMode.CROWN_CHASE) {
            // Final 2 Duel!
            currentAlive.forEach { it.hasCrown = true }
        }
    }

    fun triggerLocalEmote(emoji: String) {
        val local = players.find { it.isLocalPlayer } ?: return
        local.activeEmote = emoji
        local.emoteTimer = 2.0f
        audio.playSound(SoundEffect.SAFE_CHIME)
    }

    private fun eliminatePlayer(player: Player, reason: String) {
        if (player.isEliminated) return
        player.isEliminated = true
        player.eliminationTimeSec = matchTimerSec
        player.eliminationReason = reason
        player.eliminationRank = alivePlayers.size + 1

        audio.playSound(SoundEffect.ELIMINATION_SPLAT)
        particles.spawnExplosionFx(player.position)
        particles.showFloatingText("ELIMINATED! 💥", player.position, 0xFFFF3B30)

        // Add to Elimination Feed
        val feedItem = EliminationFeedItem(
            id = "elim_${player.id}_${System.currentTimeMillis()}",
            victimName = player.name,
            victimEmoji = player.avatarEmoji,
            reason = reason,
            isLocalVictim = player.isLocalPlayer
        )
        val currentFeed = _eliminationFeed.value.toMutableList()
        currentFeed.add(0, feedItem)
        if (currentFeed.size > 5) {
            _eliminationFeed.value = currentFeed.take(5)
        } else {
            _eliminationFeed.value = currentFeed
        }

        if (player.isLocalPlayer) {
            audio.playSound(SoundEffect.DEFEAT_TROMBONE)
            setGameState(GameState.SPECTATING)
        }

        // Check if only 1 remains
        val remaining = alivePlayers
        if (remaining.size == 1 && !isMatchFinished) {
            handleMatchWinner(remaining.first())
        } else if (remaining.isEmpty() && !isMatchFinished) {
            // Draw
            handleMatchWinner(player)
        }
    }

    private fun handleMatchWinner(winner: Player) {
        isMatchFinished = true
        winnerPlayer = winner
        winner.hasCrown = true

        val isLocalWinner = winner.isLocalPlayer
        if (isLocalWinner) {
            audio.playSound(SoundEffect.VICTORY_FANFARE)
            audio.playSound(SoundEffect.CROWD_LAUGH)
            progression.wins++
            progression.winStreak++
            if (progression.winStreak > progression.bestWinStreak) {
                progression.bestWinStreak = progression.winStreak
            }
            progression.updateMissionProgress("m5", 1)
            progression.updateAchievementProgress("a2", progression.wins)
        } else {
            progression.winStreak = 0
        }

        progression.matchesPlayed++
        progression.updateMissionProgress("m1", 1)
        progression.updateAchievementProgress("a1", progression.matchesPlayed)

        val localPlayer = players.find { it.isLocalPlayer } ?: winner
        val localRank = if (isLocalWinner) 1 else localPlayer.eliminationRank

        if (localRank <= 3) {
            progression.updateMissionProgress("m2", 1)
        }

        // Rewards calculation
        val baseCoins = if (isLocalWinner) 300 else (12 - localRank) * 25 + 50
        val baseXP = if (isLocalWinner) 250 else (12 - localRank) * 20 + 40
        val leveledUp = progression.addXp(baseXP)
        progression.addCoins(baseCoins)

        val bestMoment = viralTracker.getBestChaosMoment(localPlayer, isLocalWinner)

        particles.spawnConfettiShower(winner.position, 80)

        lastMatchResult = MatchResult(
            winner = winner,
            localPlayerRank = localRank,
            totalPlayers = players.size,
            survivalTimeSec = matchTimerSec,
            xpEarned = baseXP,
            coinsEarned = baseCoins,
            leveledUp = leveledUp,
            bestMoment = bestMoment
        )

        setGameState(GameState.RESULTS)
    }
}
