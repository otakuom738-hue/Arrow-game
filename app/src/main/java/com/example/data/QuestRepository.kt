package com.example.data

import com.example.model.DailyQuest
import com.example.model.QuestType

class QuestRepository(private val prefs: GamePreferences) {

    fun getDailyQuests(): List<DailyQuest> {
        prefs.resetQuestsIfNewDay()

        return listOf(
            DailyQuest(
                id = "q1",
                title = "Target Breaker",
                description = "Break 30 targets across any game mode",
                currentProgress = prefs.getQuestProgress("q1"),
                targetProgress = 30,
                rewardCoins = 250,
                rewardGems = 15,
                isClaimed = prefs.isQuestClaimed("q1"),
                type = QuestType.BREAK_TARGETS
            ),
            DailyQuest(
                id = "q2",
                title = "Combo Master",
                description = "Achieve a 4x combo streak in one arrow shot",
                currentProgress = prefs.getQuestProgress("q2"),
                targetProgress = 1,
                rewardCoins = 300,
                rewardGems = 20,
                isClaimed = prefs.isQuestClaimed("q2"),
                type = QuestType.ACHIEVE_COMBO
            ),
            DailyQuest(
                id = "q3",
                title = "TNT Demolition",
                description = "Trigger 3 explosive bomb targets",
                currentProgress = prefs.getQuestProgress("q3"),
                targetProgress = 3,
                rewardCoins = 200,
                rewardGems = 10,
                isClaimed = prefs.isQuestClaimed("q3"),
                type = QuestType.TRIGGER_TNT
            ),
            DailyQuest(
                id = "q4",
                title = "Archery Addict",
                description = "Play 3 matches in Campaign or Rush mode",
                currentProgress = prefs.getQuestProgress("q4"),
                targetProgress = 3,
                rewardCoins = 350,
                rewardGems = 25,
                isClaimed = prefs.isQuestClaimed("q4"),
                type = QuestType.PLAY_GAMES
            )
        )
    }

    fun recordProgress(type: QuestType, amount: Int = 1) {
        getDailyQuests().forEach { quest ->
            if (quest.type == type && !quest.isClaimed) {
                val current = prefs.getQuestProgress(quest.id)
                val updated = (current + amount).coerceAtMost(quest.targetProgress)
                prefs.setQuestProgress(quest.id, updated)
            }
        }
    }

    fun claimQuest(questId: String): Boolean {
        val quest = getDailyQuests().find { it.id == questId } ?: return false
        if (quest.currentProgress >= quest.targetProgress && !quest.isClaimed) {
            prefs.coins += quest.rewardCoins
            prefs.gems += quest.rewardGems
            prefs.setQuestClaimed(questId, true)
            return true
        }
        return false
    }

    fun claimDailyStreak(): Pair<Int, Int>? {
        val now = System.currentTimeMillis()
        val dayInMillis = 86400000L
        val lastClaim = prefs.lastClaimDate

        if (now - lastClaim >= dayInMillis || lastClaim == 0L) {
            val currentStreak = if (now - lastClaim < dayInMillis * 2) {
                (prefs.streakDays % 7) + 1
            } else {
                1
            }
            prefs.streakDays = currentStreak
            prefs.lastClaimDate = now

            val coins = when (currentStreak) {
                1 -> 150
                2 -> 250
                3 -> 400
                4 -> 600
                5 -> 800
                6 -> 1000
                7 -> 2000
                else -> 200
            }
            val gems = when (currentStreak) {
                1 -> 10
                2 -> 20
                3 -> 30
                4 -> 45
                5 -> 60
                6 -> 80
                7 -> 150
                else -> 15
            }

            prefs.coins += coins
            prefs.gems += gems
            return Pair(coins, gems)
        }
        return null
    }

    fun claimFreeChest(): Pair<Int, Int> {
        prefs.lastFreeChestTime = System.currentTimeMillis()
        val coins = (120..350).random()
        val gems = (5..18).random()
        prefs.coins += coins
        prefs.gems += gems
        return Pair(coins, gems)
    }
}
