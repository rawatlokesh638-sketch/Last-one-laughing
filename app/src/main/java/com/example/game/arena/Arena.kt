package com.example.game.arena

import androidx.compose.ui.graphics.Color
import com.example.model.Vector2D
import kotlin.math.cos
import kotlin.math.sin

data class ArenaPlatform(
    val id: Int,
    val name: String,
    val center: Vector2D,
    val width: Float,
    val height: Float,
    val elevation: Float = 0f, // 0 = base level, 30 = elevated, 60 = high tower
    val colorTag: Int = 0, // 0 = default, 1 = Red, 2 = Blue, 3 = Green, 4 = Yellow
    val baseColorHex: Long = 0xFF4A5568,
    var isFallen: Boolean = false,
    var isWarningFall: Boolean = false,
    var fallProgress: Float = 0f, // 0 to 1
    var isInvisible: Boolean = false,
    var isMoving: Boolean = false,
    var moveOffset: Vector2D = Vector2D(0f, 0f),
    var moveRange: Float = 60f,
    var moveSpeed: Float = 1.5f,
    var movePhase: Float = 0f
) {
    val currentCenter: Vector2D
        get() = Vector2D(center.x + moveOffset.x, center.y + moveOffset.y)

    fun containsPoint(p: Vector2D, margin: Float = 0f): Boolean {
        if (isFallen) return false
        val c = currentCenter
        val halfW = width / 2f + margin
        val halfH = height / 2f + margin
        return p.x >= c.x - halfW && p.x <= c.x + halfW &&
               p.y >= c.y - halfH && p.y <= c.y + halfH
    }
}

data class Bumper(
    val center: Vector2D,
    val radius: Float = 26f,
    val bounceForce: Float = 24f,
    var pulseTimer: Float = 0f
)

data class SweeperBar(
    val pivot: Vector2D,
    val length: Float = 160f,
    val width: Float = 18f,
    var angle: Float = 0f,
    val rotationSpeed: Float = 1.8f
)

data class RollingBall(
    var position: Vector2D,
    var velocity: Vector2D,
    val radius: Float = 36f,
    var rotationAngle: Float = 0f
)

data class ExplosionHazard(
    val position: Vector2D,
    val maxRadius: Float = 75f,
    var warningTimer: Float = 1.5f,
    var blastTimer: Float = 0f,
    var hasExploded: Boolean = false,
    var isDone: Boolean = false
)

data class FallingMeteor(
    val targetPosition: Vector2D,
    var altitude: Float = 400f,
    val radius: Float = 32f,
    var isImpacted: Boolean = false
)

data class ArenaCollectible(
    val id: Int,
    var position: Vector2D,
    val typeEmoji: String, // 🍬, 🍭, 🎈, ⭐
    val pointValue: Int = 1,
    var isCollected: Boolean = false,
    var respawnTimer: Float = 0f,
    var floatPhase: Float = 0f
)

class ArenaManager {
    val arenaBoundsRadius: Float = 380f
    var currentShrinkRadius: Float = 380f
    val targetShrinkRadius: Float = 380f
    
    var lavaLevel: Float = -10f // When > 0, base ground is covered in lava
    var isLavaActive: Boolean = false
    
    var waterLevel: Float = -10f // Rises during Rising Water event
    var isWaterActive: Boolean = false
    
    var arenaRotationAngle: Float = 0f
    var isArenaRotating: Boolean = false
    
    var guardRailsActive: Boolean = false // Kids Mode Edge Protection
    var isColorDanceActive: Boolean = false
    var activeDanceColorTag: Int = 0
    var danceWarningTimer: Float = 0f
    var danceIntervalTimer: Float = 5f
    
    val platforms = mutableListOf<ArenaPlatform>()
    val bumpers = mutableListOf<Bumper>()
    val sweeperBars = mutableListOf<SweeperBar>()
    val rollingBalls = mutableListOf<RollingBall>()
    val activeExplosions = mutableListOf<ExplosionHazard>()
    val fallingMeteors = mutableListOf<FallingMeteor>()
    val collectibles = mutableListOf<ArenaCollectible>()
    
    init {
        resetArena()
    }

    fun resetArena() {
        platforms.clear()
        bumpers.clear()
        sweeperBars.clear()
        rollingBalls.clear()
        activeExplosions.clear()
        fallingMeteors.clear()
        
        currentShrinkRadius = arenaBoundsRadius
        lavaLevel = -10f
        isLavaActive = false
        waterLevel = -10f
        isWaterActive = false
        arenaRotationAngle = 0f
        isArenaRotating = false

        // 1. Central Core Platform (Base)
        platforms.add(
            ArenaPlatform(
                id = 0,
                name = "Central Ring",
                center = Vector2D(0f, 0f),
                width = 240f,
                height = 240f,
                elevation = 0f,
                colorTag = 0,
                baseColorHex = 0xFF2D3748
            )
        )

        // 2. High Tower Pillars in Corners (Elevation = 45 for Lava / Water safe zones)
        // Red Top-Left
        platforms.add(
            ArenaPlatform(
                id = 1,
                name = "Red High Tower",
                center = Vector2D(-200f, -200f),
                width = 130f,
                height = 130f,
                elevation = 45f,
                colorTag = 1, // RED
                baseColorHex = 0xFFE53E3E
            )
        )
        // Blue Top-Right
        platforms.add(
            ArenaPlatform(
                id = 2,
                name = "Blue High Tower",
                center = Vector2D(200f, -200f),
                width = 130f,
                height = 130f,
                elevation = 45f,
                colorTag = 2, // BLUE
                baseColorHex = 0xFF3182CE
            )
        )
        // Green Bottom-Left
        platforms.add(
            ArenaPlatform(
                id = 3,
                name = "Green High Tower",
                center = Vector2D(-200f, 200f),
                width = 130f,
                height = 130f,
                elevation = 45f,
                colorTag = 3, // GREEN
                baseColorHex = 0xFF38A169
            )
        )
        // Yellow Bottom-Right
        platforms.add(
            ArenaPlatform(
                id = 4,
                name = "Yellow High Tower",
                center = Vector2D(200f, 200f),
                width = 130f,
                height = 130f,
                elevation = 45f,
                colorTag = 4, // YELLOW
                baseColorHex = 0xFFD69E2E
            )
        )

        // 3. Connecting Bridges (North, South, East, West)
        platforms.add(
            ArenaPlatform(
                id = 5,
                name = "North Bridge",
                center = Vector2D(0f, -210f),
                width = 90f,
                height = 110f,
                elevation = 15f,
                colorTag = 0,
                baseColorHex = 0xFF4A5568
            )
        )
        platforms.add(
            ArenaPlatform(
                id = 6,
                name = "South Bridge",
                center = Vector2D(0f, 210f),
                width = 90f,
                height = 110f,
                elevation = 15f,
                colorTag = 0,
                baseColorHex = 0xFF4A5568
            )
        )
        platforms.add(
            ArenaPlatform(
                id = 7,
                name = "West Bridge",
                center = Vector2D(-210f, 0f),
                width = 110f,
                height = 90f,
                elevation = 15f,
                colorTag = 0,
                baseColorHex = 0xFF4A5568
            )
        )
        platforms.add(
            ArenaPlatform(
                id = 8,
                name = "East Bridge",
                center = Vector2D(210f, 0f),
                width = 110f,
                height = 90f,
                elevation = 15f,
                colorTag = 0,
                baseColorHex = 0xFF4A5568
            )
        )

        // 4. Moving Floating Platforms
        platforms.add(
            ArenaPlatform(
                id = 9,
                name = "Floating Left",
                center = Vector2D(-290f, -60f),
                width = 75f,
                height = 75f,
                elevation = 25f,
                isMoving = true,
                moveRange = 50f,
                moveSpeed = 1.8f,
                baseColorHex = 0xFF805AD5
            )
        )
        platforms.add(
            ArenaPlatform(
                id = 10,
                name = "Floating Right",
                center = Vector2D(290f, 60f),
                width = 75f,
                height = 75f,
                elevation = 25f,
                isMoving = true,
                moveRange = 50f,
                moveSpeed = 1.8f,
                baseColorHex = 0xFF805AD5
            )
        )

        // 5. Center Breakable/Fallable Tiles (for Falling Platforms)
        var tileId = 11
        for (gx in -1..1) {
            for (gy in -1..1) {
                if (gx == 0 && gy == 0) continue // Keep center hole/bumper
                platforms.add(
                    ArenaPlatform(
                        id = tileId++,
                        name = "Tile $gx,$gy",
                        center = Vector2D(gx * 70f, gy * 70f),
                        width = 62f,
                        height = 62f,
                        elevation = 5f,
                        baseColorHex = 0xFF718096
                    )
                )
            }
        }

        // Bumper Pads
        bumpers.add(Bumper(center = Vector2D(0f, 0f), radius = 24f, bounceForce = 26f))
        bumpers.add(Bumper(center = Vector2D(-120f, -120f), radius = 18f, bounceForce = 20f))
        bumpers.add(Bumper(center = Vector2D(120f, 120f), radius = 18f, bounceForce = 20f))
        bumpers.add(Bumper(center = Vector2D(120f, -120f), radius = 18f, bounceForce = 20f))
        bumpers.add(Bumper(center = Vector2D(-120f, 120f), radius = 18f, bounceForce = 20f))

        // Sweeper Bars
        sweeperBars.add(SweeperBar(pivot = Vector2D(0f, 0f), length = 150f, rotationSpeed = 1.6f))
    }

    fun setupForMode(mode: com.example.model.MatchMode, kidsAssist: Boolean) {
        resetArena()
        guardRailsActive = kidsAssist || mode.isKidsFriendly
        collectibles.clear()

        when (mode) {
            com.example.model.MatchMode.CANDY_RUSH -> {
                // Clear harsh sweepers
                sweeperBars.clear()
                guardRailsActive = true
                
                // Spawn delicious candies, balloons, and stars across platforms
                val candyTypes = listOf("🍬", "🍭", "🎈", "⭐", "🧁", "🍩", "🍓", "🍰")
                var cId = 0
                val spawnPositions = listOf(
                    Vector2D(0f, 0f),
                    Vector2D(-70f, 0f), Vector2D(70f, 0f),
                    Vector2D(0f, -70f), Vector2D(0f, 70f),
                    Vector2D(-180f, -180f), Vector2D(180f, -180f),
                    Vector2D(-180f, 180f), Vector2D(180f, 180f),
                    Vector2D(0f, -200f), Vector2D(0f, 200f),
                    Vector2D(-200f, 0f), Vector2D(200f, 0f),
                    Vector2D(-280f, -60f), Vector2D(280f, 60f)
                )
                spawnPositions.forEach { pos ->
                    collectibles.add(
                        ArenaCollectible(
                            id = cId++,
                            position = pos,
                            typeEmoji = candyTypes.random(),
                            pointValue = if (pos.x == 0f && pos.y == 0f) 3 else 1,
                            floatPhase = (cId * 0.8f)
                        )
                    )
                }
            }

            com.example.model.MatchMode.COLOR_DANCE -> {
                isColorDanceActive = true
                activeDanceColorTag = (1..4).random()
                danceWarningTimer = 3.5f
                sweeperBars.clear()
                
                // Setup 4x4 Color Dance Floor
                platforms.clear()
                var dId = 1
                val colors = listOf(1, 2, 3, 4) // Red, Blue, Green, Yellow
                for (gx in -2..2) {
                    for (gy in -2..2) {
                        val cTag = colors[((gx + 2) + (gy + 2) * 2) % colors.size]
                        val hex = when (cTag) {
                            1 -> 0xFFEF4444 // Red
                            2 -> 0xFF3B82F6 // Blue
                            3 -> 0xFF10B981 // Green
                            else -> 0xFFF59E0B // Yellow
                        }
                        platforms.add(
                            ArenaPlatform(
                                id = dId++,
                                name = "ColorTile $gx,$gy",
                                center = Vector2D(gx * 85f, gy * 85f),
                                width = 76f,
                                height = 76f,
                                elevation = 5f,
                                colorTag = cTag,
                                baseColorHex = hex
                            )
                        )
                    }
                }
            }

            com.example.model.MatchMode.CROWN_CHASE -> {
                // Add center high pedestal and extra bouncers
                bumpers.add(Bumper(center = Vector2D(-60f, 0f), radius = 20f, bounceForce = 22f))
                bumpers.add(Bumper(center = Vector2D(60f, 0f), radius = 20f, bounceForce = 22f))
            }

            com.example.model.MatchMode.BOMB_PARTY -> {
                // High energy bumpers
                bumpers.add(Bumper(center = Vector2D(-80f, -80f), radius = 24f, bounceForce = 28f))
                bumpers.add(Bumper(center = Vector2D(80f, 80f), radius = 24f, bounceForce = 28f))
            }

            com.example.model.MatchMode.LAVA_RUN -> {
                // Quick start lava & crumbling tiles
                isLavaActive = true
                lavaLevel = -5f
            }

            else -> {
                // Standard arena
            }
        }
    }

    fun update(deltaTimeSec: Float) {
        // Update Moving platforms
        platforms.forEach { p ->
            if (p.isMoving) {
                p.movePhase += p.moveSpeed * deltaTimeSec
                p.moveOffset.y = sin(p.movePhase) * p.moveRange
            }
            if (p.isWarningFall) {
                p.fallProgress += deltaTimeSec * 0.8f
                if (p.fallProgress >= 1f) {
                    p.isFallen = true
                }
            }
        }

        // Update Sweeper Bars
        sweeperBars.forEach { bar ->
            bar.angle += bar.rotationSpeed * deltaTimeSec
            if (bar.angle > Math.PI * 2) bar.angle -= (Math.PI * 2).toFloat()
        }

        // Update Rolling balls
        val ballIter = rollingBalls.iterator()
        while (ballIter.hasNext()) {
            val ball = ballIter.next()
            ball.position.x += ball.velocity.x * deltaTimeSec
            ball.position.y += ball.velocity.y * deltaTimeSec
            ball.rotationAngle += 5f * deltaTimeSec
            
            // Bounce off arena perimeter
            val dist = ball.position.distanceTo(Vector2D(0f, 0f))
            if (dist > currentShrinkRadius - ball.radius) {
                val normal = ball.position.normalize()
                val dot = ball.velocity.x * normal.x + ball.velocity.y * normal.y
                ball.velocity.x -= 2 * dot * normal.x
                ball.velocity.y -= 2 * dot * normal.y
            }
        }

        // Update active explosions
        val expIter = activeExplosions.iterator()
        while (expIter.hasNext()) {
            val exp = expIter.next()
            if (!exp.hasExploded) {
                exp.warningTimer -= deltaTimeSec
                if (exp.warningTimer <= 0f) {
                    exp.hasExploded = true
                    exp.blastTimer = 0.4f
                }
            } else {
                exp.blastTimer -= deltaTimeSec
                if (exp.blastTimer <= 0f) {
                    exp.isDone = true
                    expIter.remove()
                }
            }
        }

        // Update falling meteors
        val meteorIter = fallingMeteors.iterator()
        while (meteorIter.hasNext()) {
            val meteor = meteorIter.next()
            meteor.altitude -= 500f * deltaTimeSec
            if (meteor.altitude <= 0f) {
                meteor.isImpacted = true
                activeExplosions.add(
                    ExplosionHazard(
                        position = meteor.targetPosition,
                        maxRadius = 85f,
                        warningTimer = 0f,
                        hasExploded = true,
                        blastTimer = 0.45f
                    )
                )
                meteorIter.remove()
            }
        }

        // Arena rotation if active
        if (isArenaRotating) {
            arenaRotationAngle += 0.8f * deltaTimeSec
        }

        // Update Collectibles (floating phase & respawn)
        collectibles.forEach { col ->
            col.floatPhase += 3.5f * deltaTimeSec
            if (col.isCollected) {
                col.respawnTimer -= deltaTimeSec
                if (col.respawnTimer <= 0f) {
                    col.isCollected = false
                }
            }
        }

        // Update Color Dance sequence if active
        if (isColorDanceActive) {
            danceWarningTimer -= deltaTimeSec
            if (danceWarningTimer <= 0f) {
                // Drop non-safe tiles for 2.2 seconds!
                platforms.forEach { tile ->
                    tile.isFallen = (tile.colorTag != activeDanceColorTag)
                }
                
                danceIntervalTimer -= deltaTimeSec
                if (danceIntervalTimer <= 0f) {
                    // Restore all tiles and pick new color
                    platforms.forEach { it.isFallen = false }
                    activeDanceColorTag = (1..4).random()
                    danceWarningTimer = 3.2f
                    danceIntervalTimer = 5.2f
                }
            } else {
                // Warning phase: blink non-safe tiles
                platforms.forEach { it.isFallen = false }
            }
        }
    }

    fun getPlatformUnder(position: Vector2D, heightZ: Float): ArenaPlatform? {
        // Return highest platform that contains point and whose elevation is <= heightZ + margin
        return platforms
            .filter { !it.isFallen && it.containsPoint(position, margin = 4f) }
            .maxByOrNull { it.elevation }
    }
}
