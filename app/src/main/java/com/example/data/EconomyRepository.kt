package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.model.BowSkin
import com.example.model.ShopPackage
import com.example.model.TrailSkin
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple

class EconomyRepository(private val prefs: GamePreferences) {

    val bowSkins = listOf(
        BowSkin(
            id = "wooden_recurve",
            name = "Recurve Bow",
            description = "Standard balanced archery bow. Clean and reliable.",
            priceCoins = 0,
            priceGems = 0,
            powerMultiplier = 1.0f,
            color = Color(0xFFD4A373)
        ),
        BowSkin(
            id = "neon_cyber",
            name = "Neon Cyber Bow",
            description = "+15% arrow velocity & bright electric hit trails.",
            priceCoins = 1500,
            priceGems = 0,
            powerMultiplier = 1.15f,
            color = NeonCyan
        ),
        BowSkin(
            id = "dragon_blaze",
            name = "Dragon Blaze",
            description = "+25% explosive blast radius & burns surrounding blocks.",
            priceCoins = 3500,
            priceGems = 0,
            powerMultiplier = 1.25f,
            color = Color(0xFFFF5722)
        ),
        BowSkin(
            id = "quantum_void",
            name = "Quantum Void",
            description = "Arrows slice through 1 extra block with quantum pierce.",
            priceCoins = 0,
            priceGems = 200,
            powerMultiplier = 1.4f,
            color = NeonPurple
        ),
        BowSkin(
            id = "golden_sovereign",
            name = "Golden Sovereign",
            description = "+50% gold coin drop rate & regal victory shockwave.",
            priceCoins = 0,
            priceGems = 500,
            isVipExclusive = true,
            powerMultiplier = 1.6f,
            color = NeonGold
        )
    )

    val trailSkins = listOf(
        TrailSkin(id = "classic", name = "Cyan Pulse", priceCoins = 0, color = NeonCyan),
        TrailSkin(id = "lightning", name = "Electric Spark", priceCoins = 800, color = NeonAmber),
        TrailSkin(id = "emerald", name = "Jade Comet", priceCoins = 1400, color = NeonGreen),
        TrailSkin(id = "inferno", name = "Solar Flare", priceCoins = 2200, color = Color(0xFFFF2A6D))
    )

    val shopPackages = listOf(
        ShopPackage(
            id = "pkg_starter",
            title = "Elite Starter Pack",
            subtitle = "5,000 Coins + 150 Gems + Neon Cyber Bow!",
            priceUsd = "$0.99",
            coins = 5000,
            gems = 150,
            isPopular = true
        ),
        ShopPackage(
            id = "pkg_no_ads",
            title = "VIP Ad-Free Pass",
            subtitle = "Permanently remove all ads + VIP badge + 100 Gems bonus",
            priceUsd = "$1.99",
            coins = 2000,
            gems = 100,
            hasNoAds = true
        ),
        ShopPackage(
            id = "pkg_pro_pass",
            title = "Pro Archer Season Pass",
            subtitle = "Golden Sovereign Bow + Infinite Energy + 350 Gems",
            priceUsd = "$4.99",
            coins = 15000,
            gems = 350,
            isBestDeal = true
        ),
        ShopPackage(
            id = "pkg_gem_pouch",
            title = "Pouch of Gems",
            subtitle = "120 Emerald Gems for revives & unlocks",
            priceUsd = "$0.99",
            coins = 0,
            gems = 120
        ),
        ShopPackage(
            id = "pkg_gem_vault",
            title = "Vault of Gems",
            subtitle = "850 Emerald Gems (Best Value Bundle)",
            priceUsd = "$4.99",
            coins = 0,
            gems = 850,
            isBestDeal = true
        ),
        ShopPackage(
            id = "pkg_coin_wagon",
            title = "Wagon of Coins",
            subtitle = "35,000 Gold Coins for armory skins",
            priceUsd = "$2.99",
            coins = 35000,
            gems = 0
        )
    )

    fun buyBow(skin: BowSkin): Boolean {
        val unlocked = prefs.getUnlockedBows()
        if (unlocked.contains(skin.id)) {
            prefs.selectedBowId = skin.id
            return true
        }

        if (skin.priceCoins > 0 && prefs.coins >= skin.priceCoins) {
            prefs.coins -= skin.priceCoins
            prefs.unlockBow(skin.id)
            prefs.selectedBowId = skin.id
            return true
        }

        if (skin.priceGems > 0 && prefs.gems >= skin.priceGems) {
            prefs.gems -= skin.priceGems
            prefs.unlockBow(skin.id)
            prefs.selectedBowId = skin.id
            return true
        }

        return false
    }

    fun buyTrail(trail: TrailSkin): Boolean {
        val unlocked = prefs.getUnlockedTrails()
        if (unlocked.contains(trail.id)) {
            prefs.selectedTrailId = trail.id
            return true
        }

        if (trail.priceCoins > 0 && prefs.coins >= trail.priceCoins) {
            prefs.coins -= trail.priceCoins
            prefs.unlockTrail(trail.id)
            prefs.selectedTrailId = trail.id
            return true
        }

        return false
    }

    fun processIapPurchase(pkg: ShopPackage): Boolean {
        prefs.coins += pkg.coins
        prefs.gems += pkg.gems
        if (pkg.hasNoAds) {
            prefs.isNoAds = true
        }
        if (pkg.id == "pkg_starter") {
            prefs.unlockBow("neon_cyber")
        }
        if (pkg.id == "pkg_pro_pass") {
            prefs.isVip = true
            prefs.unlockBow("golden_sovereign")
            prefs.energy = 5
        }
        return true
    }

    fun rewardFromAd(rewardType: AdRewardType): String {
        return when (rewardType) {
            AdRewardType.GEMS_100 -> {
                prefs.gems += 100
                "+100 Gems added to your vault!"
            }
            AdRewardType.COINS_500 -> {
                prefs.coins += 500
                "+500 Coins added to your pouch!"
            }
            AdRewardType.ENERGY_REFILL -> {
                prefs.energy = 5
                "Energy completely refilled to 5/5!"
            }
            AdRewardType.REVIVE -> {
                "Revived with 3 Bonus Arrows!"
            }
            AdRewardType.DOUBLE_MATCH -> {
                "Match Coins Doubled!"
            }
        }
    }
}

enum class AdRewardType {
    GEMS_100,
    COINS_500,
    ENERGY_REFILL,
    REVIVE,
    DOUBLE_MATCH
}
