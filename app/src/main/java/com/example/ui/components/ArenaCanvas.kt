package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.unit.dp
import com.example.game.arena.ArenaPlatform
import com.example.game.engine.MatchEngine
import com.example.model.Player
import com.example.model.Vector2D
import kotlin.math.*

@Composable
fun ArenaCanvas(
    engine: MatchEngine,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        // Apply Screen Shake
        val shakeOffset = if (engine.particles.screenShakeAmount > 0f) {
            Offset(
                (Math.random() * engine.particles.screenShakeAmount * 2 - engine.particles.screenShakeAmount).toFloat(),
                (Math.random() * engine.particles.screenShakeAmount * 2 - engine.particles.screenShakeAmount).toFloat()
            )
        } else Offset.Zero

        val cameraCenter = Offset(centerX + shakeOffset.x, centerY + shakeOffset.y)

        // 1. Draw Void / Abyss Background
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF1E1B4B), Color(0xFF0F172A), Color(0xFF020617)),
                center = cameraCenter,
                radius = size.width.coerceAtLeast(size.height)
            )
        )

        // Draw Stylized Cyber Grid Lines
        val gridSpacing = 48f
        val numLinesX = (size.width / gridSpacing).toInt() + 2
        val numLinesY = (size.height / gridSpacing).toInt() + 2
        for (i in 0..numLinesX) {
            val x = i * gridSpacing
            drawLine(
                color = Color(0x156366F1),
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1.5f
            )
        }
        for (j in 0..numLinesY) {
            val y = j * gridSpacing
            drawLine(
                color = Color(0x156366F1),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.5f
            )
        }

        // 2. Draw Lava / Water Floor if active
        if (engine.arena.isLavaActive) {
            val lavaPulse = (sin(engine.matchTimerSec * 4f) * 0.15f + 0.85f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFF3D00).copy(alpha = 0.85f * lavaPulse),
                        Color(0xFFFF9100).copy(alpha = 0.7f),
                        Color(0xFFDD2C00).copy(alpha = 0.9f)
                    ),
                    center = cameraCenter,
                    radius = engine.arena.arenaBoundsRadius + 60f
                ),
                center = cameraCenter,
                radius = engine.arena.arenaBoundsRadius + 60f
            )
        } else if (engine.arena.isWaterActive) {
            val waterPulse = (sin(engine.matchTimerSec * 3f) * 0.15f + 0.85f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF00E5FF).copy(alpha = 0.75f * waterPulse),
                        Color(0xFF2979FF).copy(alpha = 0.8f),
                        Color(0xFF1565C0).copy(alpha = 0.9f)
                    ),
                    center = cameraCenter,
                    radius = engine.arena.arenaBoundsRadius + 50f
                ),
                center = cameraCenter,
                radius = engine.arena.arenaBoundsRadius + 50f
            )
        }

        // 3. Draw Laser Arena Shrink Boundary
        val shrinkRadius = engine.arena.currentShrinkRadius
        drawCircle(
            color = Color(0xFFFF0055).copy(alpha = 0.3f),
            center = cameraCenter,
            radius = shrinkRadius,
            style = Stroke(
                width = 8f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 15f), engine.matchTimerSec * 50f)
            )
        )
        drawCircle(
            color = Color(0xFFFF4081).copy(alpha = 0.8f),
            center = cameraCenter,
            radius = shrinkRadius,
            style = Stroke(width = 3f)
        )

        // 3.5. Draw Protective Rainbow Edge Guard Rails (Kids Assist Mode)
        if (engine.arena.guardRailsActive) {
            val railRadius = engine.arena.arenaBoundsRadius - 15f
            val pulse = (sin(engine.matchTimerSec * 4f) * 0.2f + 0.8f)
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFFFF4081),
                        Color(0xFFFFEA00),
                        Color(0xFF00E676),
                        Color(0xFF00E5FF),
                        Color(0xFF7C4DFF),
                        Color(0xFFFF4081)
                    ),
                    center = cameraCenter
                ),
                center = cameraCenter,
                radius = railRadius,
                style = Stroke(width = 6f * pulse)
            )
            // Decorative posts along edge
            for (pIndex in 0 until 16) {
                val pAngle = (pIndex.toFloat() / 16f) * (Math.PI * 2).toFloat()
                val postX = cameraCenter.x + cos(pAngle) * railRadius
                val postY = cameraCenter.y + sin(pAngle) * railRadius
                drawCircle(
                    color = Color(0xFFFFEA00),
                    center = Offset(postX, postY),
                    radius = 5f
                )
            }
        }

        // 4. Draw Platforms (Sorted by Elevation)
        val sortedPlatforms = engine.arena.platforms.sortedBy { it.elevation }
        sortedPlatforms.forEach { platform ->
            if (!platform.isFallen && !platform.isInvisible) {
                drawPlatform(platform, cameraCenter)
            }
        }

        // 4.5. Draw Floating Collectibles (Candy Rush & Balloons)
        engine.arena.collectibles.forEach { col ->
            if (!col.isCollected) {
                val floatY = sin(col.floatPhase) * 6f
                val colPos = cameraCenter + Offset(col.position.x, col.position.y + floatY)
                
                // Drop shadow
                drawOval(
                    color = Color.Black.copy(alpha = 0.3f),
                    topLeft = colPos + Offset(-12f, 10f - floatY * 0.5f),
                    size = Size(24f, 10f)
                )

                // Sparkling Glow Aura
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFD54F).copy(alpha = 0.6f), Color.Transparent),
                        center = colPos,
                        radius = 22f
                    ),
                    center = colPos,
                    radius = 22f
                )

                // Candy Body
                drawCircle(
                    color = Color(0xFFFF4081),
                    center = colPos,
                    radius = 12f
                )
                drawCircle(
                    color = Color(0xFFFFD54F),
                    center = colPos - Offset(3f, 3f),
                    radius = 5f
                )
                // Center Star/Accent
                drawCircle(
                    color = Color.White,
                    center = colPos,
                    radius = 3f
                )
            }
        }

        // 5. Draw Bumpers
        engine.arena.bumpers.forEach { bumper ->
            val bPos = cameraCenter + Offset(bumper.center.x, bumper.center.y)
            drawCircle(
                color = Color(0xFF000000).copy(alpha = 0.35f),
                center = bPos + Offset(0f, 6f),
                radius = bumper.radius
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFEA00), Color(0xFFFF6D00)),
                    center = bPos - Offset(bumper.radius * 0.3f, bumper.radius * 0.3f),
                    radius = bumper.radius
                ),
                center = bPos,
                radius = bumper.radius
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.8f),
                center = bPos - Offset(bumper.radius * 0.3f, bumper.radius * 0.3f),
                radius = bumper.radius * 0.35f
            )
        }

        // 6. Draw Sweeper Bars
        engine.arena.sweeperBars.forEach { bar ->
            val pivot = cameraCenter + Offset(bar.pivot.x, bar.pivot.y)
            val end = pivot + Offset(cos(bar.angle) * bar.length, sin(bar.angle) * bar.length)
            
            // Shadow
            drawLine(
                color = Color.Black.copy(alpha = 0.3f),
                start = pivot + Offset(0f, 8f),
                end = end + Offset(0f, 8f),
                strokeWidth = bar.width,
                cap = StrokeCap.Round
            )
            // Bar Body with Hazard Stripes
            drawLine(
                color = Color(0xFFFFD600),
                start = pivot,
                end = end,
                strokeWidth = bar.width,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color(0xFFD50000),
                start = pivot,
                end = end,
                strokeWidth = bar.width * 0.5f,
                cap = StrokeCap.Round
            )
            drawCircle(color = Color(0xFF37474F), center = pivot, radius = bar.width * 0.9f)
        }

        // 7. Draw Active Explosions & Warning Reticles
        engine.arena.activeExplosions.forEach { exp ->
            val ePos = cameraCenter + Offset(exp.position.x, exp.position.y)
            if (!exp.hasExploded) {
                // Pulsing Warning Reticle
                val pulse = (sin(engine.matchTimerSec * 15f) * 0.3f + 0.7f)
                drawCircle(
                    color = Color(0xFFFF1744).copy(alpha = 0.35f * pulse),
                    center = ePos,
                    radius = exp.maxRadius
                )
                drawCircle(
                    color = Color(0xFFFF1744),
                    center = ePos,
                    radius = exp.maxRadius,
                    style = Stroke(width = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f))
                )
                // Crosshairs
                drawLine(Color.Red, ePos - Offset(15f, 0f), ePos + Offset(15f, 0f), strokeWidth = 3f)
                drawLine(Color.Red, ePos - Offset(0f, 15f), ePos + Offset(0f, 15f), strokeWidth = 3f)
            } else {
                // Expanding Blast Ring
                val blastProgress = (1f - (exp.blastTimer / 0.4f)).coerceIn(0f, 1f)
                val blastRadius = exp.maxRadius * blastProgress
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, Color(0xFFFFEB3B), Color(0xFFFF3D00), Color.Transparent),
                        center = ePos,
                        radius = blastRadius
                    ),
                    center = ePos,
                    radius = blastRadius
                )
            }
        }

        // 8. Draw Falling Meteors
        engine.arena.fallingMeteors.forEach { meteor ->
            val targetPos = cameraCenter + Offset(meteor.targetPosition.x, meteor.targetPosition.y)
            val currentPos = targetPos - Offset(0f, meteor.altitude)
            
            // Ground shadow indicator
            drawCircle(
                color = Color.Black.copy(alpha = (1f - meteor.altitude / 400f).coerceIn(0.2f, 0.7f)),
                center = targetPos,
                radius = meteor.radius * (1f - meteor.altitude / 600f)
            )

            // Fiery Meteor Body
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFEB3B), Color(0xFFFF5722), Color(0xFFB71C1C)),
                    center = currentPos - Offset(meteor.radius * 0.2f, meteor.radius * 0.2f),
                    radius = meteor.radius
                ),
                center = currentPos,
                radius = meteor.radius
            )
        }

        // 9. Draw Giant Rolling Balls
        engine.arena.rollingBalls.forEach { ball ->
            val bPos = cameraCenter + Offset(ball.position.x, ball.position.y)
            drawCircle(
                color = Color.Black.copy(alpha = 0.4f),
                center = bPos + Offset(0f, 8f),
                radius = ball.radius
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFF8A80), Color(0xFFD50000), Color(0xFF880E4F)),
                    center = bPos - Offset(ball.radius * 0.3f, ball.radius * 0.3f),
                    radius = ball.radius
                ),
                center = bPos,
                radius = ball.radius
            )
            // Spike / Pattern on ball
            val spokeAngle = ball.rotationAngle
            val spokeX = cos(spokeAngle) * ball.radius * 0.6f
            val spokeY = sin(spokeAngle) * ball.radius * 0.6f
            drawLine(
                color = Color.Yellow,
                start = bPos - Offset(spokeX, spokeY),
                end = bPos + Offset(spokeX, spokeY),
                strokeWidth = 5f,
                cap = StrokeCap.Round
            )
        }

        // 10. Draw Players
        val alivePlayers = engine.players.filter { it.isAlive }.sortedBy { it.position.y + it.heightZ }
        alivePlayers.forEach { player ->
            drawPlayerCharacter(player, cameraCenter, engine.matchTimerSec)
        }

        // 11. Draw Particles
        engine.particles.particles.forEach { p ->
            val pPos = cameraCenter + Offset(p.position.x, p.position.y)
            val pColor = Color(p.colorHex).copy(alpha = p.alpha)
            when (p.shapeType) {
                1 -> {
                    // Confetti / Square
                    rotate(p.rotation, pPos) {
                        drawRect(
                            color = pColor,
                            topLeft = pPos - Offset(p.size / 2f, p.size / 2f),
                            size = Size(p.size, p.size * 0.7f)
                        )
                    }
                }
                3 -> {
                    // Ring / Shockwave
                    drawCircle(
                        color = pColor,
                        center = pPos,
                        radius = p.size * (1f - p.life / p.maxLife),
                        style = Stroke(width = 4f)
                    )
                }
                else -> {
                    // Circle / Smoke
                    drawCircle(
                        color = pColor,
                        center = pPos,
                        radius = p.size / 2f
                    )
                }
            }
        }

        // 12. Draw Floating Texts (e.g. "BONK!", "ELIMINATED!")
        engine.particles.floatingTexts.forEach { textItem ->
            val tPos = cameraCenter + Offset(textItem.position.x, textItem.position.y)
            val textColor = Color(textItem.colorHex).copy(alpha = textItem.alpha)
            
            // Draw stylized comic background pill
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.75f * textItem.alpha),
                topLeft = tPos - Offset(50f, 18f),
                size = Size(100f, 36f),
                cornerRadius = CornerRadius(18f, 18f)
            )
            drawRoundRect(
                color = textColor,
                topLeft = tPos - Offset(48f, 16f),
                size = Size(96f, 32f),
                cornerRadius = CornerRadius(16f, 16f),
                style = Stroke(width = 2.5f)
            )
        }
    }
}

private fun DrawScope.drawPlatform(platform: ArenaPlatform, cameraCenter: Offset) {
    val center = cameraCenter + Offset(platform.currentCenter.x, platform.currentCenter.y)
    val width = platform.width
    val height = platform.height
    val elevation = platform.elevation

    val baseColor = Color(platform.baseColorHex)
    val highlightColor = when (platform.colorTag) {
        1 -> Color(0xFFFF5252) // RED
        2 -> Color(0xFF448AFF) // BLUE
        3 -> Color(0xFF69F0AE) // GREEN
        4 -> Color(0xFFFFD740) // YELLOW
        else -> baseColor
    }

    // Platform Shadow
    drawRoundRect(
        color = Color.Black.copy(alpha = 0.35f),
        topLeft = center - Offset(width / 2f, height / 2f) + Offset(0f, 12f + elevation * 0.4f),
        size = Size(width, height),
        cornerRadius = CornerRadius(16f, 16f)
    )

    // Platform 3D Extrusion Side
    val sideHeight = 14f + elevation * 0.2f
    drawRoundRect(
        color = highlightColor.copy(alpha = 0.6f),
        topLeft = center - Offset(width / 2f, height / 2f - sideHeight * 0.4f),
        size = Size(width, height + sideHeight),
        cornerRadius = CornerRadius(16f, 16f)
    )

    // Platform Top Face
    val topOffset = Offset(0f, -elevation * 0.5f)
    val topColor = if (platform.isWarningFall) {
        Color(0xFFFF1744) // Blinking Red
    } else {
        highlightColor
    }

    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(topColor, topColor.copy(alpha = 0.85f)),
            startY = center.y - height / 2f + topOffset.y,
            endY = center.y + height / 2f + topOffset.y
        ),
        topLeft = center - Offset(width / 2f, height / 2f) + topOffset,
        size = Size(width, height),
        cornerRadius = CornerRadius(16f, 16f)
    )

    // Highlight Edge Border
    drawRoundRect(
        color = Color.White.copy(alpha = 0.4f),
        topLeft = center - Offset(width / 2f, height / 2f) + topOffset,
        size = Size(width, height),
        cornerRadius = CornerRadius(16f, 16f),
        style = Stroke(width = 3f)
    )
}

private fun DrawScope.drawPlayerCharacter(player: Player, cameraCenter: Offset, timerSec: Float) {
    val groundPos = cameraCenter + Offset(player.position.x, player.position.y)
    val zOffset = player.heightZ * 1.5f
    val charPos = groundPos - Offset(0f, zOffset)
    val radius = 18f

    // 1. Drop Shadow on Ground
    val shadowScale = (1f - (player.heightZ / 120f)).coerceIn(0.3f, 1.2f)
    drawOval(
        color = Color.Black.copy(alpha = 0.35f * shadowScale),
        topLeft = groundPos - Offset(radius * shadowScale, radius * 0.5f * shadowScale),
        size = Size(radius * 2f * shadowScale, radius * shadowScale)
    )

    // 2. Dash / Stun visual effects
    if (player.isDashing) {
        drawCircle(
            color = Color.White.copy(alpha = 0.6f),
            center = charPos,
            radius = radius * 1.35f,
            style = Stroke(width = 4f)
        )
    }

    // 3. Main Character Body (Squash & Stretch)
    val wobble = sin(player.wobblePhase) * 0.12f
    val scaleX = 1f + wobble
    val scaleY = 1f - wobble
    val bodyColor = Color(player.baseColorHex)

    // Distinct Outline for Local Player
    if (player.isLocalPlayer) {
        drawCircle(
            color = Color(0xFFFFD700), // Golden Halo
            center = charPos,
            radius = radius * 1.25f,
            style = Stroke(width = 4f)
        )
    }

    // Body Sphere with 3D Gloss
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.6f),
                bodyColor,
                bodyColor.copy(alpha = 0.85f)
            ),
            center = charPos - Offset(radius * 0.35f, radius * 0.35f),
            radius = radius * 1.1f
        ),
        center = charPos,
        radius = radius
    )

    // 4. Expressive Cartoon Googly Eyes
    val eyeFacingX = cos(player.facingAngle) * 6f
    val eyeFacingY = sin(player.facingAngle) * 4f
    val eyeOffsetX = 5f
    val eyeOffsetY = -4f

    // Left Eye
    drawCircle(
        color = Color.White,
        center = charPos + Offset(-eyeOffsetX + eyeFacingX * 0.6f, eyeOffsetY + eyeFacingY * 0.6f),
        radius = 5.5f
    )
    drawCircle(
        color = Color.Black,
        center = charPos + Offset(-eyeOffsetX + eyeFacingX, eyeOffsetY + eyeFacingY),
        radius = 2.8f
    )

    // Right Eye
    drawCircle(
        color = Color.White,
        center = charPos + Offset(eyeOffsetX + eyeFacingX * 0.6f, eyeOffsetY + eyeFacingY * 0.6f),
        radius = 5.5f
    )
    drawCircle(
        color = Color.Black,
        center = charPos + Offset(eyeOffsetX + eyeFacingX, eyeOffsetY + eyeFacingY),
        radius = 2.8f
    )

    // Mouth (Big Smile or Surprised 'O' if stunned)
    if (player.isStunned) {
        drawCircle(
            color = Color.Black,
            center = charPos + Offset(eyeFacingX * 0.5f, 4f + eyeFacingY * 0.5f),
            radius = 3.5f
        )
    } else {
        drawArc(
            color = Color.Black,
            startAngle = 10f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = charPos + Offset(-5f + eyeFacingX * 0.5f, 1f + eyeFacingY * 0.5f),
            size = Size(10f, 8f),
            style = Stroke(width = 2.5f, cap = StrokeCap.Round)
        )
    }

    // 5. Crown if leader / Crown Chase Mode
    if (player.hasCrown) {
        val crownY = charPos.y - radius - 14f + sin(timerSec * 6f) * 3f
        val crownX = charPos.x
        // Golden Ray Aura
        drawCircle(
            color = Color(0xFFFFD700).copy(alpha = 0.4f),
            center = Offset(crownX, crownY),
            radius = 16f
        )
        // Crown Base
        drawCircle(
            color = Color(0xFFFFD700),
            center = Offset(crownX, crownY),
            radius = 9f
        )
        drawCircle(
            color = Color(0xFFFFEA00),
            center = Offset(crownX, crownY),
            radius = 5f
        )
        // Crown Peaks
        drawCircle(color = Color(0xFFFF1744), center = Offset(crownX - 6f, crownY - 6f), radius = 3f)
        drawCircle(color = Color(0xFF2979FF), center = Offset(crownX, crownY - 8f), radius = 3.5f)
        drawCircle(color = Color(0xFFFF1744), center = Offset(crownX + 6f, crownY - 6f), radius = 3f)
    }

    // 5.5. Bomb if Hot Potato Carrier
    if (player.hasBomb) {
        val bombPulse = (sin(timerSec * 14f) * 0.25f + 1f)
        val bombY = charPos.y - radius - 18f
        val bombX = charPos.x
        
        // Red Threat Warning Ring
        drawCircle(
            color = Color(0xFFFF1744).copy(alpha = 0.5f * bombPulse),
            center = charPos,
            radius = radius * 1.8f,
            style = Stroke(width = 3f)
        )

        // Bomb Body
        drawCircle(
            color = Color(0xFF212121),
            center = Offset(bombX, bombY),
            radius = 12f * bombPulse
        )
        // Bomb Highlights
        drawCircle(
            color = Color(0xFF757575),
            center = Offset(bombX - 3f, bombY - 3f),
            radius = 4f
        )
        // Fuse Spark
        drawCircle(
            color = Color(0xFFFF9100),
            center = Offset(bombX + 6f, bombY - 12f),
            radius = 4f * bombPulse
        )
        drawCircle(
            color = Color(0xFFFFEB3B),
            center = Offset(bombX + 6f, bombY - 12f),
            radius = 2f
        )
    }

    // 5.8. Candy Score Badge (Candy Rush Mode)
    if (player.candyScore > 0) {
        val badgePos = charPos + Offset(radius + 4f, -radius * 0.5f)
        drawCircle(
            color = Color(0xFFFF4081),
            center = badgePos,
            radius = 8f
        )
        drawCircle(
            color = Color.White,
            center = badgePos,
            radius = 8f,
            style = Stroke(width = 1.5f)
        )
    }

    // 6. Active Emote Bubble
    if (player.activeEmote != null) {
        val bubblePos = charPos - Offset(0f, radius + 22f)
        drawCircle(
            color = Color.White,
            center = bubblePos,
            radius = 14f
        )
        drawCircle(
            color = Color(0xFF6366F1),
            center = bubblePos,
            radius = 14f,
            style = Stroke(width = 2f)
        )
    }
}
