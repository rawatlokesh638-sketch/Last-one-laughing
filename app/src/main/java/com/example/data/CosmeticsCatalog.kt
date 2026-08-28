package com.example.data

import com.example.model.CosmeticItem
import com.example.model.CosmeticType

object CosmeticsCatalog {
    val characters = listOf(
        CosmeticItem("char_classic", "Laughing King", CosmeticType.CHARACTER, "The chaotic monarch of LOL!", 0, "👑", 0xFFFFB300, 0xFFFFA000, "Classic", true),
        CosmeticItem("char_dino", "Disco Dino", CosmeticType.CHARACTER, "Prehistoric beats and groovy jumps.", 500, "🦖", 0xFF4CAF50, 0xFF81C784, "Rare", false),
        CosmeticItem("char_toaster", "Turbo Toaster", CosmeticType.CHARACTER, "Crispy jumps and heated collisions!", 800, "🍞", 0xFFFF9800, 0xFFFFB74D, "Rare", false),
        CosmeticItem("char_robo", "Robo Chuckle", CosmeticType.CHARACTER, "Programmed exclusively for chaotic comedy.", 1200, "🤖", 0xFF00BCD4, 0xFF4DD0E1, "Epic", false),
        CosmeticItem("char_monkey", "Space Monkey", CosmeticType.CHARACTER, "Zero-gravity mischief and supersonic spins.", 1500, "🐒", 0xFF9C27B0, 0xFFBA68C8, "Epic", false),
        CosmeticItem("char_ninja", "Ninja Giggles", CosmeticType.CHARACTER, "Silent footsteps, extremely loud laughs.", 2500, "🥷", 0xFF212121, 0xFFE91E63, "Legendary", false)
    )

    val outfits = listOf(
        CosmeticItem("outfit_default", "Standard Tee", CosmeticType.OUTFIT, "Ready for the chaos arena.", 0, "👕", 0xFFE0E0E0, 0xFFBDBDBD, "Classic", true),
        CosmeticItem("outfit_neon", "Neon Party", CosmeticType.OUTFIT, "Glows under party spotlights.", 300, "🪩", 0xFF00E676, 0xFF1DE9B6, "Common", false),
        CosmeticItem("outfit_golden", "Golden Tux", CosmeticType.OUTFIT, "Survive the lava in high class.", 1500, "🤵", 0xFFFFD700, 0xFFFFA000, "Legendary", false),
        CosmeticItem("outfit_pajamas", "Comfy PJs", CosmeticType.OUTFIT, "Don't fall asleep while dodging boulders!", 400, "💤", 0xFF90CAF9, 0xFFE1BEE7, "Common", false),
        CosmeticItem("outfit_cyber", "Cyber Armor", CosmeticType.OUTFIT, "Reinforced chassis against giant balls.", 900, "🛡️", 0xFF00E5FF, 0xFF3D5AFE, "Epic", false),
        CosmeticItem("outfit_chef", "Chef Apron", CosmeticType.OUTFIT, "Cooking up certified chaos.", 450, "🧑‍🍳", 0xFFFFEB3B, 0xFFFF5722, "Common", false),
        CosmeticItem("outfit_hero", "Superhero Cape", CosmeticType.OUTFIT, "Does not give flight, but looks awesome!", 850, "🦸", 0xFFF44336, 0xFF2196F3, "Rare", false),
        CosmeticItem("outfit_wizard", "Wizard Robe", CosmeticType.OUTFIT, "Pure comedic sorcery.", 950, "🧙", 0xFF7E57C2, 0xFFFFD54F, "Rare", false),
        CosmeticItem("outfit_astro", "Astro Suit", CosmeticType.OUTFIT, "Pressurized for high-altitude catapulting.", 1100, "👨‍🚀", 0xFFECEFF1, 0xFF00E676, "Epic", false),
        CosmeticItem("outfit_rocker", "Rocker Leather", CosmeticType.OUTFIT, "Heavy metal riffs on falling tiles.", 750, "🎸", 0xFF37474F, 0xFFFF1744, "Rare", false),
        CosmeticItem("outfit_beach", "Beach Swimmer", CosmeticType.OUTFIT, "Prepared for rising tsunamis.", 500, "🩳", 0xFF29B6F6, 0xFFFF7043, "Common", false),
        CosmeticItem("outfit_clown", "Clown Jester", CosmeticType.OUTFIT, "The true laughing legend outfit.", 2000, "🤡", 0xFFFF4081, 0xFF76FF03, "Legendary", false)
    )

    val emotes = listOf(
        CosmeticItem("emote_laugh", "Mega Laugh", CosmeticType.EMOTE, "Ha ha ha ha!", 0, "😂", 0xFFFFD54F, isUnlocked = true),
        CosmeticItem("emote_cry", "Salty Tears", CosmeticType.EMOTE, "Why me?!", 200, "😭", 0xFF81D4FA, isUnlocked = false),
        CosmeticItem("emote_rage", "Rage Bonk", CosmeticType.EMOTE, "Who pushed me?!", 350, "🤬", 0xFFFF5252, isUnlocked = false),
        CosmeticItem("emote_flex", "Pure Muscle", CosmeticType.EMOTE, "Unstoppable force!", 400, "💪", 0xFFFFB74D, isUnlocked = false),
        CosmeticItem("emote_dance", "Victory Groove", CosmeticType.EMOTE, "Show off your moves!", 600, "💃", 0xFFF06292, isUnlocked = false),
        CosmeticItem("emote_crown", "Crown Flaunt", CosmeticType.EMOTE, "The throne belongs to me.", 1000, "👑", 0xFFFFD700, isUnlocked = false),
        CosmeticItem("emote_confused", "What Happened?", CosmeticType.EMOTE, "Total confusion!", 250, "❓", 0xFFB39DDB, isUnlocked = false),
        CosmeticItem("emote_fire", "On Fire!", CosmeticType.EMOTE, "Hot streak!", 500, "🔥", 0xFFFF7043, isUnlocked = false),
        CosmeticItem("emote_skull", "Oof Skull", CosmeticType.EMOTE, "RIP in peace.", 300, "💀", 0xFFCFD8DC, isUnlocked = false),
        CosmeticItem("emote_popcorn", "Spectator Snacks", CosmeticType.EMOTE, "Watching the mayhem unfold.", 450, "🍿", 0xFFFFEE58, isUnlocked = false)
    )

    val trails = listOf(
        CosmeticItem("trail_stars", "Star Sparkles", CosmeticType.TRAIL, "Glitter wherever you sprint.", 0, "✨", 0xFFFFEB3B, isUnlocked = true),
        CosmeticItem("trail_rainbow", "Rainbow Ribbon", CosmeticType.TRAIL, "A spectrum of high-speed fun.", 400, "🌈", 0xFFFF4081, isUnlocked = false),
        CosmeticItem("trail_lava", "Lava Embers", CosmeticType.TRAIL, "Leave fiery footprints behind.", 600, "🔥", 0xFFFF5722, isUnlocked = false),
        CosmeticItem("trail_bubbles", "Bubble Poppers", CosmeticType.TRAIL, "Bubbly aerodynamic bliss.", 350, "🫧", 0xFF29B6F6, isUnlocked = false),
        CosmeticItem("trail_neon", "Neon Laser Line", CosmeticType.TRAIL, "Ultra bright futuristic trail.", 800, "⚡", 0xFF00E676, isUnlocked = false),
        CosmeticItem("trail_ghost", "Ghostly Smoke", CosmeticType.TRAIL, "Spooky mist trailing in your wake.", 700, "👻", 0xFFB388FF, isUnlocked = false),
        CosmeticItem("trail_glitch", "Cyber Matrix", CosmeticType.TRAIL, "Digital distortion following your dash.", 1000, "👾", 0xFF00E5FF, isUnlocked = false),
        CosmeticItem("trail_confetti", "Party Popper", CosmeticType.TRAIL, "Perpetual party wherever you step.", 550, "🎉", 0xFFFF80AB, isUnlocked = false)
    )

    val victoryEffects = listOf(
        CosmeticItem("vic_confetti", "Confetti Storm", CosmeticType.VICTORY_EFFECT, "Explosion of colorful paper!", 0, "🎊", 0xFFFF4081, isUnlocked = true),
        CosmeticItem("vic_fireworks", "Grand Fireworks", CosmeticType.VICTORY_EFFECT, "Sky-high colorful detonations.", 500, "🎆", 0xFFFFD700, isUnlocked = false),
        CosmeticItem("vic_golden_rain", "Golden Coin Shower", CosmeticType.VICTORY_EFFECT, "Raining pure gold coins.", 1000, "🪙", 0xFFFFC107, isUnlocked = false),
        CosmeticItem("vic_disco", "Laser Dome Party", CosmeticType.VICTORY_EFFECT, "Pulsing disco strobes and music.", 800, "🪩", 0xFF7C4DFF, isUnlocked = false),
        CosmeticItem("vic_crown_burst", "Royal Crown Burst", CosmeticType.VICTORY_EFFECT, "Giant golden halos and trumpets.", 1200, "👑", 0xFFFFAB00, isUnlocked = false),
        CosmeticItem("vic_tornado", "Chaos Whirlwind", CosmeticType.VICTORY_EFFECT, "A comical cyclone of celebration.", 1500, "🌪️", 0xFF00E5FF, isUnlocked = false)
    )
}
