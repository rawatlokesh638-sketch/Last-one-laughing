package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.CosmeticsCatalog
import com.example.data.ProgressionManager
import com.example.game.arena.ArenaManager
import com.example.game.bot.BotManager
import com.example.game.events.EventManager
import com.example.game.physics.ParticleManager
import com.example.game.physics.PhysicsEngine
import com.example.model.EventType
import com.example.model.Player
import com.example.model.Vector2D
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun testAppName() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Last One Laughing", appName)
    }

    @Test
    fun testCosmeticsCatalogCompleteness() {
        assertTrue(CosmeticsCatalog.characters.size >= 6)
        assertTrue(CosmeticsCatalog.outfits.size >= 12)
        assertTrue(CosmeticsCatalog.emotes.size >= 8)
        assertTrue(CosmeticsCatalog.trails.size >= 8)
        assertTrue(CosmeticsCatalog.victoryEffects.size >= 6)
    }

    @Test
    fun testProgressionLeveling() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val progression = ProgressionManager(context)
        val initialLvl = progression.level
        progression.addXp(1000)
        assertTrue(progression.level > initialLvl)
        assertTrue(progression.coins > 0)
    }

    @Test
    fun testArenaPlatformsAndPhysics() {
        val arena = ArenaManager()
        val particles = ParticleManager()
        val physics = PhysicsEngine(arena, particles)

        val player = Player(
            id = "test_player",
            name = "Tester",
            position = Vector2D(0f, 0f)
        )

        physics.updatePlayer(
            player = player,
            moveInput = Vector2D(1f, 0f),
            jumpPressed = true,
            dashPressed = false,
            dt = 0.016f
        )

        assertTrue(player.velocity.x > 0f)
        assertTrue(player.heightZ > 0f)
    }

    @Test
    fun testEvent15TypesAvailable() {
        val allEvents = EventType.values()
        assertTrue("Must have at least 15 events", allEvents.size >= 15)
    }
}
