package com.example.game.bot

import com.example.game.arena.ArenaManager
import com.example.game.arena.ArenaPlatform
import com.example.model.BotDifficulty
import com.example.model.EventType
import com.example.model.Player
import com.example.model.Vector2D
import kotlin.math.cos
import kotlin.math.sin

data class BotController(
    val player: Player,
    var targetPosition: Vector2D = Vector2D(0f, 0f),
    var decisionTimer: Float = 0f,
    var mistakeChance: Float = 0.2f,
    var jumpTimer: Float = 0f,
    var dashTimer: Float = 0f,
    var panicTimer: Float = 0f,
    var emoteCooldown: Float = 5f
)

class BotManager(private val arena: ArenaManager) {
    private val botControllers = mutableListOf<BotController>()

    fun registerBots(players: List<Player>) {
        botControllers.clear()
        players.filter { it.isBot }.forEach { botPlayer ->
            val mistakeRate = when (botPlayer.botDifficulty) {
                BotDifficulty.EASY -> 0.45f
                BotDifficulty.NORMAL -> 0.25f
                BotDifficulty.HARD -> 0.10f
                BotDifficulty.CHAOTIC -> 0.35f
            }
            botControllers.add(
                BotController(
                    player = botPlayer,
                    mistakeChance = mistakeRate,
                    decisionTimer = (Math.random() * 0.5).toFloat()
                )
            )
        }
    }

    fun updateBots(
        deltaTimeSec: Float,
        currentEvent: EventType?,
        isEventActive: Boolean,
        activeSafeColor: Int,
        allPlayers: List<Player>,
        onBotAction: (player: Player, moveInput: Vector2D, jump: Boolean, dash: Boolean) -> Unit
    ) {
        val alivePlayers = allPlayers.filter { it.isAlive }

        botControllers.forEach { controller ->
            val bot = controller.player
            if (!bot.isAlive) return@forEach

            controller.decisionTimer -= deltaTimeSec
            controller.jumpTimer -= deltaTimeSec
            controller.dashTimer -= deltaTimeSec
            controller.emoteCooldown -= deltaTimeSec

            // Occasional funny emote
            if (controller.emoteCooldown <= 0f && Math.random() < 0.15) {
                controller.emoteCooldown = (4f + Math.random() * 8f).toFloat()
                val funnyEmotes = listOf("😂", "😜", "👑", "🔥", "❓", "💪", "💀")
                bot.activeEmote = funnyEmotes.random()
                bot.emoteTimer = 1.6f
            }

            // During DON'T MOVE event: Freeze in place unless panic mistake
            if (currentEvent == EventType.DONT_MOVE && isEventActive) {
                val willMakeMistake = Math.random() < (controller.mistakeChance * 0.05)
                if (!willMakeMistake) {
                    onBotAction(bot, Vector2D(0f, 0f), false, false)
                    return@forEach
                }
            }

            // Periodically pick target position based on event / safety
            if (controller.decisionTimer <= 0f) {
                controller.decisionTimer = (0.2f + Math.random() * 0.4f).toFloat()
                controller.targetPosition = computeSafeTarget(bot, currentEvent, isEventActive, activeSafeColor, alivePlayers)
            }

            // Hazard avoidance adjustments
            var steerVector = (controller.targetPosition - bot.position)
            val steerDist = steerVector.distanceTo(Vector2D(0f, 0f))
            var moveInput = if (steerDist > 10f) steerVector.normalize() else Vector2D(0f, 0f)

            // Dodge Rolling Balls
            arena.rollingBalls.forEach { ball ->
                val dist = bot.position.distanceTo(ball.position)
                if (dist < 110f) {
                    val dodgeDir = (bot.position - ball.position).normalize()
                    moveInput = Vector2D(moveInput.x * 0.3f + dodgeDir.x * 1.5f, moveInput.y * 0.3f + dodgeDir.y * 1.5f).normalize()
                    if (controller.jumpTimer <= 0f && Math.random() < 0.7) {
                        controller.jumpTimer = 1.0f
                    }
                }
            }

            // Dodge Sweeper Bars
            arena.sweeperBars.forEach { bar ->
                val barEndX = bar.pivot.x + cos(bar.angle) * bar.length
                val barEndY = bar.pivot.y + sin(bar.angle) * bar.length
                val distToBar = distanceToPoint(bot.position, Vector2D(barEndX, barEndY))
                if (distToBar < 70f && controller.jumpTimer <= 0f) {
                    controller.jumpTimer = 0.8f
                }
            }

            // Dodge Explosions
            arena.activeExplosions.forEach { exp ->
                val dist = bot.position.distanceTo(exp.position)
                if (dist < exp.maxRadius + 30f) {
                    val fleeDir = (bot.position - exp.position).normalize()
                    moveInput = fleeDir
                }
            }

            // Stay within shrinking arena ring
            val distCenter = bot.position.distanceTo(Vector2D(0f, 0f))
            if (distCenter > arena.currentShrinkRadius - 50f) {
                val centerDir = (Vector2D(0f, 0f) - bot.position).normalize()
                moveInput = centerDir
            }

            // Aggressive Dash Pushing behavior (if near opponent and facing them)
            var doDash = false
            if (controller.dashTimer <= 0f && bot.dashCooldownTimer <= 0f) {
                val closestOpponent = alivePlayers.filter { it.id != bot.id }.minByOrNull { it.position.distanceTo(bot.position) }
                if (closestOpponent != null) {
                    val oppDist = bot.position.distanceTo(closestOpponent.position)
                    if (oppDist in 25f..70f && Math.random() < 0.4) {
                        doDash = true
                        controller.dashTimer = 2.0f
                        moveInput = (closestOpponent.position - bot.position).normalize()
                    }
                }
            }

            val doJump = controller.jumpTimer > 0.5f

            // Intentional mistake simulation
            if (Math.random() < controller.mistakeChance * 0.02) {
                moveInput = Vector2D((Math.random() * 2 - 1).toFloat(), (Math.random() * 2 - 1).toFloat())
            }

            onBotAction(bot, moveInput, doJump, doDash)
        }
    }

    private fun computeSafeTarget(
        bot: Player,
        currentEvent: EventType?,
        isEventActive: Boolean,
        activeSafeColor: Int,
        alivePlayers: List<Player>
    ): Vector2D {
        // Mode 1: Candy Rush -> Go for closest uncollected sweet!
        if (arena.collectibles.isNotEmpty()) {
            val nearestCandy = arena.collectibles
                .filter { !it.isCollected }
                .minByOrNull { it.position.distanceTo(bot.position) }
            if (nearestCandy != null) {
                return nearestCandy.position
            }
        }

        // Mode 2: Color Dance -> Run to active safe color tile!
        if (arena.isColorDanceActive && arena.activeDanceColorTag > 0) {
            val safeTile = arena.platforms
                .filter { it.colorTag == arena.activeDanceColorTag && !it.isFallen }
                .minByOrNull { it.currentCenter.distanceTo(bot.position) }
            if (safeTile != null) {
                return safeTile.currentCenter
            }
        }

        // Mode 3: Crown Chase
        val crownCarrier = alivePlayers.firstOrNull { it.hasCrown }
        if (crownCarrier != null) {
            if (bot.hasCrown) {
                // Run away from closest opponent!
                val closestOpp = alivePlayers.filter { it.id != bot.id }.minByOrNull { it.position.distanceTo(bot.position) }
                if (closestOpp != null) {
                    val fleeDir = (bot.position - closestOpp.position).normalize()
                    return bot.position + fleeDir * 90f
                }
            } else {
                // Chase the crown carrier!
                return crownCarrier.position
            }
        }

        // Mode 4: Bomb Party
        val bombCarrier = alivePlayers.firstOrNull { it.hasBomb }
        if (bombCarrier != null) {
            if (bot.hasBomb) {
                // Chase nearest player to pass the bomb!
                val nearestVictim = alivePlayers.filter { it.id != bot.id }.minByOrNull { it.position.distanceTo(bot.position) }
                if (nearestVictim != null) {
                    return nearestVictim.position
                }
            } else {
                // Flee from bomb carrier!
                val fleeDir = (bot.position - bombCarrier.position).normalize()
                return bot.position + fleeDir * 120f
            }
        }

        // 1. Safe Color Event: Steer directly to matching color platform
        if (currentEvent == EventType.SAFE_COLOR && isEventActive && activeSafeColor > 0) {
            val safePlatform = arena.platforms.firstOrNull { it.colorTag == activeSafeColor && !it.isFallen }
            if (safePlatform != null) {
                return safePlatform.currentCenter
            }
        }

        // 2. Floor is Lava / Rising Water: Steer to high tower platform
        if ((currentEvent == EventType.FLOOR_IS_LAVA || currentEvent == EventType.RISING_WATER) && isEventActive) {
            val highPlatform = arena.platforms.filter { it.elevation >= 35f && !it.isFallen }
                .minByOrNull { it.currentCenter.distanceTo(bot.position) }
            if (highPlatform != null) {
                return highPlatform.currentCenter
            }
        }

        // 3. Falling Platforms: Steer away from warning falling tiles
        if (currentEvent == EventType.FALLING_PLATFORMS) {
            val stablePlatform = arena.platforms.filter { !it.isFallen && !it.isWarningFall }
                .minByOrNull { it.currentCenter.distanceTo(bot.position) }
            if (stablePlatform != null) {
                return stablePlatform.currentCenter
            }
        }

        // 4. Default roam / tactical positioning
        val validPlatforms = arena.platforms.filter { !it.isFallen && !it.isWarningFall }
        val currentPlatform = arena.getPlatformUnder(bot.position, bot.heightZ)
        
        if (currentPlatform != null && !currentPlatform.isFallen) {
            // Stay within current platform with small wander
            val jitter = Vector2D((Math.random() * 40 - 20).toFloat(), (Math.random() * 40 - 20).toFloat())
            return currentPlatform.currentCenter + jitter
        }

        // Steer to center
        return Vector2D(0f, 0f)
    }

    private fun distanceToPoint(p1: Vector2D, p2: Vector2D): Float = p1.distanceTo(p2)
}
