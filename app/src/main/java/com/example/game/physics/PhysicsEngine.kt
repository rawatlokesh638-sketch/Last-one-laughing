package com.example.game.physics

import com.example.audio.AudioManager
import com.example.audio.SoundEffect
import com.example.game.arena.ArenaManager
import com.example.game.arena.ArenaPlatform
import com.example.model.Player
import com.example.model.Vector2D
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class PhysicsEngine(
    private val arena: ArenaManager,
    private val particles: ParticleManager,
    private val audio: AudioManager
) {
    var gravity: Float = -55f
    var playerRadius: Float = 18f
    var baseMoveSpeed: Float = 180f
    var isIceFriction: Boolean = false
    var areControlsReversed: Boolean = false
    var speedMultiplier: Float = 1.0f

    fun updatePlayer(
        player: Player,
        moveInput: Vector2D,
        jumpPressed: Boolean,
        dashPressed: Boolean,
        deltaTimeSec: Float,
        onEliminated: (Player, String) -> Unit
    ) {
        if (player.isEliminated) return

        // Update timers
        if (player.dashCooldownTimer > 0f) {
            player.dashCooldownTimer -= deltaTimeSec
        }
        if (player.dashTimer > 0f) {
            player.dashTimer -= deltaTimeSec
            if (player.dashTimer <= 0f) {
                player.isDashing = false
            }
        }
        if (player.stunTimer > 0f) {
            player.stunTimer -= deltaTimeSec
            if (player.stunTimer <= 0f) {
                player.isStunned = false
            }
        }
        if (player.emoteTimer > 0f) {
            player.emoteTimer -= deltaTimeSec
            if (player.emoteTimer <= 0f) {
                player.activeEmote = null
            }
        }

        // Check Jump
        if (jumpPressed && player.isGrounded && !player.isStunned && !player.isFrozen) {
            player.velocityZ = 22f
            player.isGrounded = false
            audio.playSound(SoundEffect.JUMP)
            particles.spawnDustPuff(player.position, 6)
        }

        // Check Dash
        if (dashPressed && player.dashCooldownTimer <= 0f && !player.isStunned && !player.isFrozen) {
            player.isDashing = true
            player.dashTimer = 0.22f
            player.dashCooldownTimer = 1.4f
            
            // Dash in facing direction or input direction
            val dir = if (moveInput.x != 0f || moveInput.y != 0f) {
                moveInput.normalize()
            } else {
                Vector2D(cos(player.facingAngle), sin(player.facingAngle))
            }
            player.velocity.x = dir.x * 480f * speedMultiplier
            player.velocity.y = dir.y * 480f * speedMultiplier
            
            audio.playSound(SoundEffect.DASH)
            particles.spawnTrail(player.position, player.trailId, player.baseColorHex)
        }

        // Movement input
        if (!player.isStunned && !player.isFrozen && !player.isDashing) {
            var adjustedInput = moveInput
            if (areControlsReversed) {
                adjustedInput = Vector2D(-adjustedInput.x, -adjustedInput.y)
            }
            
            if (adjustedInput.x != 0f || adjustedInput.y != 0f) {
                val norm = adjustedInput.normalize()
                player.facingAngle = atan2(norm.y, norm.x)
                
                val targetVx = norm.x * baseMoveSpeed * speedMultiplier
                val targetVy = norm.y * baseMoveSpeed * speedMultiplier
                
                val accel = if (isIceFriction) 4f else 14f
                player.velocity.x += (targetVx - player.velocity.x) * (accel * deltaTimeSec)
                player.velocity.y += (targetVy - player.velocity.y) * (accel * deltaTimeSec)
                
                player.wobblePhase += 18f * deltaTimeSec
            } else {
                val friction = if (isIceFriction) 1.5f else 12f
                player.velocity.x *= (1f - friction * deltaTimeSec).coerceAtLeast(0f)
                player.velocity.y *= (1f - friction * deltaTimeSec).coerceAtLeast(0f)
            }
        }

        // Centrifugal push if arena rotating
        if (arena.isArenaRotating) {
            val dist = player.position.distanceTo(Vector2D(0f, 0f))
            if (dist > 10f) {
                val outward = player.position.normalize()
                player.velocity.x += outward.x * (dist * 0.4f) * deltaTimeSec
                player.velocity.y += outward.y * (dist * 0.4f) * deltaTimeSec
            }
        }

        // Position integration
        player.position.x += player.velocity.x * deltaTimeSec
        player.position.y += player.velocity.y * deltaTimeSec

        // Z-axis physics
        player.velocityZ += gravity * deltaTimeSec
        player.heightZ += player.velocityZ * 10f * deltaTimeSec

        // Platform detection & landing
        val platformUnder = arena.getPlatformUnder(player.position, player.heightZ)
        val platformElevation = platformUnder?.elevation ?: -100f

        if (platformUnder != null) {
            player.lastSafePlatformId = platformUnder.id
            player.safeColorTag = platformUnder.colorTag
            
            // Carry player if platform is moving
            if (platformUnder.isMoving) {
                // platform movement offset velocity
            }
        } else {
            player.safeColorTag = 0
        }

        if (player.heightZ <= platformElevation) {
            if (platformUnder != null) {
                // Landed on platform
                player.heightZ = platformElevation
                player.velocityZ = 0f
                if (!player.isGrounded) {
                    player.isGrounded = true
                    particles.spawnDustPuff(player.position, 4)
                }
            } else {
                // Falling into abyss / liquid
                player.isGrounded = false
                if (player.heightZ < -150f) {
                    onEliminated(player, "Fell into the Abyss! 🕳️")
                    return
                }
            }
        } else {
            player.isGrounded = false
        }

        // Hazards check: Lava
        if (arena.isLavaActive && player.heightZ < 25f && !player.isEliminated) {
            audio.playSound(SoundEffect.LAVA_SIZZLE)
            particles.spawnExplosionFx(player.position)
            onEliminated(player, "Melted in Lava! 🔥")
            return
        }

        // Hazards check: Water
        if (arena.isWaterActive && player.heightZ < arena.waterLevel && !player.isEliminated) {
            audio.playSound(SoundEffect.ELIMINATION_SPLAT)
            particles.spawnDustPuff(player.position, 12)
            onEliminated(player, "Swept by Tsunami! 🌊")
            return
        }

        // Hazards check: Shrinking Arena Ring
        val distFromCenter = player.position.distanceTo(Vector2D(0f, 0f))
        if (distFromCenter > arena.currentShrinkRadius && !player.isEliminated) {
            audio.playSound(SoundEffect.EXPLOSION)
            particles.spawnExplosionFx(player.position)
            onEliminated(player, "Crushed outside Laser Ring! ⭕")
            return
        }

        // Edge Guard Rails for Kids / Assist Mode
        if (arena.guardRailsActive) {
            val edgeDist = player.position.distanceTo(Vector2D(0f, 0f))
            if (edgeDist > arena.arenaBoundsRadius - 30f) {
                // Bounce back safely towards center
                val inward = (Vector2D(0f, 0f) - player.position).normalize()
                player.velocity.x = inward.x * 260f
                player.velocity.y = inward.y * 260f
                player.velocityZ = 12f
                player.isGrounded = false
                audio.playSound(SoundEffect.IMPACT_BONK)
                particles.showFloatingText("BOUNCE! 🌈", player.position, 0xFFFF4081)
                particles.spawnDustPuff(player.position, 8)
            }
            if (player.heightZ < -30f && !player.isEliminated) {
                // Rescue bounce back to closest platform!
                player.heightZ = 20f
                player.velocityZ = 18f
                player.velocity = Vector2D(0f, 0f)
                player.position = Vector2D(0f, 0f)
                particles.showFloatingText("SAFE! 🛡️", player.position, 0xFF00E676)
            }
        }

        // Collectibles check (Candy Rush / Balloons)
        arena.collectibles.forEach { col ->
            if (!col.isCollected) {
                val dist = player.position.distanceTo(col.position)
                if (dist < playerRadius + 20f && player.heightZ < 35f) {
                    col.isCollected = true
                    col.respawnTimer = 6f
                    player.candyScore += col.pointValue
                    audio.playSound(SoundEffect.JUMP)
                    particles.showFloatingText("+${col.pointValue} ${col.typeEmoji}", player.position, 0xFFFFD700)
                    particles.spawnConfettiShower(player.position, 12)
                }
            }
        }

        // Collision with Bumpers
        arena.bumpers.forEach { bumper ->
            val dist = player.position.distanceTo(bumper.center)
            val minDist = playerRadius + bumper.radius
            if (dist < minDist && dist > 0.001f) {
                val normal = (player.position - bumper.center).normalize()
                player.velocity.x = normal.x * (bumper.bounceForce * 22f)
                player.velocity.y = normal.y * (bumper.bounceForce * 22f)
                player.velocityZ = 16f
                player.isGrounded = false
                audio.playSound(SoundEffect.IMPACT_BONK)
                particles.showFloatingText("BOING!", player.position, 0xFFFFD700)
                particles.spawnDustPuff(player.position, 10)
            }
        }

        // Collision with Sweeper Bars
        arena.sweeperBars.forEach { bar ->
            val angle = bar.angle
            val endX = bar.pivot.x + cos(angle) * bar.length
            val endY = bar.pivot.y + sin(angle) * bar.length
            
            // Distance from player to segment
            val distToSegment = distanceToSegment(player.position, bar.pivot, Vector2D(endX, endY))
            if (distToSegment < playerRadius + bar.width / 2f && player.heightZ < 20f) {
                // Hit by rotating bar!
                val hitForce = 380f
                val pushDir = Vector2D(-sin(angle), cos(angle))
                player.velocity.x += pushDir.x * hitForce
                player.velocity.y += pushDir.y * hitForce
                player.velocityZ = 14f
                player.isGrounded = false
                player.isStunned = true
                player.stunTimer = 0.4f
                audio.playSound(SoundEffect.IMPACT_BONK)
                particles.showFloatingText("WHAM!", player.position, 0xFFFF5722)
                particles.spawnDustPuff(player.position, 8)
            }
        }

        // Collision with Rolling Balls
        arena.rollingBalls.forEach { ball ->
            val dist = player.position.distanceTo(ball.position)
            if (dist < playerRadius + ball.radius && player.heightZ < 30f) {
                val normal = (player.position - ball.position).normalize()
                player.velocity.x += normal.x * 520f + ball.velocity.x * 0.8f
                player.velocity.y += normal.y * 520f + ball.velocity.y * 0.8f
                player.velocityZ = 18f
                player.isGrounded = false
                player.isStunned = true
                player.stunTimer = 0.5f
                audio.playSound(SoundEffect.IMPACT_BONK)
                particles.showFloatingText("CRUSHED!", player.position, 0xFFFF3D00)
                particles.spawnExplosionFx(player.position)
            }
        }

        // Collision with Explosions
        arena.activeExplosions.forEach { exp ->
            if (exp.hasExploded && exp.blastTimer > 0.2f) {
                val dist = player.position.distanceTo(exp.position)
                if (dist < exp.maxRadius) {
                    val normal = if (dist > 0.001f) (player.position - exp.position).normalize() else Vector2D(1f, 0f)
                    val blastForce = 620f * (1f - dist / exp.maxRadius)
                    player.velocity.x += normal.x * blastForce
                    player.velocity.y += normal.y * blastForce
                    player.velocityZ = 24f
                    player.isGrounded = false
                    player.isStunned = true
                    player.stunTimer = 0.6f
                    particles.showFloatingText("BOOM!", player.position, 0xFFFF9100)
                }
            }
        }
    }

    fun resolvePlayerCollisions(players: List<Player>) {
        val alivePlayers = players.filter { it.isAlive }
        for (i in alivePlayers.indices) {
            val p1 = alivePlayers[i]
            for (j in i + 1 until alivePlayers.size) {
                val p2 = alivePlayers[j]
                
                // Check height overlap
                if (kotlin.math.abs(p1.heightZ - p2.heightZ) > 25f) continue
                
                val dist = p1.position.distanceTo(p2.position)
                val minDist = playerRadius * 2f
                if (dist < minDist && dist > 0.0001f) {
                    val overlap = minDist - dist
                    val normal = (p1.position - p2.position).normalize()
                    
                    // Separate positions
                    p1.position.x += normal.x * (overlap * 0.5f)
                    p1.position.y += normal.y * (overlap * 0.5f)
                    p2.position.x -= normal.x * (overlap * 0.5f)
                    p2.position.y -= normal.y * (overlap * 0.5f)
                    
                    // Push / Knockback calculation
                    var pushForce = 120f
                    if (p1.isDashing) {
                        pushForce = 440f
                        p2.velocity.x -= normal.x * pushForce
                        p2.velocity.y -= normal.y * pushForce
                        p2.isStunned = true
                        p2.stunTimer = 0.35f
                        p1.pushesLanded++
                        p2.timesPushed++
                        audio.playSound(SoundEffect.IMPACT_BONK)
                        particles.showFloatingText("BONK!", p2.position, 0xFFFFD54F)
                    } else if (p2.isDashing) {
                        pushForce = 440f
                        p1.velocity.x += normal.x * pushForce
                        p1.velocity.y += normal.y * pushForce
                        p1.isStunned = true
                        p1.stunTimer = 0.35f
                        p2.pushesLanded++
                        p1.timesPushed++
                        audio.playSound(SoundEffect.IMPACT_BONK)
                        particles.showFloatingText("BONK!", p1.position, 0xFFFFD54F)
                    } else {
                        // Gentle bounce
                        val dot1 = p1.velocity.x * normal.x + p1.velocity.y * normal.y
                        val dot2 = p2.velocity.x * normal.x + p2.velocity.y * normal.y
                        p1.velocity.x -= normal.x * dot1 * 0.8f
                        p1.velocity.y -= normal.y * dot1 * 0.8f
                        p2.velocity.x += normal.x * dot2 * 0.8f
                        p2.velocity.y += normal.y * dot2 * 0.8f
                    }

                    // Mode: Crown Steal on contact!
                    if (p1.hasCrown && !p2.hasCrown) {
                        p1.hasCrown = false
                        p2.hasCrown = true
                        audio.playSound(SoundEffect.COUNTDOWN_TICK)
                        particles.showFloatingText("CROWN STOLEN! 👑", p2.position, 0xFFFFD700)
                    } else if (p2.hasCrown && !p1.hasCrown) {
                        p2.hasCrown = false
                        p1.hasCrown = true
                        audio.playSound(SoundEffect.COUNTDOWN_TICK)
                        particles.showFloatingText("CROWN STOLEN! 👑", p1.position, 0xFFFFD700)
                    }

                    // Mode: Bomb Tag Passing on contact!
                    if (p1.hasBomb && !p2.hasBomb && p1.bombTimerSec > 0.4f) {
                        p1.hasBomb = false
                        p2.hasBomb = true
                        p2.bombTimerSec = p1.bombTimerSec
                        p1.bombTimerSec = 0f
                        audio.playSound(SoundEffect.IMPACT_BONK)
                        particles.showFloatingText("PASSED BOMB! 💣", p2.position, 0xFFFF3D00)
                    } else if (p2.hasBomb && !p1.hasBomb && p2.bombTimerSec > 0.4f) {
                        p2.hasBomb = false
                        p1.hasBomb = true
                        p1.bombTimerSec = p2.bombTimerSec
                        p2.bombTimerSec = 0f
                        audio.playSound(SoundEffect.IMPACT_BONK)
                        particles.showFloatingText("PASSED BOMB! 💣", p1.position, 0xFFFF3D00)
                    }
                }
            }
        }
    }

    private fun distanceToSegment(p: Vector2D, a: Vector2D, b: Vector2D): Float {
        val ab = b - a
        val ap = p - a
        val abLenSq = ab.x * ab.x + ab.y * ab.y
        if (abLenSq == 0f) return p.distanceTo(a)
        val t = ((ap.x * ab.x + ap.y * ab.y) / abLenSq).coerceIn(0f, 1f)
        val projection = Vector2D(a.x + ab.x * t, a.y + ab.y * t)
        return p.distanceTo(projection)
    }
}
