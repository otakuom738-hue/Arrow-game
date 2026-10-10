package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CoinGold
import com.example.ui.theme.EnergyRed
import com.example.ui.theme.GameSurfaceElevated
import com.example.ui.theme.GemGreen
import com.example.ui.theme.TextPrimary

@Composable
fun TopCurrencyBar(
    coins: Int,
    gems: Int,
    energy: Int,
    soundEnabled: Boolean,
    onToggleSound: () -> Unit,
    onOpenShop: () -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Energy Counter
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(GameSurfaceElevated)
                .clickable { onOpenShop() }
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = "Energy",
                tint = EnergyRed,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "$energy/5",
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(3.dp))
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(EnergyRed),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Energy",
                    tint = Color.White,
                    modifier = Modifier.size(10.dp)
                )
            }
        }

        // Coins Counter
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(GameSurfaceElevated)
                .clickable { onOpenShop() }
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = "Coins",
                tint = CoinGold,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "$coins",
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Gems Counter
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(GameSurfaceElevated)
                .clickable { onOpenShop() }
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Diamond,
                contentDescription = "Gems",
                tint = GemGreen,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "$gems",
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Sound Toggle
        IconButton(
            onClick = onToggleSound,
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(GameSurfaceElevated)
                .testTag("sound_toggle_button")
        ) {
            Icon(
                imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                contentDescription = "Sound Toggle",
                tint = TextPrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        // Profile / Settings
        IconButton(
            onClick = onOpenProfile,
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(GameSurfaceElevated)
                .testTag("profile_button")
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = TextPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
