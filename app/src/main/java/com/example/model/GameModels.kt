package com.example.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

enum class GameMode(val displayName: String, val description: String) {
    CAMPAIGN("Campaign", "Clear handcrafted stages, earn stars & unlock stages"),
    RUSH_ENDLESS("Endless Rush", "Descending target waves! Survive & claim the leaderboard"),
    DAILY_CHALLENGE("Daily Challenge", "Unique daily challenge with 3x coins & gem prizes")
}

enum class TargetType {
    NORMAL,
    HARD,
    GOLD_BONUS,
    GEM_CRYSTAL,
    BOMB_TNT,
    PIERCE_ARROW,
    SPLIT_ARROW
}

data class Target(
    val id: String,
    val row: Int,
    val col: Int,
    var x: Float,
    var y: Float,
    val width: Float,
    val height: Float,
    val maxHp: Int,
    var currentHp: Int,
    val type: TargetType,
    var isDestroyed: Boolean = false,
    var hitFlash: Float = 0f
)

data class Arrow(
    val id: Long,
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val radius: Float = 9f,
    var isActive: Boolean = true,
    var pierceCount: Int = 0,
    var trail: MutableList<Offset> = mutableListOf()
)

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    var alpha: Float = 1f,
    var life: Float = 1f,
    val size: Float = 6f
)

data class FloatingText(
    val id: Long,
    val text: String,
    var x: Float,
    var y: Float,
    var alpha: Float = 1f,
    val color: Color
)

data class BowSkin(
    val id: String,
    val name: String,
    val description: String,
    val priceCoins: Int,
    val priceGems: Int,
    val isVipExclusive: Boolean = false,
    val powerMultiplier: Float = 1.0f,
    val color: Color
)

data class TrailSkin(
    val id: String,
    val name: String,
    val priceCoins: Int,
    val color: Color
)

data class DailyQuest(
    val id: String,
    val title: String,
    val description: String,
    val currentProgress: Int,
    val targetProgress: Int,
    val rewardCoins: Int,
    val rewardGems: Int,
    val isClaimed: Boolean = false,
    val type: QuestType
)

enum class QuestType {
    BREAK_TARGETS,
    ACHIEVE_COMBO,
    PLAY_GAMES,
    TRIGGER_TNT,
    COLLECT_COINS
}

data class LeaderboardEntry(
    val rank: Int,
    val username: String,
    val score: Int,
    val league: String,
    val badge: String,
    val isPlayer: Boolean = false
)

data class ShopPackage(
    val id: String,
    val title: String,
    val subtitle: String,
    val priceUsd: String,
    val coins: Int,
    val gems: Int,
    val isPopular: Boolean = false,
    val isBestDeal: Boolean = false,
    val hasNoAds: Boolean = false
)
