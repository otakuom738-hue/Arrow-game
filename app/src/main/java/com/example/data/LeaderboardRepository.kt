package com.example.data

import com.example.model.LeaderboardEntry

class LeaderboardRepository(private val prefs: GamePreferences) {

    private val baseGlobalPlayers = listOf(
        Pair("ShadowSniper", 6850),
        Pair("Valkyrie99", 5940),
        Pair("AeroStrike", 5320),
        Pair("NeoArcher", 4890),
        Pair("CyberHawk", 4420),
        Pair("PhoenixFury", 4150),
        Pair("ApexBowman", 3890),
        Pair("FrostQuiver", 3650),
        Pair("QuantumAim", 3420),
        Pair("TitanArrow", 3180),
        Pair("BlazeRanger", 2950),
        Pair("ZenithShot", 2780),
        Pair("GhostHunter", 2540),
        Pair("ViperStrike", 2390),
        Pair("NovaPierce", 2150),
        Pair("StormBringer", 1920),
        Pair("EchoArrow", 1750),
        Pair("PulseSniper", 1580),
        Pair("RiftWalker", 1390),
        Pair("IronFletch", 1120)
    )

    private val baseWeeklyPlayers = listOf(
        Pair("StarReaper", 4200),
        Pair("Valkyrie99", 3980),
        Pair("QuantumAim", 3750),
        Pair("NeoArcher", 3400),
        Pair("ApexBowman", 3150),
        Pair("ZenithShot", 2900),
        Pair("BlazeRanger", 2620),
        Pair("CyberHawk", 2410),
        Pair("EchoArrow", 2100),
        Pair("IronFletch", 1850)
    )

    fun getGlobalLeaderboard(): List<LeaderboardEntry> {
        val playerEntry = Pair(prefs.playerName, prefs.highestScore)
        val allEntries = (baseGlobalPlayers + playerEntry)
            .distinctBy { it.first }
            .sortedByDescending { it.second }

        return allEntries.mapIndexed { index, pair ->
            val rank = index + 1
            val league = when {
                rank <= 3 -> "Grand Master"
                rank <= 7 -> "Diamond"
                rank <= 12 -> "Platinum"
                rank <= 18 -> "Gold"
                else -> "Silver"
            }
            val badge = when (rank) {
                1 -> "👑"
                2 -> "🥈"
                3 -> "🥉"
                in 4..7 -> "💎"
                in 8..12 -> "⚔️"
                else -> "🏹"
            }
            LeaderboardEntry(
                rank = rank,
                username = pair.first,
                score = pair.second,
                league = league,
                badge = badge,
                isPlayer = pair.first == prefs.playerName
            )
        }
    }

    fun getWeeklyTournament(): List<LeaderboardEntry> {
        val playerWeeklyScore = (prefs.highestScore * 0.85).toInt().coerceAtLeast(350)
        val playerEntry = Pair(prefs.playerName, playerWeeklyScore)
        val allEntries = (baseWeeklyPlayers + playerEntry)
            .distinctBy { it.first }
            .sortedByDescending { it.second }

        return allEntries.mapIndexed { index, pair ->
            val rank = index + 1
            val league = when {
                rank <= 3 -> "Champions League"
                rank <= 6 -> "Diamond Division"
                else -> "Gold Division"
            }
            LeaderboardEntry(
                rank = rank,
                username = pair.first,
                score = pair.second,
                league = league,
                badge = when (rank) {
                    1 -> "🏆"
                    2 -> "🥈"
                    3 -> "🥉"
                    else -> "⭐"
                },
                isPlayer = pair.first == prefs.playerName
            )
        }
    }

    fun getPlayerRank(): Int {
        val list = getGlobalLeaderboard()
        val found = list.find { it.isPlayer }
        return found?.rank ?: (list.size + 1)
    }
}
