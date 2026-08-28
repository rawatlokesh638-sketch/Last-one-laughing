package com.example.game.engine

import com.example.model.ChaosMoment
import com.example.model.HighlightType
import com.example.model.Player

class ViralMomentTracker {
    private val moments = mutableListOf<ChaosMoment>()

    fun recordMoment(moment: ChaosMoment) {
        moments.add(moment)
    }

    fun recordCloseEscape(player: Player, hazardName: String, marginMs: Int = 120) {
        moments.add(
            ChaosMoment(
                type = HighlightType.CLOSEST_ESCAPE,
                title = "CLUTCH ESCAPE! ⚡",
                description = "${player.name} dodged $hazardName with only ${marginMs}ms to spare!",
                statValue = "${marginMs}ms margin"
            )
        )
    }

    fun recordLethalPush(pusher: Player, victim: Player) {
        moments.add(
            ChaosMoment(
                type = HighlightType.MOST_ELIMINATIONS,
                title = "SAVAGE KNOCKOUT! 🥊",
                description = "${pusher.name} sent ${victim.name} flying into the abyss!",
                statValue = "${pusher.pushesLanded} Pushes"
            )
        )
    }

    fun recordFreezeFailure(victim: Player) {
        moments.add(
            ChaosMoment(
                type = HighlightType.FUNNIEST_ELIMINATION,
                title = "THE WIGGLE OF DOOM! 🛑",
                description = "${victim.name} couldn't stay still for even 2 seconds!",
                statValue = "Instant Splat"
            )
        )
    }

    fun recordFinalDuel(winner: Player, runnerUp: Player) {
        moments.add(
            ChaosMoment(
                type = HighlightType.FINAL_DUEL,
                title = "LAST ONE LAUGHING! 👑",
                description = "${winner.name} outlasted ${runnerUp.name} in an intense final 1v1 duel!",
                statValue = "Victory #1"
            )
        )
    }

    fun getBestChaosMoment(localPlayer: Player, isWinner: Boolean): ChaosMoment {
        // Return local player's best moment or the match pinnacle
        if (isWinner) {
            return ChaosMoment(
                type = HighlightType.FINAL_DUEL,
                title = "LAST ONE LAUGHING! 😂👑",
                description = "You survived every single chaotic event and crushed 11 opponents!",
                statValue = "CHAMPION"
            )
        }

        if (localPlayer.pushesLanded >= 2) {
            return ChaosMoment(
                type = HighlightType.MOST_ELIMINATIONS,
                title = "ARENA BULLY! 🥊",
                description = "You shoved ${localPlayer.pushesLanded} players straight into hazards!",
                statValue = "${localPlayer.pushesLanded} Pushes"
            )
        }

        if (localPlayer.nearMissCount >= 2) {
            return ChaosMoment(
                type = HighlightType.CLOSEST_ESCAPE,
                title = "DEATH DEFIER! ⚡",
                description = "Narrowly dodged ${localPlayer.nearMissCount} fatal explosions and boulders!",
                statValue = "${localPlayer.nearMissCount} Near Misses"
            )
        }

        // Fallback funny highlight
        val candidate = moments.lastOrNull()
        if (candidate != null) return candidate

        return ChaosMoment(
            type = HighlightType.LONGEST_SURVIVAL,
            title = "VALIANT EFFORT! ⏱️",
            description = "You survived deep into the chaos before getting bonked into outer space!",
            statValue = "Rank #${localPlayer.eliminationRank}"
        )
    }

    fun clear() {
        moments.clear()
    }
}
