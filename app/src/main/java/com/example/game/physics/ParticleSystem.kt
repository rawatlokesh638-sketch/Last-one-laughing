package com.example.game.physics

import androidx.compose.ui.graphics.Color
import com.example.model.Vector2D
import kotlin.math.cos
import kotlin.math.sin

data class Particle(
    var position: Vector2D,
    var velocity: Vector2D,
    var colorHex: Long,
    var size: Float,
    var alpha: Float = 1f,
    var life: Float = 1f,
    val maxLife: Float = 1f,
    var rotation: Float = 0f,
    var rotationSpeed: Float = 0f,
    var shapeType: Int = 0 // 0 = Circle, 1 = Square/Confetti, 2 = Star, 3 = Ring
)

data class FloatingText(
    val id: Long,
    var text: String,
    var position: Vector2D,
    var velocityY: Float = -40f,
    var colorHex: Long = 0xFFFFEB3B,
    var alpha: Float = 1f,
    var scale: Float = 1.2f,
    var life: Float = 1.2f,
    val maxLife: Float = 1.2f
)

class ParticleManager {
    val particles = mutableListOf<Particle>()
    val floatingTexts = mutableListOf<FloatingText>()
    private var textIdCounter = 0L

    var screenShakeAmount: Float = 0f
    var screenShakeDuration: Float = 0f

    fun triggerScreenShake(intensity: Float = 15f, duration: Float = 0.3f) {
        screenShakeAmount = intensity
        screenShakeDuration = duration
    }

    fun update(deltaTimeSec: Float) {
        // Update screen shake
        if (screenShakeDuration > 0f) {
            screenShakeDuration -= deltaTimeSec
            if (screenShakeDuration <= 0f) {
                screenShakeAmount = 0f
            }
        }

        // Update particles
        val pIter = particles.iterator()
        while (pIter.hasNext()) {
            val p = pIter.next()
            p.life -= deltaTimeSec
            if (p.life <= 0f) {
                pIter.remove()
                continue
            }
            p.position.x += p.velocity.x * deltaTimeSec
            p.position.y += p.velocity.y * deltaTimeSec
            p.velocity.x *= 0.95f
            p.velocity.y *= 0.95f
            p.rotation += p.rotationSpeed * deltaTimeSec
            p.alpha = (p.life / p.maxLife).coerceIn(0f, 1f)
        }

        // Update floating texts
        val tIter = floatingTexts.iterator()
        while (tIter.hasNext()) {
            val t = tIter.next()
            t.life -= deltaTimeSec
            if (t.life <= 0f) {
                tIter.remove()
                continue
            }
            t.position.y += t.velocityY * deltaTimeSec
            t.velocityY *= 0.96f
            t.alpha = (t.life / t.maxLife).coerceIn(0f, 1f)
            t.scale = 1f + 0.3f * (t.life / t.maxLife)
        }
    }

    fun spawnConfettiShower(center: Vector2D, count: Int = 50) {
        val colors = listOf(
            0xFFFF1744, 0xFFFF9100, 0xFFFFEA00, 0xFF00E676,
            0xFF00E5FF, 0xFF2979FF, 0xFFD500F9, 0xFFFF4081
        )
        for (i in 0 until count) {
            val angle = (Math.random() * Math.PI * 2).toFloat()
            val speed = (100f + Math.random() * 260f).toFloat()
            val life = (1.5f + Math.random() * 1.5f).toFloat()
            particles.add(
                Particle(
                    position = Vector2D(center.x + (Math.random() * 40 - 20).toFloat(), center.y + (Math.random() * 40 - 20).toFloat()),
                    velocity = Vector2D(cos(angle) * speed, sin(angle) * speed),
                    colorHex = colors.random(),
                    size = (10f + Math.random() * 14f).toFloat(),
                    life = life,
                    maxLife = life,
                    rotationSpeed = (Math.random() * 10 - 5).toFloat(),
                    shapeType = 1
                )
            )
        }
    }

    fun spawnDustPuff(pos: Vector2D, count: Int = 8) {
        for (i in 0 until count) {
            val angle = (Math.random() * Math.PI * 2).toFloat()
            val speed = (30f + Math.random() * 50f).toFloat()
            val life = 0.4f
            particles.add(
                Particle(
                    position = Vector2D(pos.x, pos.y),
                    velocity = Vector2D(cos(angle) * speed, sin(angle) * speed),
                    colorHex = 0xAAFFFFFF,
                    size = (8f + Math.random() * 8f).toFloat(),
                    life = life,
                    maxLife = life,
                    shapeType = 0
                )
            )
        }
    }

    fun spawnExplosionFx(pos: Vector2D) {
        triggerScreenShake(20f, 0.4f)
        // Blast wave ring
        particles.add(
            Particle(
                position = Vector2D(pos.x, pos.y),
                velocity = Vector2D(0f, 0f),
                colorHex = 0xFFFF5722,
                size = 120f,
                life = 0.35f,
                maxLife = 0.35f,
                shapeType = 3
            )
        )
        // Fiery sparks
        for (i in 0 until 35) {
            val angle = (Math.random() * Math.PI * 2).toFloat()
            val speed = (140f + Math.random() * 220f).toFloat()
            val life = 0.5f + (Math.random() * 0.3f).toFloat()
            val color = if (Math.random() > 0.5) 0xFFFF9800 else 0xFFFFEB3B
            particles.add(
                Particle(
                    position = Vector2D(pos.x, pos.y),
                    velocity = Vector2D(cos(angle) * speed, sin(angle) * speed),
                    colorHex = color,
                    size = (12f + Math.random() * 10f).toFloat(),
                    life = life,
                    maxLife = life,
                    shapeType = 0
                )
            )
        }
    }

    fun spawnTrail(pos: Vector2D, trailId: String, colorHex: Long) {
        val shape = when (trailId) {
            "trail_stars" -> 2
            "trail_confetti" -> 1
            "trail_bubbles" -> 3
            else -> 0
        }
        val life = 0.4f
        particles.add(
            Particle(
                position = Vector2D(pos.x + (Math.random() * 6 - 3).toFloat(), pos.y + (Math.random() * 6 - 3).toFloat()),
                velocity = Vector2D((Math.random() * 20 - 10).toFloat(), (Math.random() * 20 - 10).toFloat()),
                colorHex = colorHex,
                size = 8f,
                life = life,
                maxLife = life,
                shapeType = shape
            )
        )
    }

    fun showFloatingText(text: String, pos: Vector2D, colorHex: Long = 0xFFFFEB3B) {
        floatingTexts.add(
            FloatingText(
                id = ++textIdCounter,
                text = text,
                position = Vector2D(pos.x, pos.y - 20f),
                colorHex = colorHex
            )
        )
    }

    fun clear() {
        particles.clear()
        floatingTexts.clear()
        screenShakeAmount = 0f
        screenShakeDuration = 0f
    }
}
