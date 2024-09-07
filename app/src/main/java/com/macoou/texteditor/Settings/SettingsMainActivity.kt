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
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.macoou.texteditor.BaseActivity
import com.macoou.texteditor.R
import com.macoou.texteditor.databinding.ActivitySettingsMainBinding
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.onClickView
import de.Maxr1998.modernpreferences.helpers.pref
import de.Maxr1998.modernpreferences.helpers.screen

class SettingsMainActivity : BaseActivity() {
  private lateinit var recyclerView: RecyclerView
  private lateinit var binding: ActivitySettingsMainBinding
  lateinit var padapter: PreferencesAdapter
  lateinit var playoutManager: LinearLayoutManager

  companion object {
    var settingsMain: SettingsMainActivity? = null
  }

  fun get_recycler_view(): RecyclerView {
    binding = ActivitySettingsMainBinding.inflate(layoutInflater)
    recyclerView = binding.recyclerView
    return recyclerView
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    settingsMain = this
    super.onCreate(savedInstanceState)
    padapter = PreferencesAdapter(getScreen())
    savedInstanceState?.getParcelable<PreferencesAdapter.SavedState>("padapter")
      ?.let(padapter::loadSavedState)

    playoutManager = LinearLayoutManager(this)
    get_recycler_view().apply {
      layoutManager = playoutManager
      adapter = padapter
      //layoutAnimation = AnimationUtils.loadLayoutAnimation(this@settings2, R.anim.preference_layout_fall_down)
    }


    setContentView(binding.root)
    binding.toolbar.title = getString(R.string.settings)
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
  }

  override fun onDestroy() {
    settingsMain = null
    super.onDestroy()
  }

  fun getScreen(): PreferenceScreen {
    return screen(this) {
      pref("Application") {
        title = getString(R.string.application)
        summary = getString(R.string.general_settings_for_the_application)
        iconRes = R.drawable.android
        onClickView {
          startActivity(Intent(this@SettingsMainActivity, SettingsApp::class.java))
        }
      }
      pref("editor") {
        title = getString(R.string.editor)
        summary = getString(R.string.general_settings_for_the_editor)
        iconRes = R.drawable.edit
        onClickView {
          startActivity(Intent(this@SettingsMainActivity, SettingsEditor::class.java))
        }
      }
      pref("app_version") {
        title = getString(R.string.about_the_app)
        summary = getString(R.string.version_1_0)
        iconRes = R.drawable.ic_info_24
        onClickView {

        }
      }
      pref("license") {
        title = "License"
        summary = "GNU GENERAL PUBLIC LICENSE 2.0"
        iconRes = R.drawable.ic_copyright_24
        onClickView {
          val url = "https://github.com/maco2ou/texteditor"
          val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
          }
          startActivity(intent)
        }
      }

    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    // Save the padapter state as a parcelable into the Android-managed instance state
    outState.putParcelable("padapter", padapter.getSavedState())
  }

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