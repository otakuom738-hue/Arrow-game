package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.EconomyRepository
import com.example.data.GamePreferences
import com.example.data.LeaderboardRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun readStringFromContext() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Arrow Break", appName)
  }

  @Test
  fun testGameEconomyAndLeaderboard() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = GamePreferences(context)
    val economy = EconomyRepository(prefs)
    val leaderboard = LeaderboardRepository(prefs)

    assertTrue(economy.bowSkins.isNotEmpty())
    assertTrue(economy.shopPackages.isNotEmpty())

    val rank = leaderboard.getPlayerRank()
    assertTrue(rank > 0)
  }
}
