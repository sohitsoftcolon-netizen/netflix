package com.example.util

import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration

enum class DeviceFormFactor {
    PHONE,
    TABLET,
    TV
}

object DeviceUtils {

    fun isTelevision(context: Context): Boolean {
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager
        if (uiModeManager?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) {
            return true
        }
        val pm = context.packageManager
        if (pm.hasSystemFeature(PackageManager.FEATURE_LEANBACK)) {
            return true
        }
        // Touchscreen check: TV devices typically don't have touchscreens
        val hasNoTouch = !pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN)
        val hasTelevisionFeature = pm.hasSystemFeature("android.hardware.type.television")
        return hasTelevisionFeature || (hasNoTouch && !pm.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE))
    }

    fun isTablet(context: Context): Boolean {
        if (isTelevision(context)) return false
        val config = context.resources.configuration
        return config.smallestScreenWidthDp >= 600
    }

    fun getDeviceFormFactor(context: Context): DeviceFormFactor {
        return when {
            isTelevision(context) -> DeviceFormFactor.TV
            isTablet(context) -> DeviceFormFactor.TABLET
            else -> DeviceFormFactor.PHONE
        }
    }
}
