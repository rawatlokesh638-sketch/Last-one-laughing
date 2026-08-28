package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.Achievement
import com.example.model.DailyMission
import com.example.model.PlayerStats

class ProgressionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("lol_party_save", Context.MODE_PRIVATE)

    var playerName: String
        get() = prefs.getString("player_name", "LaughingChamp") ?: "LaughingChamp"
        set(value) = prefs.edit().putString("player_name", value).apply()

    var level: Int
        get() = prefs.getInt("player_level", 1)
        set(value) = prefs.edit().putInt("player_level", value).apply()

    var currentXp: Int
        get() = prefs.getInt("player_xp", 0)
        set(value) = prefs.edit().putInt("player_xp", value).apply()

    var coins: Int
        get() = prefs.getInt("player_coins", 600) // Starting bonus
        set(value) = prefs.edit().putInt("player_coins", value).apply()

    var wins: Int
        get() = prefs.getInt("player_wins", 0)
        set(value) = prefs.edit().putInt("player_wins", value).apply()

    var matchesPlayed: Int
        get() = prefs.getInt("player_matches", 0)
        set(value) = prefs.edit().putInt("player_matches", value).apply()

    var winStreak: Int
        get() = prefs.getInt("win_streak", 0)
        set(value) = prefs.edit().putInt("win_streak", value).apply()

    var bestWinStreak: Int
        get() = prefs.getInt("best_win_streak", 0)
        set(value) = prefs.edit().putInt("best_win_streak", value).apply()

    var totalEliminations: Int
        get() = prefs.getInt("total_elims", 0)
        set(value) = prefs.edit().putInt("total_elims", value).apply()

    // Equipped Cosmetics
    var equippedCharacterId: String
        get() = prefs.getString("equipped_char", "char_classic") ?: "char_classic"
        set(value) = prefs.edit().putString("equipped_char", value).apply()

    var equippedOutfitId: String
        get() = prefs.getString("equipped_outfit", "outfit_default") ?: "outfit_default"
        set(value) = prefs.edit().putString("equipped_outfit", value).apply()

    var equippedTrailId: String
        get() = prefs.getString("equipped_trail", "trail_stars") ?: "trail_stars"
        set(value) = prefs.edit().putString("equipped_trail", value).apply()

    var equippedEmoteId: String
        get() = prefs.getString("equipped_emote", "emote_laugh") ?: "emote_laugh"
        set(value) = prefs.edit().putString("equipped_emote", value).apply()

    var equippedVictoryEffectId: String
        get() = prefs.getString("equipped_vic", "vic_confetti") ?: "vic_confetti"
        set(value) = prefs.edit().putString("equipped_vic", value).apply()

    // Settings
    var graphicsQuality: String // "LOW", "MEDIUM", "HIGH"
        get() = prefs.getString("graphics_quality", "HIGH") ?: "HIGH"
        set(value) = prefs.edit().putString("graphics_quality", value).apply()

    var screenShakeEnabled: Boolean
        get() = prefs.getBoolean("screen_shake", true)
        set(value) = prefs.edit().putBoolean("screen_shake", value).apply()

    var sfxEnabled: Boolean
        get() = prefs.getBoolean("sfx_enabled", true)
        set(value) = prefs.edit().putBoolean("sfx_enabled", value).apply()

    var bgmEnabled: Boolean
        get() = prefs.getBoolean("bgm_enabled", true)
        set(value) = prefs.edit().putBoolean("bgm_enabled", value).apply()

    // Kids & Young player accessibility
    var kidsAssistEnabled: Boolean
        get() = prefs.getBoolean("kids_assist", true) // Enabled by default for easy playing!
        set(value) = prefs.edit().putBoolean("kids_assist", value).apply()

    var controlStyle: String // "JOYSTICK", "LARGE_BUTTONS", "TAP_TO_MOVE"
        get() = prefs.getString("control_style", "JOYSTICK") ?: "JOYSTICK"
        set(value) = prefs.edit().putString("control_style", value).apply()

    val dailyMissions = mutableListOf(
        DailyMission("m1", "Survive 3 Matches", "Play in 3 chaotic survival matches", 3, 0, 150, 100),
        DailyMission("m2", "Top 3 Podium", "Finish in the Top 3 players in any match", 1, 0, 200, 150),
        DailyMission("m3", "Lava Survivor", "Survive during a Floor is Lava event", 1, 0, 180, 120),
        DailyMission("m4", "Push Master", "Push and bonk 5 opponents", 5, 0, 250, 200),
        DailyMission("m5", "Last One Laughing", "Win 1 complete multiplayer match", 1, 0, 500, 400)
    )

    val achievements = mutableListOf(
        Achievement("a1", "First Chuckle", "Play your very first party survival match", "🎮", 1, 0, 100),
        Achievement("a2", "Ultimate Champion", "Become the Last One Laughing 5 times", "👑", 5, 0, 1000),
        Achievement("a3", "Heavy Hitter", "Bonk 25 opponents into hazards", "🥊", 25, 0, 500),
        Achievement("a4", "Immortal Jumper", "Survive 10 distinct hazard events", "🔥", 10, 0, 400),
        Achievement("a5", "High Roller", "Accumulate 2,000 total gold coins", "💰", 2000, 0, 600)
    )

    fun xpForNextLevel(lvl: Int = level): Int = lvl * 150

    fun addXp(amount: Int): Boolean {
        currentXp += amount
        var leveledUp = false
        while (currentXp >= xpForNextLevel(level)) {
            currentXp -= xpForNextLevel(level)
            level++
            coins += 200 // Level up reward!
            leveledUp = true
        }
        return leveledUp
    }

    fun addCoins(amount: Int) {
        coins += amount
        updateAchievementProgress("a5", coins)
    }

    fun isCosmeticUnlocked(id: String): Boolean {
        // Free defaults
        if (id in listOf("char_classic", "outfit_default", "trail_stars", "emote_laugh", "vic_confetti")) return true
        return prefs.getBoolean("unlocked_$id", false)
    }

    fun unlockCosmetic(id: String) {
        prefs.edit().putBoolean("unlocked_$id", true).apply()
    }

    fun updateMissionProgress(missionId: String, amount: Int = 1) {
        dailyMissions.find { it.id == missionId }?.let { mission ->
            if (!mission.isCompleted) {
                mission.currentCount = (mission.currentCount + amount).coerceAtMost(mission.targetCount)
            }
        }
    }

    fun updateAchievementProgress(achievementId: String, amount: Int) {
        achievements.find { it.id == achievementId }?.let { ach ->
            if (!ach.isCompleted) {
                ach.currentCount = amount.coerceAtMost(ach.targetCount)
            }
        }
    }

    fun claimDailyReward(): Boolean {
        val lastClaimTime = prefs.getLong("last_daily_claim", 0L)
        val now = System.currentTimeMillis()
        // 20 hours cooldown
        if (now - lastClaimTime > 20 * 60 * 60 * 1000L || lastClaimTime == 0L) {
            prefs.edit().putLong("last_daily_claim", now).apply()
            addCoins(300)
            addXp(150)
            return true
        }
        return false
    }
}
