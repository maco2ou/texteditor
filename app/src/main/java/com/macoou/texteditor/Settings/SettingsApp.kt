package com.macoou.texteditor.Settings

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

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
//import com.rk.libPlugin.client.ManagePlugin
import com.macoou.texteditor.After
import com.macoou.texteditor.BaseActivity
import com.macoou.texteditor.LoadingPopup
import com.macoou.texteditor.MainActivity.MainActivity
import com.macoou.texteditor.R
import com.macoou.texteditor.databinding.ActivitySettingsMainBinding
import com.macoou.texteditor.rkUtils
import com.macoou.texteditor.theme.ThemeManager
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.onCheckedChange
import de.Maxr1998.modernpreferences.helpers.onClickView
import de.Maxr1998.modernpreferences.helpers.pref
import de.Maxr1998.modernpreferences.helpers.screen
import de.Maxr1998.modernpreferences.helpers.switch

class SettingsApp : BaseActivity() {
  private lateinit var recyclerView: RecyclerView
  private lateinit var binding: ActivitySettingsMainBinding
  private lateinit var padapter: PreferencesAdapter
  private lateinit var playoutManager: LinearLayoutManager

  fun getRecyclerView(): RecyclerView {
    binding = ActivitySettingsMainBinding.inflate(layoutInflater)
    recyclerView = binding.recyclerView
    return recyclerView
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    padapter = PreferencesAdapter(getScreen())

    savedInstanceState?.getParcelable<PreferencesAdapter.SavedState>("padapter")
      ?.let(padapter::loadSavedState)


    playoutManager = LinearLayoutManager(this)
    getRecyclerView().apply {
      layoutManager = playoutManager
      adapter = padapter
      //layoutAnimation = AnimationUtils.loadLayoutAnimation(this@settings2, R.anim.preference_layout_fall_down)
    }

    setContentView(binding.root)
    binding.toggleButton.visibility = View.VISIBLE
    binding.toolbar.title = getString(R.string.application)
    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    if (SettingsData.isDarkMode(this) && SettingsData.isOled(this)) {
      binding.root.setBackgroundColor(Color.BLACK)
      binding.toolbar.setBackgroundColor(Color.BLACK)
      binding.appbar.setBackgroundColor(Color.BLACK)
      window.navigationBarColor = Color.BLACK
      val window = window
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
      window.statusBarColor = Color.BLACK
      window.navigationBarColor = Color.BLACK
    } else if (SettingsData.isDarkMode(this)) {
      val window = window
      window.navigationBarColor = Color.parseColor("#141118")
    }

    fun getCheckedBtnIdFromSettings(): Int {
      val settingDefaultNightMode = SettingsData.getSetting(
        this, "default_night_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString()
      ).toInt()

      return when (settingDefaultNightMode) {
        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> binding.auto.id
        AppCompatDelegate.MODE_NIGHT_NO -> binding.light.id
        AppCompatDelegate.MODE_NIGHT_YES -> binding.dark.id
        else -> throw RuntimeException("Illegal default night mode state")
      }
    }

    binding.toggleButton.check(getCheckedBtnIdFromSettings())

    val listener = View.OnClickListener {
      when (binding.toggleButton.checkedButtonId) {
        binding.auto.id -> {
          LoadingPopup(this@SettingsApp, 200)
          After(300) {
            SettingsData.setSetting(
              this,
              "default_night_mode",
              AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString()
            )

            runOnUiThread {
              AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
          }
        }

        binding.light.id -> {
          LoadingPopup(this@SettingsApp, 200)
          After(300) {
            SettingsData.setSetting(
              this,
              "default_night_mode",
              AppCompatDelegate.MODE_NIGHT_NO.toString()
            )

            runOnUiThread {
              AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
          }
        }

        binding.dark.id -> {
          LoadingPopup(this@SettingsApp, 200)
          After(300) {
            SettingsData.setSetting(
              this,
              "default_night_mode",
              AppCompatDelegate.MODE_NIGHT_YES.toString()
            )

            runOnUiThread {
              AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
          }
        }
      }
    }

    binding.light.setOnClickListener(listener)
    binding.dark.setOnClickListener(listener)
    binding.auto.setOnClickListener(listener)
  }


  private fun getScreen(): PreferenceScreen {
    return screen(this) {
      switch("oled") {
        titleRes = R.string.oled
        summary = getString(R.string.pure_black_theme_for_amoled_devices)
        iconRes = R.drawable.dark_mode
        defaultValue = false
        onCheckedChange { newValue ->
          SettingsData.setBoolean(this@SettingsApp, "isOled", newValue)
          LoadingPopup(this@SettingsApp, 180)
          getActivity(MainActivity::class.java)?.recreate()
          return@onCheckedChange true
        }
      }

      pref("Themes") {
        title = getString(R.string.themes)
        summary = getString(R.string.change_themes)
        iconRes = R.drawable.palette
        onClickView {
          val themes = ThemeManager.getThemes(this@SettingsApp)

          val linearLayout = LinearLayout(this@SettingsApp).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20.dp, 8.dp, 0, 0)
          }

          val radioGroup = RadioGroup(this@SettingsApp).apply {
            orientation = RadioGroup.VERTICAL
          }

          themes.forEach { theme ->
            val radioButton = RadioButton(this@SettingsApp).apply {
              text = theme.first
            }
            radioGroup.addView(radioButton)
          }

          linearLayout.addView(radioGroup)
          val selectedThemeName = ThemeManager.getSelectedTheme(this@SettingsApp)
          val selectedThemeIndex = themes.indexOfFirst { it.first == selectedThemeName }
          if (selectedThemeIndex != -1) {
            radioGroup.check(radioGroup.getChildAt(selectedThemeIndex).id)
          } else {
            radioGroup.check(radioGroup.getChildAt(0).id)
          }

          var checkID = radioGroup.checkedRadioButtonId

          radioGroup.setOnCheckedChangeListener { _, checkedId ->
            checkID = checkedId
          }

          val dialog =
            MaterialAlertDialogBuilder(this@SettingsApp)
              .setView(linearLayout)
              .setTitle(getString(R.string.themes))
              .setNegativeButton(getString(R.string.cancel), null)
              .setPositiveButton(getString(R.string.apply)) { _, _ ->
                val loading = LoadingPopup(this@SettingsApp, null).show()

                val selectedTheme = themes[radioGroup.indexOfChild(radioGroup.findViewById(checkID))]
                ThemeManager.setSelectedTheme(this@SettingsApp, selectedTheme.first)

                activityMap.values.forEach { activity ->
                  activity?.recreate()
                }

                loading.hide()
              }.show()

          dialog.window?.setLayout(
            resources.getDimensionPixelSize(R.dimen.dialog_width), // Set your desired width here
            ViewGroup.LayoutParams.WRAP_CONTENT
          )
        }
      }

    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    // Save the padapter state as a parcelable into the Android-managed instance state
    outState.putParcelable("padapter", padapter.getSavedState())
  }

  val Int.dp: Int
    get() = TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics
    ).toInt()

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // Handle action bar item clicks here
    val id = item.itemId
    if (id == android.R.id.home) {
      // Handle the back arrow click here
      onBackPressed()
      return true
    }
    return super.onOptionsItemSelected(item)
  }
}
