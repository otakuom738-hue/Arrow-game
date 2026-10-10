package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AdRewardType
import com.example.data.EconomyRepository
import com.example.data.GamePreferences
import com.example.model.BowSkin
import com.example.model.ShopPackage
import com.example.model.TrailSkin
import com.example.ui.components.IapPurchaseDialog
import com.example.ui.components.RewardedAdDialog
import com.example.ui.theme.CoinGold
import com.example.ui.theme.GameBgDark
import com.example.ui.theme.GameSurfaceBright
import com.example.ui.theme.GameSurfaceDark
import com.example.ui.theme.GameSurfaceElevated
import com.example.ui.theme.GemGreen
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ShopScreen(
    prefs: GamePreferences,
    economyRepo: EconomyRepository,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var selectedShopTab by remember { mutableIntStateOf(0) }
    var refreshKey by remember { mutableIntStateOf(0) }

    var selectedIapPackage by remember { mutableStateOf<ShopPackage?>(null) }
    var activeAdReward by remember { mutableStateOf<AdRewardType?>(null) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GameBgDark)
    ) {
        // Tab Header: In-App Store & Armory Customization
        TabRow(
            selectedTabIndex = selectedShopTab,
            containerColor = GameBgDark,
            contentColor = NeonCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedShopTab]),
                    color = NeonCyan
                )
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Tab(
                selected = selectedShopTab == 0,
                onClick = { selectedShopTab = 0 },
                text = {
                    Text(
                        "STORE & IAP",
                        fontWeight = if (selectedShopTab == 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedShopTab == 0) NeonCyan else TextSecondary
                    )
                },
                modifier = Modifier.testTag("tab_store_iap")
            )
            Tab(
                selected = selectedShopTab == 1,
                onClick = { selectedShopTab = 1 },
                text = {
                    Text(
                        "BOW ARMORY",
                        fontWeight = if (selectedShopTab == 1) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedShopTab == 1) NeonCyan else TextSecondary
                    )
                },
                modifier = Modifier.testTag("tab_bow_armory")
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            if (selectedShopTab == 0) {
                // Section 1: REWARDED AD GENERATION (Watch & Earn)
                Text(
                    text = "REWARDED ADS (FREE EARNINGS)",
                    color = NeonAmber,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Ad Offer 1: Free Gems
                AdEarnCard(
                    title = "+100 Emerald Gems",
                    subtitle = "Watch a 5-second sponsor video to get free gems",
                    badge = "FREE",
                    icon = Icons.Default.Diamond,
                    iconTint = GemGreen,
                    onWatch = { activeAdReward = AdRewardType.GEMS_100 },
                    testTag = "watch_ad_gems_button"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Ad Offer 2: Free Coins
                AdEarnCard(
                    title = "+500 Gold Coins",
                    subtitle = "Instantly boost your coin bank for skin upgrades",
                    badge = "FREE",
                    icon = Icons.Default.MonetizationOn,
                    iconTint = CoinGold,
                    onWatch = { activeAdReward = AdRewardType.COINS_500 },
                    testTag = "watch_ad_coins_button"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Ad Offer 3: Instant Energy Refill
                AdEarnCard(
                    title = "Instant 5/5 Energy Refill",
                    subtitle = "Never wait for energy recharge timer",
                    badge = "REFILL",
                    icon = Icons.Default.Bolt,
                    iconTint = Color(0xFFFF4D4D),
                    onWatch = { activeAdReward = AdRewardType.ENERGY_REFILL },
                    testTag = "watch_ad_energy_button"
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Section 2: IN-APP PURCHASES & SPECIAL OFFERS
                Text(
                    text = "IN-APP PURCHASES & PACKAGES",
                    color = NeonGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                economyRepo.shopPackages.forEach { pkg ->
                    IapPackageCard(
                        pkg = pkg,
                        onBuy = { selectedIapPackage = pkg }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            } else {
                // ARMORY & WEAPON SKINS SECTION
                Text(
                    text = "CUSTOMIZE BOW ARSENAL",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                val unlockedBows = prefs.getUnlockedBows()

                economyRepo.bowSkins.forEach { skin ->
                    val isUnlocked = unlockedBows.contains(skin.id)
                    val isEquipped = prefs.selectedBowId == skin.id

                    BowSkinCard(
                        skin = skin,
                        isUnlocked = isUnlocked,
                        isEquipped = isEquipped,
                        onEquip = {
                            prefs.selectedBowId = skin.id
                            refreshKey++
                        },
                        onBuy = {
                            val success = economyRepo.buyBow(skin)
                            if (success) {
                                refreshKey++
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Trails Arsenal
                Text(
                    text = "ARROW TRAILS",
                    color = NeonAmber,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                val unlockedTrails = prefs.getUnlockedTrails()

                economyRepo.trailSkins.forEach { trail ->
                    val isUnlocked = unlockedTrails.contains(trail.id)
                    val isEquipped = prefs.selectedTrailId == trail.id

                    TrailSkinCard(
                        trail = trail,
                        isUnlocked = isUnlocked,
                        isEquipped = isEquipped,
                        onEquip = {
                            prefs.selectedTrailId = trail.id
                            refreshKey++
                        },
                        onBuy = {
                            val success = economyRepo.buyTrail(trail)
                            if (success) {
                                refreshKey++
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Rewarded Ad Dialog Trigger
    if (activeAdReward != null) {
        val reward = activeAdReward!!
        RewardedAdDialog(
            rewardTitle = when (reward) {
                AdRewardType.GEMS_100 -> "Reward: +100 Emerald Gems"
                AdRewardType.COINS_500 -> "Reward: +500 Gold Coins"
                AdRewardType.ENERGY_REFILL -> "Reward: Instant 5/5 Energy Refill"
                else -> "Reward Claimed"
            },
            onRewardEarned = {
                economyRepo.rewardFromAd(reward)
                refreshKey++
            },
            onDismiss = { activeAdReward = null }
        )
    }

    // Google Play IAP Dialog Trigger
    if (selectedIapPackage != null) {
        IapPurchaseDialog(
            pkg = selectedIapPackage!!,
            onPurchaseConfirmed = {
                economyRepo.processIapPurchase(selectedIapPackage!!)
                refreshKey++
            },
            onDismiss = { selectedIapPackage = null }
        )
    }
}

@Composable
private fun AdEarnCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    onWatch: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onWatch() }
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = GameSurfaceDark),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(NeonGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badge,
                                color = NeonGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = subtitle,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(NeonAmber)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("WATCH", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun IapPackageCard(
    pkg: ShopPackage,
    onBuy: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBuy() }
            .testTag("iap_card_${pkg.id}"),
        colors = CardDefaults.cardColors(containerColor = GameSurfaceDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = pkg.title,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (pkg.isPopular) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(NeonAmber.copy(alpha = 0.25f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("HOT", color = NeonAmber, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (pkg.isBestDeal) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(NeonGold.copy(alpha = 0.25f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("BEST DEAL", color = NeonGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(NeonCyan)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = pkg.priceUsd,
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = pkg.subtitle,
                color = TextSecondary,
                fontSize = 12.sp
            )

            if (pkg.coins > 0 || pkg.gems > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (pkg.coins > 0) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = CoinGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("+${pkg.coins}", color = CoinGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    if (pkg.gems > 0) {
                        Icon(Icons.Default.Diamond, contentDescription = null, tint = GemGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("+${pkg.gems}", color = GemGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun BowSkinCard(
    skin: BowSkin,
    isUnlocked: Boolean,
    isEquipped: Boolean,
    onEquip: () -> Unit,
    onBuy: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isEquipped) GameSurfaceElevated else GameSurfaceDark
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(skin.color.copy(alpha = 0.2f))
                        .border(1.5.dp, skin.color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏹", fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = skin.name,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = skin.description,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            when {
                isEquipped -> {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GameSurfaceBright)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("EQUIPPED", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                isUnlocked -> {
                    Button(
                        onClick = onEquip,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                        modifier = Modifier.testTag("equip_bow_${skin.id}")
                    ) {
                        Text("EQUIP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                skin.priceCoins > 0 -> {
                    Button(
                        onClick = onBuy,
                        colors = ButtonDefaults.buttonColors(containerColor = CoinGold, contentColor = Color.Black),
                        modifier = Modifier.testTag("buy_bow_${skin.id}")
                    ) {
                        Text("${skin.priceCoins} 🪙", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                skin.priceGems > 0 -> {
                    Button(
                        onClick = onBuy,
                        colors = ButtonDefaults.buttonColors(containerColor = GemGreen, contentColor = Color.Black),
                        modifier = Modifier.testTag("buy_bow_${skin.id}")
                    ) {
                        Text("${skin.priceGems} 💎", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                skin.isVipExclusive -> {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonGold.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("VIP PASS", color = NeonGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun TrailSkinCard(
    trail: TrailSkin,
    isUnlocked: Boolean,
    isEquipped: Boolean,
    onEquip: () -> Unit,
    onBuy: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isEquipped) GameSurfaceElevated else GameSurfaceDark
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(trail.color)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = trail.name,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (isEquipped) {
                Text("EQUIPPED", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            } else if (isUnlocked) {
                OutlinedButton(onClick = onEquip) {
                    Text("USE", fontSize = 10.sp)
                }
            } else {
                Button(
                    onClick = onBuy,
                    colors = ButtonDefaults.buttonColors(containerColor = CoinGold, contentColor = Color.Black)
                ) {
                    Text("${trail.priceCoins} 🪙", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
