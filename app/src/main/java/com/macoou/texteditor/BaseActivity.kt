package com.macoou.texteditor

/**
MIT License

Copyright (c) 2024 Rohit Kushvaha

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 **/

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.macoou.texteditor.Settings.SettingsData
import com.macoou.texteditor.theme.ThemeManager
import java.util.WeakHashMap


abstract class BaseActivity : AppCompatActivity() {

  companion object {
    val activityMap = WeakHashMap<Class<out BaseActivity>, Activity>()

    @Suppress("UNCHECKED_CAST")
    fun <T : BaseActivity> getActivity(activityClass: Class<T>): T? {
      return activityMap[activityClass] as? T
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    ThemeManager.applyTheme(this)
    super.onCreate(savedInstanceState)

    activityMap[javaClass] = this

    val settingDefaultNightMode = SettingsData.getSetting(
      this, "default_night_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString()
    ).toInt()

    if (settingDefaultNightMode != AppCompatDelegate.getDefaultNightMode()) {
      AppCompatDelegate.setDefaultNightMode(settingDefaultNightMode)
    }

    if (!SettingsData.isDarkMode(this)) {
      //light mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
          window.navigationBarColor = Color.parseColor("#FEF7FF")
          window.statusBarColor = Color.parseColor("#000000")//
          val decorView = window.decorView
          val windowInsetsController = decorView.windowInsetsController
          windowInsetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS)
        }else{//対策
          window.navigationBarColor = Color.parseColor("#FEF7FF")
          val decorView = window.decorView
          var flags = decorView.systemUiVisibility
          flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
          decorView.systemUiVisibility = flags
          window.statusBarColor = Color.parseColor("#FEF7FF")
        }

    } else {
      window.statusBarColor = Color.parseColor("#141118")
    }
    if (SettingsData.isDarkMode(this) && SettingsData.isOled(this)) {
      val window = window
      window.navigationBarColor = Color.BLACK
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
      window.statusBarColor = Color.BLACK
    }

//onCreate
  }

  override fun onPause() {
    super.onPause()
    ThemeManager.applyTheme(this)
  }
}