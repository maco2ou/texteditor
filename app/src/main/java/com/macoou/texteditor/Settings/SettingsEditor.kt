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

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.macoou.texteditor.BaseActivity
import com.macoou.texteditor.LoadingPopup
import com.macoou.texteditor.MainActivity.fragment.DynamicFragment
import com.macoou.texteditor.MainActivity.MainActivity
import com.macoou.texteditor.MainActivity.StaticData
import com.macoou.texteditor.R
import com.macoou.texteditor.databinding.ActivitySettingsMainBinding
import com.macoou.texteditor.rkUtils
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.onCheckedChange
import de.Maxr1998.modernpreferences.helpers.onClick
import de.Maxr1998.modernpreferences.helpers.pref
import de.Maxr1998.modernpreferences.helpers.screen
import de.Maxr1998.modernpreferences.helpers.switch

class SettingsEditor : BaseActivity() {

  private lateinit var recyclerView: RecyclerView
  private lateinit var binding: ActivitySettingsMainBinding
  private lateinit var padapter: PreferencesAdapter
  private lateinit var playoutManager: LinearLayoutManager

  fun get_recycler_view(): RecyclerView {
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
    get_recycler_view().apply {
      layoutManager = playoutManager
      adapter = padapter
      //layoutAnimation = AnimationUtils.loadLayoutAnimation(this@settings2, R.anim.preference_layout_fall_down)
    }

    setContentView(binding.root)
    binding.toolbar.title = getString(R.string.editor)
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

  fun getScreen(): PreferenceScreen {
    return screen(this) {
      switch("wordwrap") {
        titleRes = R.string.ww
        summary = getString(R.string.enable_word_wrap_in_all_editors)
        iconRes = R.drawable.reorder
        onCheckedChange { isChecked ->
          SettingsData.setBoolean(this@SettingsEditor, "wordwrap", isChecked)
          if (StaticData.fragments != null && StaticData.fragments.isNotEmpty()) {
            for (fragment in StaticData.fragments) {
              val dynamicFragment = fragment as DynamicFragment
              dynamicFragment.editor.isWordwrap = isChecked
            }
          }
          return@onCheckedChange true
        }
      }


      switch("diagnolScroll") {
        title = getString(R.string.diagnol_scrolling)
        summary = getString(R.string.enable_diagnol_scrolling_in_file_browser)
        iconRes = R.drawable.diagonal_scroll
        defaultValue = false
        onCheckedChange { isChecked ->
          SettingsData.setBoolean(this@SettingsEditor, "diagonalScroll", isChecked)
          LoadingPopup(this@SettingsEditor, 180)
          getActivity(MainActivity::class.java)?.recreate()
          return@onCheckedChange true
        }
      }
      switch("showlinenumbers") {
        title = getString(R.string.show_line_numbers)
        summary = getString(R.string.show_line_numbers_in_editor)
        iconRes = R.drawable.linenumbers
        defaultValue = true
        onCheckedChange { isChecked ->
          if (StaticData.fragments?.isNotEmpty() == true) {
            StaticData.fragments.forEach { fragment ->
              fragment.editor.isLineNumberEnabled = isChecked
            }
          }
          return@onCheckedChange true
        }
      }
      switch("pinlinenumbers") {
        title = getString(R.string.pin_line_numbers)
        summary = getString(R.string.pin_line_numbers_in_editor)
        iconRes = R.drawable.linenumbers
        defaultValue = false
        onCheckedChange { isChecked ->
          SettingsData.setBoolean(this@SettingsEditor,"pinline",isChecked)
          if (StaticData.fragments?.isNotEmpty() == true) {
            StaticData.fragments.forEach { fragment ->
              fragment.editor.setPinLineNumber(isChecked)
            }
          }
          return@onCheckedChange true
        }
      }

      switch("arrow_keys") {
        title = getString(R.string.extra_keys)
        summary = getString(R.string.show_extra_keys_in_the_editor)
        iconRes = R.drawable.double_arrows
        defaultValue = false
        onCheckedChange { isChecked ->

          SettingsData.setBoolean(this@SettingsEditor, "show_arrows", isChecked)

          if (StaticData.fragments == null || StaticData.fragments.isEmpty()) {
            return@onCheckedChange true
          }
          LoadingPopup(this@SettingsEditor,200)

          if (isChecked) {
            getActivity(MainActivity::class.java)?.binding?.divider?.visibility = View.VISIBLE
            getActivity(MainActivity::class.java)?.binding?.mainBottomBar?.visibility = View.VISIBLE
            val vp = getActivity(MainActivity::class.java)?.binding?.viewpager
            val layoutParams = vp?.layoutParams as RelativeLayout.LayoutParams
            layoutParams.bottomMargin = rkUtils.dpToPx(
              40f, getActivity(MainActivity::class.java)
            ) // Convert dp to pixels as needed
            vp.setLayoutParams(layoutParams)
          } else {
            getActivity(MainActivity::class.java)?.binding?.divider?.visibility = View.GONE
            getActivity(MainActivity::class.java)?.binding?.mainBottomBar?.visibility = View.GONE
            val vp = getActivity(MainActivity::class.java)?.binding?.viewpager
            val layoutParams = vp?.layoutParams as RelativeLayout.LayoutParams
            layoutParams.bottomMargin = rkUtils.dpToPx(
              0f, getActivity(MainActivity::class.java)
            ) // Convert dp to pixels as needed
            vp.setLayoutParams(layoutParams)
          }

          getActivity(MainActivity::class.java)?.recreate()

          return@onCheckedChange true
        }
      }
      pref("tabsize"){
        title = getString(R.string.tab_size)
        summary = getString(R.string.set_tab_size)
        iconRes = R.drawable.double_arrows
        onClick {
          val view = LayoutInflater.from(this@SettingsEditor).inflate(R.layout.popup_new,null)
          val edittext = view.findViewById<EditText>(R.id.name).apply {
            hint = getString(R.string.tab_size)
            setText(SettingsData.getSetting(this@SettingsEditor,"tabsize","4"))
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED or InputType.TYPE_NUMBER_FLAG_DECIMAL
          }
          MaterialAlertDialogBuilder(this@SettingsEditor).setTitle(getString(R.string.tab_size))
            .setView(view).setNegativeButton(getString(R.string.cancel),null).setPositiveButton(getString(R.string.apply)){
              dialog,which ->
              val text = edittext.text.toString()
              for (c in text){
                if (!c.isDigit()){
                  rkUtils.toast(this@SettingsEditor,getString(R.string.invalid_value))
                  return@setPositiveButton
                }
              }
              if(text.toInt() > 16){
                rkUtils.toast(this@SettingsEditor,getString(R.string.value_too_large))
                return@setPositiveButton
              }
              SettingsData.setSetting(this@SettingsEditor,"tabsize",text)

              if(StaticData.fragments != null){
                for (f in StaticData.fragments){
                  f.editor.tabWidth = text.toInt()
                }
              }
            }.show()

          return@onClick true
        }
      }
      pref("textsize"){
        title = getString(R.string.text_size)
        summary = getString(R.string.set_text_size)
        iconRes = R.drawable.reorder
        onClick {
          val view = LayoutInflater.from(this@SettingsEditor).inflate(R.layout.popup_new,null)
          val edittext = view.findViewById<EditText>(R.id.name).apply {
            hint = getString(R.string.text_size)
            setText(SettingsData.getSetting(this@SettingsEditor,"textsize","14"))
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED or InputType.TYPE_NUMBER_FLAG_DECIMAL
          }
          MaterialAlertDialogBuilder(this@SettingsEditor).setTitle(getString(R.string.text_size))
            .setView(view).setNegativeButton(getString(R.string.cancel),null).setPositiveButton(getString(R.string.apply)){
              dialog,which ->
              val text = edittext.text.toString()
              for (c in text){
                if (!c.isDigit()){
                  rkUtils.toast(this@SettingsEditor, getString(R.string.invalid_value))
                  return@setPositiveButton
                }
              }
              if(text.toInt() > 32){
                rkUtils.toast(this@SettingsEditor, getString(R.string.value_too_large))
                return@setPositiveButton
              }
              SettingsData.setSetting(this@SettingsEditor,"textsize",text)

              if(StaticData.fragments != null){
                for (f in StaticData.fragments){
                  f.editor.setTextSize(text.toFloat())
                }
              }
            }.show()

          return@onClick true
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