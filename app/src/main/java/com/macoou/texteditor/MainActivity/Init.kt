package com.macoou.texteditor.MainActivity

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
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import android.view.Surface
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.PopupMenu
import com.blankj.utilcode.util.KeyboardUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.rk.librunner.Runner
import com.macoou.texteditor.After
import com.macoou.texteditor.Decompress
import com.macoou.texteditor.MainActivity.PathUtils.convertUriToPath
import com.macoou.texteditor.MainActivity.StaticData.mTabLayout
import com.macoou.texteditor.R
import com.macoou.texteditor.Settings.SettingsData
import com.macoou.texteditor.rkUtils
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.Locale



class Init(activity: MainActivity) {
  init {
    Thread {
      Thread.currentThread().priority = 10
      with(activity) {


        if (!SettingsData.isDarkMode(this)) {
          //light mode
            window.navigationBarColor = Color.parseColor("#FEF7FF")
            val decorView = window.decorView
            var flags = decorView.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            decorView.systemUiVisibility = flags
            window.statusBarColor = Color.parseColor("#FEF7FF")

        } else if (SettingsData.isDarkMode(this)) {

          if (SettingsData.isOled(this)) {

            binding.drawerLayout.setBackgroundColor(Color.BLACK)
            //binding.navView.setBackgroundColor(Color.BLACK)
            binding.main.setBackgroundColor(Color.BLACK)
            binding.appbar.setBackgroundColor(Color.BLACK)
            binding.toolbar.setBackgroundColor(Color.BLACK)
            binding.tabs.setBackgroundColor(Color.BLACK)
            binding.mainView.setBackgroundColor(Color.BLACK)
            val window = window
            window.navigationBarColor = Color.BLACK
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.BLACK
          } else {
            val window = window
            window.navigationBarColor = Color.parseColor("#141118")

          }

        }


        mTabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
          override fun onTabSelected(tab: TabLayout.Tab) {
            viewPager.setCurrentItem(tab.position)
            val fragment = StaticData.fragments[mTabLayout.selectedTabPosition]
            fragment.updateUndoRedo()
            StaticData.menu?.findItem(R.id.run)?.setVisible(fragment.file != null && Runner.isRunnable(fragment.file!!))
            StaticData.menu?.findItem(R.id.stop)?.setVisible(fragment.file != null && Runner.isRunnable(fragment.file!!))
          }

          override fun onTabUnselected(tab: TabLayout.Tab) {}
          override fun onTabReselected(tab: TabLayout.Tab) {
            val popupMenu = PopupMenu(activity, tab.view)
            val inflater = popupMenu.menuInflater

            inflater.inflate(R.menu.tab_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
              val id = item.itemId
              //tab
              if (id == R.id.close_this) {
                adapter.removeFragment(mTabLayout.selectedTabPosition)
                StaticData.menu?.findItem(R.id.run)?.setVisible(false)
                StaticData.menu?.findItem(R.id.stop)?.setVisible(false)
              } else if (id == R.id.close_others) {
                adapter.closeOthers(viewPager.currentItem)
                StaticData.menu?.findItem(R.id.run)?.setVisible(false)
                StaticData.menu?.findItem(R.id.stop)?.setVisible(false)
              } else if (id == R.id.close_all) {
                adapter.clear()
                StaticData.menu?.findItem(R.id.run)?.setVisible(false)
                StaticData.menu?.findItem(R.id.stop)?.setVisible(false)
              } else if (id == R.id.properties) {
                showProperties()
              } else if (id == R.id.rename) {
                showRename(activity)
              }

              // Update tab names
              for (i in 0 until mTabLayout.tabCount) {
                val tab = mTabLayout.getTabAt(i)
                if (tab != null) {
                  val name = StaticData.fragments[i].fileName
                  tab.setText(name)
                }
              }

              if (mTabLayout.tabCount < 1) {
                binding.tabs.visibility = View.GONE
                binding.mainView.visibility = View.GONE
                binding.fabEx.visibility = View.VISIBLE
              }
              MainActivity.updateMenuItems()
              true
            }
            popupMenu.show()
          }

          private fun showRename(activity: MainActivity) {
            val fragment = StaticData.fragments[mTabLayout.selectedTabPosition]
            val name = StaticData.fragments[mTabLayout.selectedTabPosition].fileName

            val file = fragment.file

            val dialogView = layoutInflater.inflate(R.layout.popup_new, null)
            val editTextFileName = dialogView.findViewById<EditText>(R.id.name)
            editTextFileName.setText(name)

            MaterialAlertDialogBuilder(activity)
              .setTitle(getString(R.string.confirmation))
              .setView(dialogView)
              .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                val newFileName = editTextFileName.text.toString()
                if (newFileName.isNotEmpty()) {

                  val newFile = File(file!!.parent, newFileName)
                  if (file.renameTo(newFile)) {
                    val updatedUri = Uri.fromFile(newFile)
                    rkUtils.toast(activity,"Name changed")
                    adapter.removeFragment(mTabLayout.selectedTabPosition)
                    newEditor(File(convertUriToPath(activity, updatedUri)), false, updatedUri)
                    MainActivity.updateMenuItems()
                  } else {
                    val externalStorageDir = Environment.getExternalStorageDirectory()
                    val newExternalFile = File(externalStorageDir, newFileName)
                    if (file.renameTo(newExternalFile)) {
                      val updatedUri = Uri.fromFile(newExternalFile)
                      rkUtils.toast(activity,"Name changed :"+newExternalFile)
                      adapter.removeFragment(mTabLayout.selectedTabPosition)
                      newEditor(File(convertUriToPath(activity, updatedUri)), false, updatedUri)
                      MainActivity.updateMenuItems()
                    }
                  }

                }else{
                  rkUtils.toast(activity, getString(R.string.name_is_empty))
                }
                dialog.dismiss()
              }
              .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
              }
              .show()
          }



          fun showProperties() {
            val fragment = StaticData.fragments[mTabLayout.selectedTabPosition]
            val file = fragment.file
            val encode = fragment.encoding
            val properties = getFileProperties(file!!,encode)

            val propertiesText = properties.entries.joinToString("\n") { "${it.key}: ${it.value}" }

            MaterialAlertDialogBuilder(activity)
              .setTitle(getString(R.string.properties))
              .setMessage(propertiesText)
              .setPositiveButton(getString(R.string.ok), null)
              .show()
          }

          fun getFileProperties(file: File, encode: String): Map<String, String> {
            val properties = mutableMapOf<String, String>()
            properties[getString(R.string.name)] = file.name
            properties[getString(R.string.type)] = file.extension
            properties[getString(R.string.encode)] = encode
            properties[getString(R.string.size)] = "${file.length()} bytes"
            properties[getString(R.string.created)] = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(file.lastModified())
            properties[getString(R.string.last_modified)] = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(file.lastModified())
            properties[getString(R.string.destination_path)] = file.absolutePath
            return properties
          }

        })

        //todo use shared prefs instead of files
        if (!File(getExternalFilesDir(null).toString() + "/unzip").exists()) {
          Thread {
            try {
              Decompress.unzipFromAssets(
                this@with,
                "files.zip",
                getExternalFilesDir(null).toString() + "/unzip"
              )
              File(getExternalFilesDir(null).toString() + "files").delete()
              File(getExternalFilesDir(null).toString() + "files.zip").delete()
              File(getExternalFilesDir(null).toString() + "textmate").delete()
            } catch (e: Exception) {
              e.printStackTrace()
            }
          }.start()
        }



      }

      After(
        1000
      ) {
        rkUtils.runOnUiThread {
          activity.onBackPressedDispatcher.addCallback(activity,
            object : OnBackPressedCallback(true) {
              override fun handleOnBackPressed() {

                var shouldExit = true

                var isModified = false
                if (StaticData.fragments != null) {
                  for (fragment in StaticData.fragments) {

                    if (fragment.isModified) {
                      isModified = true
                    }
                  }
                  if (isModified) {
                    shouldExit = false
                    val dialog: MaterialAlertDialogBuilder =
                      MaterialAlertDialogBuilder(activity).setTitle(
                        activity.getString(R.string.unsaved)
                      ).setMessage(activity.getString(R.string.unsavedfiles))
                        .setNegativeButton(
                          activity.getString(R.string.cancel),
                          null
                        ).setPositiveButton(
                          activity.getString(R.string.exit)
                        ) { dialogInterface: DialogInterface?, i: Int -> activity.finish() }


                    dialog.setNeutralButton(
                      activity.getString(R.string.saveexit)
                    ) { xdialog: DialogInterface?, which: Int ->
                      activity.onOptionsItemSelected(
                        StaticData.menu.findItem(
                          R.id.action_save_all
                        )
                      )
                      activity.finish()
                    }
                    dialog.show()
                  }
                }
                if (shouldExit) {
                  activity.finish()
                }
              }
            })
        }
      }

      val intent: Intent = activity.intent
      val type = intent.type




      rkUtils.runOnUiThread {
        val arrows = activity.binding.childs
        for (i in 0 until arrows.childCount) {
          val button = arrows.getChildAt(i)
          button.setOnClickListener { v ->
            val fragment = StaticData.fragments[mTabLayout.selectedTabPosition]
            val cursor = fragment.editor.cursor
            when (v.id) {
              R.id.left_arrow -> {
                if (cursor.leftColumn - 1 >= 0) {
                  fragment.editor.setSelection(
                    cursor.leftLine,
                    cursor.leftColumn - 1
                  )
                }
              }

              R.id.right_arrow -> {
                val lineNumber = cursor.leftLine
                val line = fragment.content!!.getLine(lineNumber)

                if (cursor.leftColumn < line.length) {
                  fragment.editor.setSelection(
                    cursor.leftLine,
                    cursor.leftColumn + 1
                  )

                }

              }

              R.id.up_arrow -> {
                if (cursor.leftLine - 1 >= 0) {
                  val upline = cursor.leftLine - 1
                  val uplinestr = fragment.content!!.getLine(upline)

                  var columm = 0

                  if (uplinestr.length < cursor.leftColumn) {
                    columm = uplinestr.length
                  } else {
                    columm = cursor.leftColumn
                  }


                  fragment.editor.setSelection(
                    cursor.leftLine - 1,
                    columm
                  )
                }

              }

              R.id.down_arrow -> {
                if (cursor.leftLine + 1 < fragment.content!!.lineCount) {

                  val dnline = cursor.leftLine + 1
                  val dnlinestr = fragment.content!!.getLine(dnline)

                  var columm = 0

                  if (dnlinestr.length < cursor.leftColumn) {
                    columm = dnlinestr.length
                  } else {
                    columm = cursor.leftColumn
                  }

                  fragment.editor.setSelection(
                    cursor.leftLine + 1,
                    columm
                  )
                }
              }
              R.id.tab -> {
                val tabsize = SettingsData.getSetting(activity,"tabsize","4").toInt()
                val sb = StringBuilder()
                for (i in 0 until tabsize){
                  sb.append(" ")
                }
                fragment.editor.insertText(sb.toString(),tabsize)}

              R.id.untab -> {

                if (cursor.leftColumn == 0) {
                  return@setOnClickListener
                }

                // Retrieve tab size setting
                val tabSize = SettingsData.getSetting(activity, "tabsize", "4").toInt()

                // Create a string with spaces equal to tab size
                val spaceString = " ".repeat(tabSize)

                // Get the line content where the cursor is located
                val lineContent = fragment.content?.getLine(cursor.leftLine).toString()

                // Check if there are enough characters before the cursor to match the tab size
                if (cursor.leftColumn >= tabSize) {

                  fragment.editor.deleteText()

                }

              }
              R.id.home -> {
                fragment.editor.setSelection(
                  cursor.leftLine,0
                )
              }
              R.id.end -> {
                fragment.editor.setSelection(
                  cursor.leftLine, fragment.content?.getLine(cursor.leftLine)?.length
                    ?: 0
                )
              }
            }
          }
        }
      }


      rkUtils.runOnUiThread {
        val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val rotation = windowManager.defaultDisplay.rotation
          windowManager.defaultDisplay.rotation
        activity.binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
          override fun onGlobalLayout() {
            val r = Rect()
            activity.binding.root.getWindowVisibleDisplayFrame(r)
            val screenHeight = activity.binding.root.rootView.height
            val keypadHeight = screenHeight - r.bottom

            if (keypadHeight > screenHeight * 0.30) {
             if(rotation != Surface.ROTATION_0 && rotation != Surface.ROTATION_180){
                KeyboardUtils.hideSoftInput(activity)
                rkUtils.toast(activity,
                  activity.getString(R.string.can_t_open_keyboard_in_horizontal_mode))
             }
            }
          }
        })
      }

    }.start()
  }


}