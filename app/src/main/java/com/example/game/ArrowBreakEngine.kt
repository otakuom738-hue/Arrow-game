package com.example.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.model.Arrow
import com.example.model.FloatingText
import com.example.model.GameMode
import com.example.model.Particle
import com.example.model.Target
import com.example.model.TargetType
import com.example.ui.theme.CoinGold
import com.example.ui.theme.GemGreen
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class ArrowBreakEngine(
    val mode: GameMode,
    val levelNumber: Int = 1,
    var bowPowerMultiplier: Float = 1.0f,
    var bowColor: Color = NeonCyan
) {
    var width: Float = 1000f
    var height: Float = 1800f

    val targets = mutableListOf<Target>()
    val arrows = mutableListOf<Arrow>()
    val particles = mutableListOf<Particle>()
    val floatingTexts = mutableListOf<FloatingText>()

    var score: Int = 0
    var coinsEarned: Int = 0
    var gemsEarned: Int = 0
    var targetsDestroyedCount: Int = 0
    var comboCount: Int = 0
    var maxComboInGame: Int = 0
    var tntTriggeredCount: Int = 0

    var arrowsRemaining: Int = when (mode) {
        GameMode.CAMPAIGN -> 12 + (levelNumber * 2)
        GameMode.RUSH_ENDLESS -> 9999
        GameMode.DAILY_CHALLENGE -> 15
    }

    var isAiming: Boolean = false
    var aimStart: Offset = Offset.Zero
    var aimCurrent: Offset = Offset.Zero
    var isFiring: Boolean = false
    var isGameOver: Boolean = false
    var isVictory: Boolean = false

    private var nextArrowId: Long = 1
    private var nextTextId: Long = 1

    fun initDimensions(w: Float, h: Float) {
        width = w
        height = h
        if (targets.isEmpty()) {
            setupGrid()
        }
    }

    fun setupGrid() {
        targets.clear()
        arrows.clear()
        particles.clear()
        floatingTexts.clear()
        score = 0
        coinsEarned = 0
        gemsEarned = 0
        targetsDestroyedCount = 0
        comboCount = 0
        maxComboInGame = 0
        tntTriggeredCount = 0
        isGameOver = false
        isVictory = false
        isFiring = false

        val cols = 7
        val marginX = 24f
        val topMargin = 120f
        val cellW = (width - marginX * 2) / cols
        val cellH = 56f

        when (mode) {
            GameMode.CAMPAIGN -> {
                val rows = (4 + (levelNumber % 5)).coerceAtMost(8)
                val baseHp = 1 + (levelNumber / 2)
                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        // Create engaging geometric patterns
                        val include = when ((levelNumber + r + c) % 4) {
                            0, 1 -> true
                            2 -> r > 1
                            else -> c in 1..5
                        }
                        if (include) {
                            val type = when {
                                (r == 0 && c == 3) || (r == 2 && c == 2 && levelNumber > 2) -> TargetType.BOMB_TNT
                                (r + c) % 7 == 0 -> TargetType.GOLD_BONUS
                                (r == 1 && c == 5 && levelNumber % 3 == 0) -> TargetType.GEM_CRYSTAL
                                r == rows - 1 -> TargetType.HARD
                                else -> TargetType.NORMAL
                            }
                            val hp = when (type) {
                                TargetType.HARD -> baseHp * 2
                                TargetType.BOMB_TNT -> 1
                                TargetType.GOLD_BONUS -> 2
                                TargetType.GEM_CRYSTAL -> 3
                                else -> baseHp
                            }
                            val target = Target(
                                id = "t_${r}_${c}",
                                row = r,
                                col = c,
                                x = marginX + c * cellW + 3f,
                                y = topMargin + r * (cellH + 8f),
                                width = cellW - 6f,
                                height = cellH,
                                maxHp = hp,
                                currentHp = hp,
                                type = type
                            )
                            targets.add(target)
                        }
                    }
                }
            }
            GameMode.RUSH_ENDLESS -> {
                val initialRows = 4
                for (r in 0 until initialRows) {
                    spawnRushRow(r, cols, marginX, topMargin, cellW, cellH, roundMultiplier = 1)
                }
            }
            GameMode.DAILY_CHALLENGE -> {
                val rows = 6
                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        val isBomb = (r + c) % 3 == 0
                        val type = if (isBomb) TargetType.BOMB_TNT else if ((r + c) % 5 == 0) TargetType.GOLD_BONUS else TargetType.NORMAL
                        val hp = if (type == TargetType.BOMB_TNT) 1 else 4
                        val target = Target(
                            id = "daily_${r}_${c}",
                            row = r,
                            col = c,
                            x = marginX + c * cellW + 3f,
                            y = topMargin + r * (cellH + 8f),
                            width = cellW - 6f,
                            height = cellH,
                            maxHp = hp,
                            currentHp = hp,
                            type = type
                        )
                        targets.add(target)
                    }
                }
            }
        }
    }

    private fun spawnRushRow(rowIdx: Int, cols: Int, marginX: Float, topMargin: Float, cellW: Float, cellH: Float, roundMultiplier: Int) {
        for (c in 0 until cols) {
            if (Random.nextFloat() > 0.25f) {
                val type = when {
                    Random.nextFloat() < 0.12f -> TargetType.BOMB_TNT
                    Random.nextFloat() < 0.15f -> TargetType.GOLD_BONUS
                    Random.nextFloat() < 0.05f -> TargetType.GEM_CRYSTAL
                    Random.nextFloat() < 0.2f -> TargetType.HARD
                    else -> TargetType.NORMAL
                }
                val hp = when (type) {
                    TargetType.BOMB_TNT -> 1
                    TargetType.HARD -> (roundMultiplier * 2) + 2
                    else -> roundMultiplier + 1
                }
                targets.add(
                    Target(
                        id = "rush_${System.currentTimeMillis()}_${c}",
                        row = rowIdx,
                        col = c,
                        x = marginX + c * cellW + 3f,
                        y = topMargin + rowIdx * (cellH + 8f),
                        width = cellW - 6f,
                        height = cellH,
                        maxHp = hp,
                        currentHp = hp,
                        type = type
                    )
                )
            }
        }
    }

    fun startAim(start: Offset) {
        if (isFiring || isGameOver || isVictory) return
        isAiming = true
        aimStart = Offset(width / 2f, height - 90f)
        aimCurrent = start
    }

    fun updateAim(current: Offset) {
        if (!isAiming) return
        aimCurrent = current
    }

    fun releaseAim(onShotFired: () -> Unit) {
        if (!isAiming || isFiring) return
        isAiming = false

        val launcherPos = Offset(width / 2f, height - 90f)
        val dx = aimCurrent.x - launcherPos.x
        val dy = aimCurrent.y - launcherPos.y

        // Disallow firing downward
        if (dy >= -20f) return

        val angle = atan2(dy, dx)
        val speed = 25f * (1.0f + (bowPowerMultiplier - 1f) * 0.3f)
        val vx = cos(angle) * speed
        val vy = sin(angle) * speed

        isFiring = true
        comboCount = 0

        arrows.add(
            Arrow(
                id = nextArrowId++,
                x = launcherPos.x,
                y = launcherPos.y,
                vx = vx,
                vy = vy,
                pierceCount = if (bowPowerMultiplier > 1.35f) 1 else 0
            )
        )

        if (mode != GameMode.RUSH_ENDLESS) {
            arrowsRemaining--
        }

        onShotFired()
    }

    fun updatePhysics(
        onHitTarget: () -> Unit,
        onExplode: () -> Unit
    ) {
        if (isGameOver || isVictory) return

        // Update active arrows
        val activeArrows = arrows.filter { it.isActive }
        for (arrow in activeArrows) {
            arrow.trail.add(Offset(arrow.x, arrow.y))
            if (arrow.trail.size > 8) {
                arrow.trail.removeAt(0)
            }

            arrow.x += arrow.vx
            arrow.y += arrow.vy

            // Screen borders
            if (arrow.x - arrow.radius <= 0f) {
                arrow.x = arrow.radius
                arrow.vx = -arrow.vx
                spawnSparks(arrow.x, arrow.y, NeonCyan, 4)
            } else if (arrow.x + arrow.radius >= width) {
                arrow.x = width - arrow.radius
                arrow.vx = -arrow.vx
                spawnSparks(arrow.x, arrow.y, NeonCyan, 4)
            }

            if (arrow.y - arrow.radius <= 70f) {
                arrow.y = 70f + arrow.radius
                arrow.vy = -arrow.vy
                spawnSparks(arrow.x, arrow.y, NeonCyan, 4)
            }

            // Target collision
            val activeTargets = targets.filter { !it.isDestroyed }
            for (target in activeTargets) {
                if (checkCollision(arrow, target)) {
                    // Damage target
                    val damage = (1 * bowPowerMultiplier).toInt().coerceAtLeast(1)
                    target.currentHp -= damage
                    target.hitFlash = 1f
                    comboCount++
                    if (comboCount > maxComboInGame) {
                        maxComboInGame = comboCount
                    }

                    val points = 50 * comboCount
                    score += points
                    floatingTexts.add(
                        FloatingText(
                            id = nextTextId++,
                            text = if (comboCount > 2) "+$points (${comboCount}x)" else "+$points",
                            x = target.x + target.width / 2f,
                            y = target.y,
                            color = if (comboCount > 2) NeonGold else NeonCyan
                        )
                    )

                    spawnSparks(arrow.x, arrow.y, getTargetColor(target.type), 8)
                    onHitTarget()

                    if (target.currentHp <= 0) {
                        target.isDestroyed = true
                        targetsDestroyedCount++

                        when (target.type) {
                            TargetType.BOMB_TNT -> {
                                tntTriggeredCount++
                                triggerTntExplosion(target, onExplode)
                            }
                            TargetType.GOLD_BONUS -> {
                                val gold = 30
                                coinsEarned += gold
                                floatingTexts.add(
                                    FloatingText(
                                        id = nextTextId++,
                                        text = "+$gold 🪙",
                                        x = target.x + target.width / 2f,
                                        y = target.y - 15f,
                                        color = CoinGold
                                    )
                                )
                            }
                            TargetType.GEM_CRYSTAL -> {
                                val gem = 2
                                gemsEarned += gem
                                floatingTexts.add(
                                    FloatingText(
                                        id = nextTextId++,
                                        text = "+$gem 💎",
                                        x = target.x + target.width / 2f,
                                        y = target.y - 15f,
                                        color = GemGreen
                                    )
                                )
                            }
                            else -> {}
                        }
                    }

                    // Pierce or bounce
                    if (arrow.pierceCount > 0) {
                        arrow.pierceCount--
                    } else {
                        // Rebound
                        val centerX = target.x + target.width / 2f
                        val centerY = target.y + target.height / 2f
                        val diffX = arrow.x - centerX
                        val diffY = arrow.y - centerY
                        if (kotlin.math.abs(diffX) / target.width > kotlin.math.abs(diffY) / target.height) {
                            arrow.vx = -arrow.vx
                        } else {
                            arrow.vy = -arrow.vy
                        }
                    }
                    break
                }
            }

            // Arrow fallen off bottom
            if (arrow.y > height - 30f) {
                arrow.isActive = false
            }
        }

        // Clean finished arrows
        if (isFiring && arrows.all { !it.isActive }) {
            isFiring = false
            arrows.clear()
            onTurnEnd()
        }

        // Update particles
        val pIter = particles.iterator()
        while (pIter.hasNext()) {
            val p = pIter.next()
            p.x += p.vx
            p.y += p.vy
            p.vx *= 0.94f
            p.vy *= 0.94f
            p.life -= 0.035f
            p.alpha = p.life.coerceIn(0f, 1f)
            if (p.life <= 0f) {
                pIter.remove()
            }
        }

        // Update floating texts
        val tIter = floatingTexts.iterator()
        while (tIter.hasNext()) {
            val t = tIter.next()
            t.y -= 2f
            t.alpha -= 0.03f
            if (t.alpha <= 0f) {
                tIter.remove()
            }
        }

        // Flash decay
        targets.forEach {
            if (it.hitFlash > 0f) {
                it.hitFlash -= 0.1f
            }
        }
    }

    private fun checkCollision(arrow: Arrow, target: Target): Boolean {
        val closestX = arrow.x.coerceIn(target.x, target.x + target.width)
        val closestY = arrow.y.coerceIn(target.y, target.y + target.height)
        val distanceX = arrow.x - closestX
        val distanceY = arrow.y - closestY
        return (distanceX * distanceX + distanceY * distanceY) < (arrow.radius * arrow.radius)
    }

    private fun triggerTntExplosion(bomb: Target, onExplode: () -> Unit) {
        onExplode()
        val bombCenterX = bomb.x + bomb.width / 2f
        val bombCenterY = bomb.y + bomb.height / 2f
        spawnSparks(bombCenterX, bombCenterY, NeonAmber, 25)

        val blastRadius = 140f
        targets.filter { !it.isDestroyed }.forEach { target ->
            val tCenterX = target.x + target.width / 2f
            val tCenterY = target.y + target.height / 2f
            val dist = sqrt((tCenterX - bombCenterX) * (tCenterX - bombCenterX) + (tCenterY - bombCenterY) * (tCenterY - bombCenterY))
            if (dist <= blastRadius) {
                target.currentHp -= 3
                target.hitFlash = 1f
                spawnSparks(tCenterX, tCenterY, NeonGold, 8)
                if (target.currentHp <= 0) {
                    target.isDestroyed = true
                    targetsDestroyedCount++
                    score += 150
                }
            }
        }
    }

    private fun onTurnEnd() {
        val remainingTargets = targets.filter { !it.isDestroyed }

        when (mode) {
            GameMode.CAMPAIGN -> {
                if (remainingTargets.isEmpty()) {
                    isVictory = true
                    score += arrowsRemaining * 200
                } else if (arrowsRemaining <= 0) {
                    isGameOver = true
                }
            }
            GameMode.RUSH_ENDLESS -> {
                // Descend targets by 1 row
                val cellH = 56f + 8f
                remainingTargets.forEach { t ->
                    t.y += cellH
                    if (t.y + t.height >= height - 140f) {
                        isGameOver = true
                    }
                }
                if (!isGameOver) {
                    // Spawn new row on top
                    val cols = 7
                    val marginX = 24f
                    val topMargin = 120f
                    val cellW = (width - marginX * 2) / cols
                    spawnRushRow(0, cols, marginX, topMargin, cellW, 56f, roundMultiplier = (score / 1500) + 1)
                }
            }
            GameMode.DAILY_CHALLENGE -> {
                if (remainingTargets.isEmpty()) {
                    isVictory = true
                    coinsEarned += 300
                    gemsEarned += 25
                } else if (arrowsRemaining <= 0) {
                    isGameOver = true
                }
            }
        }
    }

    fun reviveWithBonus() {
        isGameOver = false
        arrowsRemaining += 5
        // Clear lowest 2 rows in Rush mode for breathing room
        if (mode == GameMode.RUSH_ENDLESS) {
            val sorted = targets.filter { !it.isDestroyed }.sortedByDescending { it.y }
            sorted.take(6).forEach { it.isDestroyed = true }
        }
    }

    private fun spawnSparks(x: Float, y: Float, color: Color, count: Int) {
        for (i in 0 until count) {
            val angle = Random.nextFloat() * 6.28f
            val speed = Random.nextFloat() * 7f + 2f
            particles.add(
                Particle(
                    x = x,
                    y = y,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    color = color,
                    size = Random.nextFloat() * 4f + 3f
                )
            )
        }
    }

    fun getTargetColor(type: TargetType): Color {
        return when (type) {
            TargetType.NORMAL -> NeonCyan
            TargetType.HARD -> NeonPurple
            TargetType.GOLD_BONUS -> NeonGold
            TargetType.GEM_CRYSTAL -> GemGreen
            TargetType.BOMB_TNT -> NeonAmber
            TargetType.PIERCE_ARROW -> NeonGreen
            TargetType.SPLIT_ARROW -> Color(0xFFFF2A6D)
        }
    }
}
