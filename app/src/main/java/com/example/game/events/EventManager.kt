package com.example.game.events

import com.example.audio.AudioManager
import com.example.audio.SoundEffect
import com.example.game.arena.ArenaManager
import com.example.game.arena.RollingBall
import com.example.game.arena.FallingMeteor
import com.example.game.arena.ExplosionHazard
import com.example.game.physics.ParticleManager
import com.example.game.physics.PhysicsEngine
import com.example.model.EventType
import com.example.model.Player
import com.example.model.Vector2D
import kotlin.math.cos
import kotlin.math.sin

class EventManager(
    private val arena: ArenaManager,
    private val physics: PhysicsEngine,
    private val particles: ParticleManager,
    private val audio: AudioManager
) {
    var currentEvent: EventType? = null
    var isWarningPhase: Boolean = false
    var isEventActive: Boolean = false
    var eventTimer: Float = 0f
    var eventDuration: Float = 15f
    var warningDuration: Float = 3.5f
    var activeSafeColorTag: Int = 0 // 1=Red, 2=Blue, 3=Green, 4=Yellow
    
    // Tracking for events like Don't Move
    private val initialPlayerPositions = mutableMapOf<String, Vector2D>()
    private var subHazardSpawnTimer: Float = 0f

    private val availableEvents = EventType.values().toList()

    fun pickNextEvent(roundNumber: Int): EventType {
        // As rounds progress, increase chance of high difficulty events
        val candidates = if (roundNumber <= 2) {
            availableEvents.filter { it.dangerLevel <= 2 }
        } else {
            availableEvents
        }
        return candidates.random()
    }

    fun startWarning(event: EventType) {
        currentEvent = event
        isWarningPhase = true
        isEventActive = false
        eventTimer = warningDuration
        audio.playSound(SoundEffect.EVENT_WARNING)
        particles.triggerScreenShake(12f, 0.5f)

        // Event-specific setup
        when (event) {
            EventType.SAFE_COLOR -> {
                activeSafeColorTag = (1..4).random()
            }
            EventType.FALLING_PLATFORMS -> {
                // Mark 3-5 random tiles to fall
                val tiles = arena.platforms.filter { it.id >= 11 && !it.isFallen }.shuffled()
                tiles.take(4).forEach { it.isWarningFall = true }
            }
            else -> {}
        }
    }

    fun activateEvent() {
        val event = currentEvent ?: return
        isWarningPhase = false
        isEventActive = true
        eventTimer = when (event) {
            EventType.DONT_MOVE -> 6.5f
            EventType.SAFE_COLOR -> 8f
            EventType.FLOOR_IS_LAVA -> 12f
            EventType.RISING_WATER -> 12f
            EventType.CHAOS_MODE -> 14f
            else -> 12f
        }
        eventDuration = eventTimer

        audio.playSound(SoundEffect.COUNTDOWN_GO)

        when (event) {
            EventType.FLOOR_IS_LAVA -> {
                arena.isLavaActive = true
                arena.lavaLevel = 25f
            }
            EventType.RISING_WATER -> {
                arena.isWaterActive = true
                arena.waterLevel = 15f
            }
            EventType.SHRINKING_ARENA -> {
                arena.currentShrinkRadius = 240f
            }
            EventType.GIANT_ROLLING_BALL -> {
                arena.rollingBalls.add(
                    RollingBall(
                        position = Vector2D(-220f, -100f),
                        velocity = Vector2D(280f, 190f),
                        radius = 42f
                    )
                )
                arena.rollingBalls.add(
                    RollingBall(
                        position = Vector2D(220f, 100f),
                        velocity = Vector2D(-260f, -220f),
                        radius = 42f
                    )
                )
            }
            EventType.SPEED_BOOST -> {
                physics.speedMultiplier = 2.4f
                physics.isIceFriction = true
            }
            EventType.REVERSE_CONTROLS -> {
                physics.areControlsReversed = true
            }
            EventType.ROTATING_ARENA -> {
                arena.isArenaRotating = true
            }
            EventType.INVISIBLE_PLATFORMS -> {
                arena.platforms.forEach { it.isInvisible = true }
            }
            EventType.CHAOS_MODE -> {
                arena.isLavaActive = true
                arena.lavaLevel = 20f
                physics.speedMultiplier = 1.6f
                arena.isArenaRotating = true
            }
            else -> {}
        }
    }

    fun update(
        deltaTimeSec: Float,
        alivePlayers: List<Player>,
        onEliminate: (Player, String) -> Unit
    ) {
        val event = currentEvent ?: return

        eventTimer -= deltaTimeSec

        if (isWarningPhase) {
            if (eventTimer <= 0f) {
                activateEvent()
            }
            return
        }

        if (isEventActive) {
            // Periodic hazards during active event
            when (event) {
                EventType.DONT_MOVE -> {
                    // Check if anyone moved significantly
                    alivePlayers.forEach { player ->
                        val speedSq = player.velocity.x * player.velocity.x + player.velocity.y * player.velocity.y
                        if (speedSq > 2500f && !player.isStunned) {
                            particles.spawnExplosionFx(player.position)
                            particles.showFloatingText("MOVED!", player.position, 0xFFFF0000)
                            onEliminate(player, "Moved during DON'T MOVE! 🛑")
                        }
                    }
                }
                EventType.SAFE_COLOR -> {
                    // Flash non-safe colors
                    if (eventTimer <= 3.0f) {
                        // Color check deadline!
                        alivePlayers.forEach { p ->
                            if (p.safeColorTag != activeSafeColorTag) {
                                particles.spawnExplosionFx(p.position)
                                onEliminate(p, "Stood on Wrong Color! 🎨")
                            }
                        }
                    }
                }
                EventType.RANDOM_EXPLOSIONS, EventType.CHAOS_MODE -> {
                    subHazardSpawnTimer -= deltaTimeSec
                    if (subHazardSpawnTimer <= 0f) {
                        subHazardSpawnTimer = 0.8f
                        val randomTarget = Vector2D(
                            (Math.random() * 400 - 200).toFloat(),
                            (Math.random() * 400 - 200).toFloat()
                        )
                        arena.activeExplosions.add(
                            ExplosionHazard(
                                position = randomTarget,
                                maxRadius = 80f,
                                warningTimer = 1.2f
                            )
                        )
                    }
                }
                EventType.FALLING_OBJECTS -> {
                    subHazardSpawnTimer -= deltaTimeSec
                    if (subHazardSpawnTimer <= 0f) {
                        subHazardSpawnTimer = 0.6f
                        val randomTarget = Vector2D(
                            (Math.random() * 360 - 180).toFloat(),
                            (Math.random() * 360 - 180).toFloat()
                        )
                        arena.fallingMeteors.add(
                            FallingMeteor(
                                targetPosition = randomTarget,
                                altitude = 350f,
                                radius = 34f
                            )
                        )
                    }
                }
                EventType.MOVING_WALLS -> {
                    // Spin sweeper bars at 3x speed
                    arena.sweeperBars.forEach { it.angle += 3.2f * deltaTimeSec }
                }
                else -> {}
            }

            if (eventTimer <= 0f) {
                endEvent()
            }
        }
    }

    fun endEvent() {
        val event = currentEvent
        isWarningPhase = false
        isEventActive = false
        currentEvent = null
        eventTimer = 0f

        // Reset modifiers
        physics.speedMultiplier = 1.0f
        physics.isIceFriction = false
        physics.areControlsReversed = false
        arena.isLavaActive = false
        arena.lavaLevel = -10f
        arena.isWaterActive = false
        arena.waterLevel = -10f
        arena.isArenaRotating = false
        arena.currentShrinkRadius = arena.arenaBoundsRadius
        arena.rollingBalls.clear()
        arena.platforms.forEach {
            it.isInvisible = false
            it.isWarningFall = false
        }
    }
}
