package com.example.model

import androidx.compose.ui.graphics.Color
import kotlin.math.sqrt

data class Vector2D(var x: Float, var y: Float) {
    fun distanceTo(other: Vector2D): Float {
        val dx = x - other.x
        val dy = y - other.y
        return sqrt(dx * dx + dy * dy)
    }

    fun normalize(): Vector2D {
        val len = sqrt(x * x + y * y)
        return if (len > 0.0001f) Vector2D(x / len, y / len) else Vector2D(0f, 0f)
    }

    operator fun plus(other: Vector2D): Vector2D = Vector2D(x + other.x, y + other.y)
    operator fun minus(other: Vector2D): Vector2D = Vector2D(x - other.x, y - other.y)
    operator fun times(scale: Float): Vector2D = Vector2D(x * scale, y * scale)
}

data class Player(
    val id: String,
    val name: String,
    val isLocalPlayer: Boolean = false,
    val isBot: Boolean = false,
    val botDifficulty: BotDifficulty = BotDifficulty.NORMAL,
    val avatarEmoji: String = "😃",
    val characterId: String = "char_classic",
    val outfitId: String = "outfit_default",
    val trailId: String = "trail_stars",
    val emoteId: String = "emote_laugh",
    val baseColorHex: Long = 0xFFFF5722,
    
    // Physics & Movement
    var position: Vector2D = Vector2D(0f, 0f),
    var velocity: Vector2D = Vector2D(0f, 0f),
    var heightZ: Float = 0f, // 0 = ground, >0 = jumping
    var velocityZ: Float = 0f,
    var isGrounded: Boolean = true,
    var facingAngle: Float = 0f,
    var isDashing: Boolean = false,
    var dashTimer: Float = 0f,
    var dashCooldownTimer: Float = 0f,
    var isStunned: Boolean = false,
    var stunTimer: Float = 0f,
    var isFrozen: Boolean = false,
    
    // Game state
    var isEliminated: Boolean = false,
    var eliminationRank: Int = 0,
    var eliminationReason: String = "",
    var eliminationTimeSec: Float = 0f,
    var eliminationsScored: Int = 0,
    var activeEmote: String? = null,
    var emoteTimer: Float = 0f,
    
    // Visual indicators
    var wobblePhase: Float = 0f,
    var squishScaleX: Float = 1f,
    var squishScaleY: Float = 1f,
    var hasCrown: Boolean = false,
    var safeColorTag: Int = 0,
    var isTargeted: Boolean = false,
    
    // Match tracking
    var nearMissCount: Int = 0,
    var timesPushed: Int = 0,
    var pushesLanded: Int = 0,
    var longestAirtimeSec: Float = 0f,
    var lastSafePlatformId: Int = -1,
    
    // Mode specific tracking
    var candyScore: Int = 0,
    var crownHoldSeconds: Float = 0f,
    var hasBomb: Boolean = false,
    var bombTimerSec: Float = 0f
) {
    val isAlive: Boolean get() = !isEliminated
}
