package com.example.model

import androidx.compose.ui.graphics.Color

enum class GameState {
    SPLASH,
    MAIN_MENU,
    MODE_SELECT,
    LOBBY,
    MATCH_COUNTDOWN,
    PLAYING,
    EVENT_ALERT,
    SPECTATING,
    MATCH_OVER,
    RESULTS,
    SHOP,
    COSMETICS,
    MISSIONS,
    LEADERBOARDS,
    SETTINGS,
    FRIENDS_ROOM
}

enum class MatchMode(
    val title: String,
    val description: String,
    val iconEmoji: String,
    val tagLabel: String,
    val isKidsFriendly: Boolean,
    val themeColorHex: Long
) {
    QUICK_CHAOS(
        title = "Party Survival",
        description = "Classic multi-event knockout mayhem! Dodging meteors, sweeper bars, and falling platforms.",
        iconEmoji = "🎉",
        tagLabel = "All Ages",
        isKidsFriendly = true,
        themeColorHex = 0xFF7C4DFF
    ),
    CANDY_RUSH(
        title = "Candy & Balloons",
        description = "Super fun & easy for young kids! Pop balloons and collect 15 sweets with bouncy protective guard rails!",
        iconEmoji = "🍬",
        tagLabel = "🧸 Kids Easy",
        isKidsFriendly = true,
        themeColorHex = 0xFFFF4081
    ),
    COLOR_DANCE(
        title = "Color Tile Dance",
        description = "Disco lights frenzy! Run to the announced color tile before other platforms vanish!",
        iconEmoji = "🎨",
        tagLabel = "🌈 Fun Party",
        isKidsFriendly = true,
        themeColorHex = 0xFF00E676
    ),
    CROWN_CHASE(
        title = "Crown Tag King",
        description = "Steal and wear the Golden King's Crown for 20 seconds! Dash into the crown holder to snatch it!",
        iconEmoji = "👑",
        tagLabel = "👑 Arcade Tag",
        isKidsFriendly = true,
        themeColorHex = 0xFFFFD700
    ),
    BOMB_PARTY(
        title = "Hot Potato Bomb",
        description = "Ticking explosive TNT attached to a player! Bump opponents to pass it before it goes BOOM!",
        iconEmoji = "💣",
        tagLabel = "⚡ Fast Thrills",
        isKidsFriendly = false,
        themeColorHex = 0xFFFF5722
    ),
    LAVA_RUN(
        title = "Floor is Lava Extreme",
        description = "Extreme high heat survival! Rising molten tides, disintegrating platforms, and meteor showers!",
        iconEmoji = "🌋",
        tagLabel = "🔥 Young / Teens",
        isKidsFriendly = false,
        themeColorHex = 0xFFFF1744
    ),
    FRIENDS_ROOM(
        title = "Custom Private Room",
        description = "Create or join a private multiplayer room with your friends and custom rules!",
        iconEmoji = "👥",
        tagLabel = "Multiplayer",
        isKidsFriendly = true,
        themeColorHex = 0xFF8B5CF6
    ),
    PRACTICE(
        title = "Bot Training Arena",
        description = "Practice movement, jumps, and dashes at your own pace against friendly training bots.",
        iconEmoji = "🤖",
        tagLabel = "Training",
        isKidsFriendly = true,
        themeColorHex = 0xFF00B0FF
    )
}

enum class BotDifficulty {
    EASY,
    NORMAL,
    HARD,
    CHAOTIC
}

enum class HighlightType(val title: String, val badge: String) {
    LONGEST_SURVIVAL("Survivor Legend", "⏱️"),
    CLOSEST_ESCAPE("Death Defier", "⚡"),
    BIGGEST_FALL("Epic Splashdown", "🌊"),
    MOST_ELIMINATIONS("Ring Master", "🥊"),
    LAST_SECOND_DODGE("Clutch Reflexes", "🎯"),
    FUNNIEST_ELIMINATION("Slip & Slide", "🍌"),
    FINAL_DUEL("The Showdown", "👑")
}

data class ChaosMoment(
    val type: HighlightType,
    val title: String,
    val description: String,
    val statValue: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class EliminationFeedItem(
    val id: String,
    val victimName: String,
    val victimEmoji: String,
    val reason: String,
    val isLocalVictim: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class PlayerStats(
    var totalMatches: Int = 0,
    var totalWins: Int = 0,
    var totalEliminations: Int = 0,
    var totalLavaSurvivals: Int = 0,
    var winStreak: Int = 0,
    var bestWinStreak: Int = 0,
    var totalFalls: Int = 0,
    var totalNearMisses: Int = 0,
    var totalPushes: Int = 0
)

data class DailyMission(
    val id: String,
    val title: String,
    val description: String,
    val targetCount: Int,
    var currentCount: Int = 0,
    val rewardCoins: Int,
    val rewardXP: Int,
    var isClaimed: Boolean = false
) {
    val isCompleted: Boolean get() = currentCount >= targetCount
}

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val targetCount: Int,
    var currentCount: Int = 0,
    val rewardCoins: Int,
    var isUnlocked: Boolean = false
) {
    val isCompleted: Boolean get() = currentCount >= targetCount
}

enum class CosmeticType {
    CHARACTER,
    OUTFIT,
    TRAIL,
    EMOTE,
    VICTORY_EFFECT
}

data class CosmeticItem(
    val id: String,
    val name: String,
    val type: CosmeticType,
    val description: String,
    val price: Int,
    val iconEmoji: String,
    val primaryColorHex: Long,
    val secondaryColorHex: Long = 0xFFFFFFFF,
    val rarity: String = "Rare",
    var isUnlocked: Boolean = false
)

enum class EventType(
    val displayName: String,
    val shortInstruction: String,
    val iconEmoji: String,
    val themeColorHex: Long,
    val dangerLevel: Int // 1 to 3
) {
    FLOOR_IS_LAVA("FLOOR IS LAVA!", "GET TO HIGH GROUND NOW!", "🔥", 0xFFFF3B30, 2),
    DONT_MOVE("DON'T MOVE!", "FREEZE! ANY MOVEMENT ELIMINATES YOU!", "🛑", 0xFFFF9500, 3),
    FALLING_PLATFORMS("FALLING PLATFORMS!", "RED TILES ARE COLLAPSING!", "🧱", 0xFFFF2D55, 2),
    SAFE_COLOR("SAFE COLOR!", "STAND ON THE ANNOUNCED COLOR!", "🎨", 0xFF5856D6, 2),
    GIANT_ROLLING_BALL("BOULDER RUSH!", "DODGE THE ROLLING TITAN!", "⚽", 0xFFFF9F0A, 2),
    RISING_WATER("TSUNAMI SURGE!", "JUMP UP! WATER LEVEL IS RISING!", "🌊", 0xFF007AFF, 1),
    SHRINKING_ARENA("SHRINKING ARENA!", "STAY INSIDE THE LASER RING!", "⭕", 0xFFAF52DE, 2),
    RANDOM_EXPLOSIONS("MINEFIELD CHAOS!", "EVACUATE BLAST RETICLES!", "💣", 0xFFFF453A, 3),
    SPEED_BOOST("SUPER SPEED & ICE!", "SPEED BOOST WITH ZERO FRICTION!", "⚡", 0xFF30D158, 1),
    REVERSE_CONTROLS("REVERSED CONTROLS!", "UP IS DOWN, LEFT IS RIGHT!", "🔄", 0xFFBF5AF2, 3),
    MOVING_WALLS("SPINNING SWEEPERS!", "JUMP OVER THE ROTATING BARS!", "🚧", 0xFFFFD60A, 2),
    FALLING_OBJECTS("METEOR SHOWER!", "LOOK OUT ABOVE! DODGE FALLING METEORS!", "☄️", 0xFFFF375F, 3),
    ROTATING_ARENA("CENTRIFUGE SPIN!", "DON'T GET FLUNG OFF THE EDGE!", "🌀", 0xFF64D2FF, 2),
    INVISIBLE_PLATFORMS("GHOST FLOORS!", "PLATFORMS VANISHED! REMEMBER POSITIONS!", "👻", 0xFF63E6E2, 3),
    CHAOS_MODE("ULTIMATE CHAOS!", "ALL HAZARDS COMBINED! SURVIVE!", "💀", 0xFFFF0055, 3)
}
