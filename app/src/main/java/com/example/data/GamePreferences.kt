package com.example.data

import android.content.Context
import android.content.SharedPreferences

class GamePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("arrow_break_prefs", Context.MODE_PRIVATE)

    var coins: Int
        get() = prefs.getInt(KEY_COINS, 450)
        set(value) = prefs.edit().putInt(KEY_COINS, value).apply()

    var gems: Int
        get() = prefs.getInt(KEY_GEMS, 35)
        set(value) = prefs.edit().putInt(KEY_GEMS, value).apply()

    var energy: Int
        get() = prefs.getInt(KEY_ENERGY, 5)
        set(value) = prefs.edit().putInt(KEY_ENERGY, value.coerceIn(0, 5)).apply()

    var lastEnergyTime: Long
        get() = prefs.getLong(KEY_LAST_ENERGY_TIME, System.currentTimeMillis())
        set(value) = prefs.edit().putLong(KEY_LAST_ENERGY_TIME, value).apply()

    var highestScore: Int
        get() = prefs.getInt(KEY_HIGHEST_SCORE, 1420)
        set(value) = prefs.edit().putInt(KEY_HIGHEST_SCORE, value).apply()

    var campaignLevel: Int
        get() = prefs.getInt(KEY_CAMPAIGN_LEVEL, 1)
        set(value) = prefs.edit().putInt(KEY_CAMPAIGN_LEVEL, value).apply()

    var playerName: String
        get() = prefs.getString(KEY_PLAYER_NAME, "ArcherPro") ?: "ArcherPro"
        set(value) = prefs.edit().putString(KEY_PLAYER_NAME, value).apply()

    var selectedBowId: String
        get() = prefs.getString(KEY_SELECTED_BOW, "wooden_recurve") ?: "wooden_recurve"
        set(value) = prefs.edit().putString(KEY_SELECTED_BOW, value).apply()

    var selectedTrailId: String
        get() = prefs.getString(KEY_SELECTED_TRAIL, "classic") ?: "classic"
        set(value) = prefs.edit().putString(KEY_SELECTED_TRAIL, value).apply()

    var isVip: Boolean
        get() = prefs.getBoolean(KEY_IS_VIP, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_VIP, value).apply()

    var isNoAds: Boolean
        get() = prefs.getBoolean(KEY_IS_NO_ADS, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_NO_ADS, value).apply()

    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND, value).apply()

    var hapticsEnabled: Boolean
        get() = prefs.getBoolean(KEY_HAPTICS, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTICS, value).apply()

    var streakDays: Int
        get() = prefs.getInt(KEY_STREAK_DAYS, 2)
        set(value) = prefs.edit().putInt(KEY_STREAK_DAYS, value).apply()

    var lastClaimDate: Long
        get() = prefs.getLong(KEY_LAST_CLAIM_DATE, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_CLAIM_DATE, value).apply()

    var lastFreeChestTime: Long
        get() = prefs.getLong(KEY_LAST_CHEST_TIME, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_CHEST_TIME, value).apply()

    fun getUnlockedBows(): Set<String> {
        val set = prefs.getStringSet(KEY_UNLOCKED_BOWS, null)
        return set ?: setOf("wooden_recurve")
    }

    fun unlockBow(bowId: String) {
        val set = getUnlockedBows().toMutableSet()
        set.add(bowId)
        prefs.edit().putStringSet(KEY_UNLOCKED_BOWS, set).apply()
    }

    fun getUnlockedTrails(): Set<String> {
        val set = prefs.getStringSet(KEY_UNLOCKED_TRAILS, null)
        return set ?: setOf("classic")
    }

    fun unlockTrail(trailId: String) {
        val set = getUnlockedTrails().toMutableSet()
        set.add(trailId)
        prefs.edit().putStringSet(KEY_UNLOCKED_TRAILS, set).apply()
    }

    fun getQuestProgress(questId: String): Int {
        return prefs.getInt("quest_prog_$questId", 0)
    }

    fun setQuestProgress(questId: String, progress: Int) {
        prefs.edit().putInt("quest_prog_$questId", progress).apply()
    }

    fun isQuestClaimed(questId: String): Boolean {
        return prefs.getBoolean("quest_claim_$questId", false)
    }

    fun setQuestClaimed(questId: String, claimed: Boolean) {
        prefs.edit().putBoolean("quest_claim_$questId", claimed).apply()
    }

    fun resetQuestsIfNewDay() {
        val lastDay = prefs.getLong("last_quest_day", 0L)
        val now = System.currentTimeMillis()
        val dayInMillis = 86400000L
        if (now - lastDay > dayInMillis) {
            prefs.edit()
                .putLong("last_quest_day", now)
                .remove("quest_prog_q1")
                .remove("quest_prog_q2")
                .remove("quest_prog_q3")
                .remove("quest_prog_q4")
                .remove("quest_claim_q1")
                .remove("quest_claim_q2")
                .remove("quest_claim_q3")
                .remove("quest_claim_q4")
                .apply()
        }
    }

    companion object {
        private const val KEY_COINS = "coins"
        private const val KEY_GEMS = "gems"
        private const val KEY_ENERGY = "energy"
        private const val KEY_LAST_ENERGY_TIME = "last_energy_time"
        private const val KEY_HIGHEST_SCORE = "highest_score"
        private const val KEY_CAMPAIGN_LEVEL = "campaign_level"
        private const val KEY_PLAYER_NAME = "player_name"
        private const val KEY_SELECTED_BOW = "selected_bow"
        private const val KEY_SELECTED_TRAIL = "selected_trail"
        private const val KEY_UNLOCKED_BOWS = "unlocked_bows"
        private const val KEY_UNLOCKED_TRAILS = "unlocked_trails"
        private const val KEY_IS_VIP = "is_vip"
        private const val KEY_IS_NO_ADS = "is_no_ads"
        private const val KEY_SOUND = "sound_enabled"
        private const val KEY_HAPTICS = "haptics_enabled"
        private const val KEY_STREAK_DAYS = "streak_days"
        private const val KEY_LAST_CLAIM_DATE = "last_claim_date"
        private const val KEY_LAST_CHEST_TIME = "last_chest_time"
    }
}
