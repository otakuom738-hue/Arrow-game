package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.EconomyRepository
import com.example.data.GamePreferences
import com.example.data.QuestRepository
import com.example.game.ArrowBreakEngine
import com.example.game.GameFeedback
import com.example.model.GameMode
import com.example.model.QuestType
import com.example.model.TargetType
import com.example.ui.components.RewardedAdDialog
import com.example.ui.theme.CoinGold
import com.example.ui.theme.GameBgDark
import com.example.ui.theme.GameSurfaceElevated
import com.example.ui.theme.GemGreen
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun GamePlayScreen(
    mode: GameMode,
    levelNumber: Int,
    prefs: GamePreferences,
    economyRepo: EconomyRepository,
    questRepo: QuestRepository,
    onExitGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val feedback = remember { GameFeedback(context) }

    val activeBow = remember {
        economyRepo.bowSkins.find { it.id == prefs.selectedBowId } ?: economyRepo.bowSkins.first()
    }

    val engine = remember {
        ArrowBreakEngine(
            mode = mode,
            levelNumber = levelNumber,
            bowPowerMultiplier = activeBow.powerMultiplier,
            bowColor = activeBow.color
        )
    }

    var frameTick by remember { mutableStateOf(0L) }
    var showReviveAdModal by remember { mutableStateOf(false) }
    var showDoubleRewardAdModal by remember { mutableStateOf(false) }
    var isDoubleRewarded by remember { mutableStateOf(false) }
    var showQuitConfirm by remember { mutableStateOf(false) }

    BackHandler {
        showQuitConfirm = true
    }

    // Continuous physics game loop
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { time ->
                frameTick = time
                engine.updatePhysics(
                    onHitTarget = {
                        feedback.hitFeedback(prefs.soundEnabled, prefs.hapticsEnabled)
                        questRepo.recordProgress(QuestType.BREAK_TARGETS, 1)
                        if (engine.comboCount >= 4) {
                            questRepo.recordProgress(QuestType.ACHIEVE_COMBO, 1)
                        }
                    },
                    onExplode = {
                        feedback.explodeFeedback(prefs.soundEnabled, prefs.hapticsEnabled)
                        questRepo.recordProgress(QuestType.TRIGGER_TNT, 1)
                    }
                )
            }
        }
    }

    // Handle Victory or Game Over persistence
    LaunchedEffect(engine.isVictory) {
        if (engine.isVictory) {
            feedback.victoryFeedback(prefs.soundEnabled)
            prefs.coins += engine.coinsEarned + 100
            prefs.gems += engine.gemsEarned
            if (engine.score > prefs.highestScore) {
                prefs.highestScore = engine.score
            }
            if (mode == GameMode.CAMPAIGN && levelNumber >= prefs.campaignLevel) {
                prefs.campaignLevel = levelNumber + 1
            }
            questRepo.recordProgress(QuestType.PLAY_GAMES, 1)
        }
    }

    LaunchedEffect(engine.isGameOver) {
        if (engine.isGameOver) {
            prefs.coins += engine.coinsEarned
            prefs.gems += engine.gemsEarned
            if (engine.score > prefs.highestScore) {
                prefs.highestScore = engine.score
            }
            questRepo.recordProgress(QuestType.PLAY_GAMES, 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GameBgDark)
    ) {
        // Main Game Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            engine.startAim(offset)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            engine.updateAim(change.position)
                        },
                        onDragEnd = {
                            engine.releaseAim {
                                feedback.hitFeedback(prefs.soundEnabled, prefs.hapticsEnabled)
                            }
                        },
                        onDragCancel = {
                            engine.isAiming = false
                        }
                    )
                }
        ) {
            engine.initDimensions(size.width, size.height)

            // Background subtle grid effect
            val gridSpacing = 60f
            var gx = 0f
            while (gx < size.width) {
                drawLine(
                    color = Color(0x0CFFFFFF),
                    start = Offset(gx, 0f),
                    end = Offset(gx, size.height),
                    strokeWidth = 1f
                )
                gx += gridSpacing
            }
            var gy = 0f
            while (gy < size.height) {
                drawLine(
                    color = Color(0x0CFFFFFF),
                    start = Offset(0f, gy),
                    end = Offset(size.width, gy),
                    strokeWidth = 1f
                )
                gy += gridSpacing
            }

            // Draw Danger Line in Endless Rush Mode
            if (mode == GameMode.RUSH_ENDLESS) {
                val dangerY = size.height - 140f
                drawLine(
                    color = Color(0x55FF2A6D),
                    start = Offset(0f, dangerY),
                    end = Offset(size.width, dangerY),
                    strokeWidth = 3f
                )
            }

            // Draw Targets
            val activeTargets = engine.targets.filter { !it.isDestroyed }
            for (target in activeTargets) {
                val baseColor = engine.getTargetColor(target.type)
                val targetFill = if (target.hitFlash > 0f) Color.White else baseColor.copy(alpha = 0.28f)

                drawRoundRect(
                    color = targetFill,
                    topLeft = Offset(target.x, target.y),
                    size = Size(target.width, target.height),
                    cornerRadius = CornerRadius(10f, 10f)
                )

                drawRoundRect(
                    color = if (target.hitFlash > 0f) Color.White else baseColor,
                    topLeft = Offset(target.x, target.y),
                    size = Size(target.width, target.height),
                    cornerRadius = CornerRadius(10f, 10f),
                    style = Stroke(width = 2.5f)
                )

                // Label inside target
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 28f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = true
                    }
                    val label = when (target.type) {
                        TargetType.BOMB_TNT -> "💣"
                        TargetType.GOLD_BONUS -> "🪙"
                        TargetType.GEM_CRYSTAL -> "💎"
                        else -> "${target.currentHp}"
                    }
                    drawText(
                        label,
                        target.x + target.width / 2f,
                        target.y + target.height / 2f + 10f,
                        paint
                    )
                }
            }

            // Draw Flying Arrows
            val activeArrows = engine.arrows.filter { it.isActive }
            for (arrow in activeArrows) {
                // Glow trail
                for (i in 0 until arrow.trail.size - 1) {
                    val p1 = arrow.trail[i]
                    val p2 = arrow.trail[i + 1]
                    val alpha = (i.toFloat() / arrow.trail.size) * 0.7f
                    drawLine(
                        color = activeBow.color.copy(alpha = alpha),
                        start = p1,
                        end = p2,
                        strokeWidth = (i * 1.5f).coerceAtLeast(2f)
                    )
                }

                // Arrow Head
                drawCircle(
                    color = Color.White,
                    radius = arrow.radius,
                    center = Offset(arrow.x, arrow.y)
                )
                drawCircle(
                    color = activeBow.color,
                    radius = arrow.radius + 3f,
                    center = Offset(arrow.x, arrow.y),
                    style = Stroke(width = 2f)
                )
            }

            // Draw Aiming trajectory indicator
            if (engine.isAiming) {
                val launcherPos = Offset(size.width / 2f, size.height - 90f)
                val dx = engine.aimCurrent.x - launcherPos.x
                val dy = engine.aimCurrent.y - launcherPos.y

                if (dy < -20f) {
                    val length = kotlin.math.sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
                    val dirX = dx / length
                    val dirY = dy / length

                    var currX = launcherPos.x
                    var currY = launcherPos.y

                    for (step in 1..28) {
                        currX += dirX * 22f
                        currY += dirY * 22f

                        // Ricochet visualization
                        if (currX <= 0f || currX >= size.width) {
                            break
                        }
                        if (currY <= 80f) {
                            break
                        }

                        val dotAlpha = 1f - (step / 30f)
                        drawCircle(
                            color = activeBow.color.copy(alpha = dotAlpha),
                            radius = if (step % 3 == 0) 4f else 2.5f,
                            center = Offset(currX, currY)
                        )
                    }
                }
            }

            // Draw Bow Launcher at Bottom Center
            val launcherX = size.width / 2f
            val launcherY = size.height - 90f

            drawCircle(
                color = activeBow.color.copy(alpha = 0.2f),
                radius = 36f,
                center = Offset(launcherX, launcherY)
            )
            drawCircle(
                color = activeBow.color,
                radius = 28f,
                center = Offset(launcherX, launcherY),
                style = Stroke(width = 3.5f)
            )
            drawCircle(
                color = Color.White,
                radius = 12f,
                center = Offset(launcherX, launcherY)
            )

            // Draw Particles
            for (p in engine.particles) {
                drawCircle(
                    color = p.color.copy(alpha = p.alpha),
                    radius = p.size * p.alpha,
                    center = Offset(p.x, p.y)
                )
            }

            // Draw Floating Score Texts
            for (ft in engine.floatingTexts) {
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.argb(
                            (ft.alpha * 255).toInt().coerceIn(0, 255),
                            (ft.color.red * 255).toInt(),
                            (ft.color.green * 255).toInt(),
                            (ft.color.blue * 255).toInt()
                        )
                        textSize = 34f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = true
                    }
                    drawText(ft.text, ft.x, ft.y, paint)
                }
            }
        }

        // Top Game HUD Overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { showQuitConfirm = true },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(GameSurfaceElevated)
                    .testTag("game_back_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${engine.score}",
                    color = NeonCyan,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = when (mode) {
                        GameMode.CAMPAIGN -> "STAGE $levelNumber"
                        GameMode.RUSH_ENDLESS -> "RUSH WAVE"
                        GameMode.DAILY_CHALLENGE -> "DAILY CHALLENGE"
                    },
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Ammo / Arrows remaining badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(GameSurfaceElevated)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🏹",
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (mode == GameMode.RUSH_ENDLESS) "∞" else "${engine.arrowsRemaining}",
                        color = if (engine.arrowsRemaining <= 3 && mode != GameMode.RUSH_ENDLESS) NeonAmber else TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Combo Float Banner
        if (engine.comboCount >= 2 && engine.isFiring) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NeonGold.copy(alpha = 0.25f))
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "COMBO ${engine.comboCount}X!",
                    color = NeonGold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Bottom Aim Guide Helper
        if (!engine.isFiring && !engine.isGameOver && !engine.isVictory) {
            Text(
                text = "DRAG & AIM UPWARDS TO SHOOT",
                color = TextSecondary.copy(alpha = 0.65f),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 30.dp)
            )
        }

        // VICTORY DIALOG
        AnimatedVisibility(
            visible = engine.isVictory,
            enter = fadeIn() + scaleIn(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .clip(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = GameBgDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Victory",
                        tint = NeonGold,
                        modifier = Modifier.size(60.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "VICTORY!",
                        color = NeonGold,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "Targets successfully annihilated!",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "SCORE: ${engine.score}",
                        color = NeonCyan,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = CoinGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+${if (isDoubleRewarded) (engine.coinsEarned + 100) * 2 else (engine.coinsEarned + 100)}",
                                color = CoinGold,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (engine.gemsEarned > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Diamond, contentDescription = null, tint = GemGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "+${engine.gemsEarned}",
                                    color = GemGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // REVENUE: Double rewards with rewarded ad!
                    if (!isDoubleRewarded) {
                        Button(
                            onClick = { showDoubleRewardAdModal = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonAmber,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("double_reward_ad_button")
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WATCH AD FOR 2X REWARDS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Button(
                        onClick = {
                            if (mode == GameMode.CAMPAIGN) {
                                engine.setupGrid()
                            } else {
                                onExitGame()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("victory_continue_button")
                    ) {
                        Text(
                            text = if (mode == GameMode.CAMPAIGN) "NEXT STAGE" else "CONTINUE",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onExitGame,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("BACK TO LOBBY", color = TextSecondary)
                    }
                }
            }
        }

        // GAME OVER DIALOG
        AnimatedVisibility(
            visible = engine.isGameOver,
            enter = fadeIn() + scaleIn(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .clip(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = GameBgDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "GAME OVER",
                        color = Color(0xFFFF3366),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "You ran out of arrows!",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "FINAL SCORE: ${engine.score}",
                        color = NeonCyan,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // REVENUE: Revive with Rewarded Ad (Second Chance)
                    Button(
                        onClick = { showReviveAdModal = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonGreen,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("revive_ad_button")
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("REVIVE WITH AD (+5 ARROWS)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Revive with Gems
                    if (prefs.gems >= 15) {
                        OutlinedButton(
                            onClick = {
                                prefs.gems -= 15
                                engine.reviveWithBonus()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("revive_gems_button")
                        ) {
                            Icon(Icons.Default.Diamond, contentDescription = null, tint = GemGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("REVIVE FOR 15 GEMS", color = GemGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Retry button
                    Button(
                        onClick = { engine.setupGrid() },
                        colors = ButtonDefaults.buttonColors(containerColor = GameSurfaceElevated, contentColor = TextPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("restart_game_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("TRY AGAIN", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onExitGame,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("LOBBY", color = TextSecondary)
                    }
                }
            }
        }
    }

    // Rewarded Ad Dialogs
    if (showReviveAdModal) {
        RewardedAdDialog(
            rewardTitle = "Revive your match with +5 Bonus Arrows!",
            onRewardEarned = {
                engine.reviveWithBonus()
            },
            onDismiss = { showReviveAdModal = false }
        )
    }

    if (showDoubleRewardAdModal) {
        RewardedAdDialog(
            rewardTitle = "Double your coins and gems bonus!",
            onRewardEarned = {
                isDoubleRewarded = true
                prefs.coins += (engine.coinsEarned + 100)
            },
            onDismiss = { showDoubleRewardAdModal = false }
        )
    }

    // Quit Confirmation Dialog
    if (showQuitConfirm) {
        Dialog(onDismissRequest = { showQuitConfirm = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = GameBgDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "LEAVE MATCH?",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Current match progress will be saved.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { showQuitConfirm = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("STAY", color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                showQuitConfirm = false
                                onExitGame()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("LEAVE", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
