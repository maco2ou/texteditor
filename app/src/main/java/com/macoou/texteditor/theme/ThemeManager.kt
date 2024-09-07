package com.macoou.texteditor.theme

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

import android.content.Context
import android.content.res.Resources
import com.macoou.texteditor.R
import com.macoou.texteditor.Settings.SettingsData

object ThemeManager {
  private const val THEME_PREFIX = "selectable_"

  fun getSelectedTheme(context: Context): String {
    return SettingsData.getSetting(context, "selected_theme", "Berry")
  }

  fun setSelectedTheme(context: Context, themeName: String) {
    SettingsData.setSetting(context, "selected_theme", themeName)
  }

  fun applyTheme(context: Context) {
    setTheme(context, getSelectedTheme(context))
  }

  fun setTheme(context: Context, themeName: String) {
    context.setTheme(getThemeIdByName(context, themeName))
    setSelectedTheme(context, themeName)
  }

  private fun getThemeIdByName(context: Context, themeName: String): Int {
    val themeResName = "$THEME_PREFIX$themeName"
    return context.resources.getIdentifier(themeResName, "style", context.packageName)
  }

  fun getThemes(context: Context): List<Pair<String, Int>> {
    val stylesClass = R.style::class.java
    val fields = stylesClass.declaredFields
    val themes = mutableListOf<Pair<String, Int>>()

    for (field in fields) {
      try {
        val resourceId = field.getInt(null)
        val resourceName = context.resources.getResourceEntryName(resourceId)
        if (!resourceName.startsWith(THEME_PREFIX)) {
          continue
        }
        val finalName = resourceName.removePrefix(THEME_PREFIX)
        themes.add(Pair(finalName, resourceId))
      } catch (e: IllegalAccessException) {
        e.printStackTrace()
      }
    }

    return themes
  }

  fun getCurrentTheme(context: Context): Resources.Theme? {
    return context.theme
  }

  fun getCurrentThemeId(context: Context): Int {
    val attrs = intArrayOf(android.R.attr.theme)
    val typedArray = getCurrentTheme(context)!!.obtainStyledAttributes(attrs)
    val themeId = typedArray.getResourceId(0, 0)
    typedArray.recycle()
    return themeId
  }
}
