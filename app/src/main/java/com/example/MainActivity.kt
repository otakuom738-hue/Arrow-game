package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.window.Dialog
import com.example.data.EconomyRepository
import com.example.data.GamePreferences
import com.example.data.LeaderboardRepository
import com.example.data.QuestRepository
import com.example.model.GameMode
import com.example.ui.components.ProfileDialog
import com.example.ui.components.RewardedAdDialog
import com.example.ui.components.TopCurrencyBar
import com.example.ui.screens.DailyChallengesScreen
import com.example.ui.screens.GamePlayScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.theme.GameBgDark
import com.example.ui.theme.GameSurfaceDark
import com.example.ui.theme.GameSurfaceElevated
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class AppNavTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    HOME("Battle", Icons.Default.SportsEsports),
    LEADERBOARD("Ranks", Icons.Default.EmojiEvents),
    DAILY("Quests", Icons.Default.FlashOn),
    SHOP("Store", Icons.Default.ShoppingBag)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = GamePreferences(this)
        val economyRepo = EconomyRepository(prefs)
        val leaderboardRepo = LeaderboardRepository(prefs)
        val questRepo = QuestRepository(prefs)

        setContent {
            MyApplicationTheme {
                MainAppContainer(
                    prefs = prefs,
                    economyRepo = economyRepo,
                    leaderboardRepo = leaderboardRepo,
                    questRepo = questRepo
                )
            }
        }
    }
}

@Composable
fun MainAppContainer(
    prefs: GamePreferences,
    economyRepo: EconomyRepository,
    leaderboardRepo: LeaderboardRepository,
    questRepo: QuestRepository
) {
    var currentTab by remember { mutableStateOf(AppNavTab.HOME) }
    var inActiveGame by remember { mutableStateOf(false) }
    var activeGameMode by remember { mutableStateOf(GameMode.RUSH_ENDLESS) }
    var activeGameLevel by remember { mutableIntStateOf(1) }

    var showProfileDialog by remember { mutableStateOf(false) }
    var showOutOfEnergyDialog by remember { mutableStateOf(false) }
    var showEnergyAdModal by remember { mutableStateOf(false) }
    var currencyRefreshTick by remember { mutableIntStateOf(0) }

    fun refreshCurrency() {
        currencyRefreshTick++
    }

    if (inActiveGame) {
        GamePlayScreen(
            mode = activeGameMode,
            levelNumber = activeGameLevel,
            prefs = prefs,
            economyRepo = economyRepo,
            questRepo = questRepo,
            onExitGame = {
                inActiveGame = false
                refreshCurrency()
            }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopCurrencyBar(
                    coins = prefs.coins,
                    gems = prefs.gems,
                    energy = prefs.energy,
                    soundEnabled = prefs.soundEnabled,
                    onToggleSound = {
                        prefs.soundEnabled = !prefs.soundEnabled
                        refreshCurrency()
                    },
                    onOpenShop = {
                        currentTab = AppNavTab.SHOP
                    },
                    onOpenProfile = {
                        showProfileDialog = true
                    }
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = GameSurfaceDark,
                    contentColor = NeonCyan
                ) {
                    AppNavTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = currentTab == tab,
                            onClick = { currentTab = tab },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (currentTab == tab) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NeonCyan,
                                selectedTextColor = NeonCyan,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = GameSurfaceElevated
                            ),
                            modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                        )
                    }
                }
            },
            containerColor = GameBgDark
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tab_content"
                ) { tab ->
                    when (tab) {
                        AppNavTab.HOME -> {
                            HomeScreen(
                                prefs = prefs,
                                economyRepo = economyRepo,
                                leaderboardRepo = leaderboardRepo,
                                onStartGame = { mode, lvl ->
                                    if (prefs.energy > 0 || prefs.isVip) {
                                        if (!prefs.isVip) {
                                            prefs.energy--
                                        }
                                        activeGameMode = mode
                                        activeGameLevel = lvl
                                        inActiveGame = true
                                    } else {
                                        showOutOfEnergyDialog = true
                                    }
                                },
                                onOpenLeaderboard = { currentTab = AppNavTab.LEADERBOARD },
                                onOpenShop = { currentTab = AppNavTab.SHOP }
                            )
                        }
                        AppNavTab.LEADERBOARD -> {
                            LeaderboardScreen(leaderboardRepo = leaderboardRepo)
                        }
                        AppNavTab.DAILY -> {
                            DailyChallengesScreen(
                                prefs = prefs,
                                questRepo = questRepo
                            )
                        }
                        AppNavTab.SHOP -> {
                            ShopScreen(
                                prefs = prefs,
                                economyRepo = economyRepo
                            )
                        }
                    }
                }
            }
        }
    }

    // Out of Energy Dialog (Revenue opportunity: watch ad or spend gems)
    if (showOutOfEnergyDialog) {
        Dialog(onDismissRequest = { showOutOfEnergyDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp)),
                colors = CardDefaults.cardColors(containerColor = GameBgDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color(0xFFFF4D4D).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Energy",
                            tint = Color(0xFFFF4D4D),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "OUT OF ENERGY!",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Watch a quick video ad to refill 5/5 energy or use 15 gems.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Watch ad button
                    Button(
                        onClick = {
                            showOutOfEnergyDialog = false
                            showEnergyAdModal = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonAmber,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("refill_energy_ad_button")
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("WATCH AD FOR 5/5 REFILL", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Use gems button
                    if (prefs.gems >= 15) {
                        Button(
                            onClick = {
                                prefs.gems -= 15
                                prefs.energy = 5
                                showOutOfEnergyDialog = false
                                refreshCurrency()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonCyan,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Text("REFILL FOR 15 GEMS", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    OutlinedButton(
                        onClick = { showOutOfEnergyDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("CANCEL", color = TextSecondary)
                    }
                }
            }
        }
    }

    // Energy Refill Video Ad Modal
    if (showEnergyAdModal) {
        RewardedAdDialog(
            rewardTitle = "Instantly Refill Energy to 5/5!",
            onRewardEarned = {
                prefs.energy = 5
                refreshCurrency()
            },
            onDismiss = { showEnergyAdModal = false }
        )
    }

    // Profile Dialog
    if (showProfileDialog) {
        ProfileDialog(
            prefs = prefs,
            onNameUpdated = {
                refreshCurrency()
            },
            onDismiss = {
                showProfileDialog = false
                refreshCurrency()
            }
        )
    }
}
