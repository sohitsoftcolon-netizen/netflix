package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.adblock.AdBlocker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("MWSGuy", appName)
  }

  @Test
  fun `ad blocker correctly identifies known ad domains`() {
    assertTrue(AdBlocker.isAd("https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"))
    assertTrue(AdBlocker.isAd("https://adservice.google.com/adsid/integrator.js"))
    assertTrue(AdBlocker.isAd("https://serve.popads.net/serve.js"))
    assertTrue(AdBlocker.isAd("https://creative.exoclick.com/widgets/ad.js"))
    assertTrue(AdBlocker.isAd("https://example.com/banner/ad_slot.png"))
  }

  @Test
  fun `ad blocker does not block target site or legitimate urls`() {
    assertFalse(AdBlocker.isAd("https://www.mwsguy.sbs/p/home.html"))
    assertFalse(AdBlocker.isAd("https://www.mwsguy.sbs/assets/style.css"))
    assertFalse(AdBlocker.isAd("https://www.mwsguy.sbs/favicon.ico"))
    assertFalse(AdBlocker.isAd("https://example.com/article/read"))
  }
}
