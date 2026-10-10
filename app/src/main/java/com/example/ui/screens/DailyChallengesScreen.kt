package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.GamePreferences
import com.example.data.QuestRepository
import com.example.model.DailyQuest
import com.example.ui.components.MysteryChestDialog
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
fun DailyChallengesScreen(
    prefs: GamePreferences,
    questRepo: QuestRepository,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var refreshKey by remember { mutableIntStateOf(0) }
    val quests = remember(refreshKey) { questRepo.getDailyQuests() }

    var chestLoot by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var streakClaimedReward by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var showChestAdModal by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GameBgDark)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Daily Free Mystery Chest Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = GameSurfaceDark),
            shape = RoundedCornerShape(18.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(NeonGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = "Loot Chest",
                            tint = NeonGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Daily Mystery Chest",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Free random Coins & Gems",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Button(
                    onClick = {
                        val loot = questRepo.claimFreeChest()
                        chestLoot = loot
                        refreshKey++
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGold,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.testTag("open_free_chest_button")
                ) {
                    Text("OPEN", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 7-Day Login Streak Section
        Text(
            text = "7-DAY ARCHER STREAK",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = GameSurfaceDark),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Day ${prefs.streakDays} of 7 Streak",
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Log in daily for scaling coin multipliers",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = {
                            val reward = questRepo.claimDailyStreak()
                            if (reward != null) {
                                streakClaimedReward = reward
                                refreshKey++
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.testTag("claim_streak_button")
                    ) {
                        Text("CLAIM TODAY", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 7 days row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (day in 1..7) {
                        val isCurrent = day == prefs.streakDays
                        val isPassed = day < prefs.streakDays
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 2.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        isCurrent -> NeonCyan.copy(alpha = 0.25f)
                                        isPassed -> GameSurfaceElevated
                                        else -> GameSurfaceBright
                                    }
                                )
                                .border(
                                    width = if (isCurrent) 1.5.dp else 0.dp,
                                    color = if (isCurrent) NeonCyan else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "D$day",
                                    color = if (isCurrent) NeonCyan else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = when (day) {
                                        7 -> "🎁"
                                        in 4..6 -> "💎"
                                        else -> "🪙"
                                    },
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Daily Quests Heading
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DAILY MISSIONS",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            // Watch ad for instant chest reload
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(GameSurfaceElevated)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.Videocam, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Bonus Chest Ad",
                    color = NeonAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("bonus_ad_chip")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quests List
        quests.forEach { quest ->
            QuestCard(
                quest = quest,
                onClaim = {
                    questRepo.claimQuest(quest.id)
                    refreshKey++
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Chest Loot Dialog
    if (chestLoot != null) {
        MysteryChestDialog(
            coins = chestLoot!!.first,
            gems = chestLoot!!.second,
            onDismiss = { chestLoot = null }
        )
    }

    if (streakClaimedReward != null) {
        MysteryChestDialog(
            coins = streakClaimedReward!!.first,
            gems = streakClaimedReward!!.second,
            onDismiss = { streakClaimedReward = null }
        )
    }

    if (showChestAdModal) {
        RewardedAdDialog(
            rewardTitle = "Unlock an extra Mystery Loot Chest!",
            onRewardEarned = {
                chestLoot = questRepo.claimFreeChest()
                refreshKey++
            },
            onDismiss = { showChestAdModal = false }
        )
    }
}

@Composable
private fun QuestCard(
    quest: DailyQuest,
    onClaim: () -> Unit
) {
    val isComplete = quest.currentProgress >= quest.targetProgress
    val progress = (quest.currentProgress.toFloat() / quest.targetProgress).coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GameSurfaceDark),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = quest.title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = quest.description,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Rewards label
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = CoinGold, modifier = Modifier.size(14.dp))
                    Text("+${quest.rewardCoins}", color = CoinGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.Diamond, contentDescription = null, tint = GemGreen, modifier = Modifier.size(14.dp))
                    Text("+${quest.rewardGems}", color = GemGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Progress",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "${quest.currentProgress}/${quest.targetProgress}",
                            color = if (isComplete) NeonGreen else TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (isComplete) NeonGreen else NeonCyan,
                        trackColor = GameSurfaceElevated
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Button(
                    onClick = onClaim,
                    enabled = isComplete && !quest.isClaimed,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (quest.isClaimed) GameSurfaceElevated else NeonGreen,
                        contentColor = if (quest.isClaimed) TextSecondary else Color.Black,
                        disabledContainerColor = GameSurfaceElevated,
                        disabledContentColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("claim_quest_${quest.id}")
                ) {
                    if (quest.isClaimed) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("CLAIMED", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    } else if (isComplete) {
                        Text("CLAIM", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                    } else {
                        Text("LOCKED", fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
