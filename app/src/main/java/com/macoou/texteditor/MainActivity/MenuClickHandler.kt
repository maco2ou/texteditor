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

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import com.macoou.texteditor.After
import com.macoou.texteditor.BatchReplacement.BatchReplacement
import com.macoou.texteditor.MainActivity.StaticData.fragments
import com.macoou.texteditor.MainActivity.StaticData.mTabLayout
import com.macoou.texteditor.OpenSourceLicenseActivity
import com.macoou.texteditor.Printer
import com.macoou.texteditor.R
import com.macoou.texteditor.Settings.SettingsMainActivity
import com.macoou.texteditor.rkUtils

import com.rk.librunner.Runner
import io.github.rosemoe.sora.text.ContentIO
import io.github.rosemoe.sora.widget.EditorSearcher
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date



class MenuClickHandler {

  companion object {

    private var searchText: String? = ""
    const val REQUEST_CODE_SAVE_FILE = 1

    fun handle(
      activity: MainActivity,
      menuItem: MenuItem,

    ): Boolean {

      val id = menuItem.itemId
      when (id) {

        R.id.action_close_all -> {
          activity.adapter.clear()
          activity.binding.tabs.visibility = View.GONE
          activity.binding.mainView.visibility = View.GONE
          activity.binding.fabEx.visibility = View.VISIBLE
          StaticData.menu?.findItem(R.id.run)?.setVisible(false)
          StaticData.menu?.findItem(R.id.stop)?.setVisible(false)

          MainActivity.updateMenuItems()
          return true
        }

        //プレビュー
        R.id.run -> {
          val webView = activity.findViewById<WebView>(R.id.webview)
          webView.visibility = View.VISIBLE
          Runner.run(fragments[mTabLayout.selectedTabPosition].file,activity,webView)
          return true
        }
        R.id.stop -> {
          val webView = activity.findViewById<WebView>(R.id.webview)
          webView.visibility = View.GONE
          Toast.makeText(activity, activity.getString(R.string.preview_stopped), Toast.LENGTH_SHORT).show()
          return true
        }
        
        R.id.action_save_all -> {
          // Handle action_all
          handleSaveAll(activity)
          return true
        }

        R.id.action_new -> {
          val webView = activity.findViewById<WebView>(R.id.webview)
          webView.visibility = View.GONE

          activity.NewFile()
          Toast.makeText(activity, R.string.create, Toast.LENGTH_SHORT).show()
          return true
        }

        R.id.action_open -> {
          val webView = activity.findViewById<WebView>(R.id.webview)
          webView.visibility = View.GONE

          activity.openFile2()
          Toast.makeText(activity, activity.getString(R.string.file_open), Toast.LENGTH_SHORT).show()

          return true
        }

        R.id.action_save -> {
          // Handle action_save
          saveFile(activity, fragments[mTabLayout.selectedTabPosition].id)
          return true
        }

        R.id.undo -> {
          // Handle undo
          fragments[mTabLayout.selectedTabPosition].Undo()
          updateUndoRedoMenuItems()
          Toast.makeText(activity, activity.getString(R.string.undo_done), Toast.LENGTH_SHORT).show()
          return true
        }

        R.id.redo -> {
          // Handle redo
          fragments[mTabLayout.selectedTabPosition].Redo()
          updateUndoRedoMenuItems()
          Toast.makeText(activity, activity.getString(R.string.redo_done), Toast.LENGTH_SHORT).show()
          return true
        }

        R.id.action_settings -> {
          val webView = activity.findViewById<WebView>(R.id.webview)
          webView.visibility = View.GONE
          activity.startActivity(Intent(activity, SettingsMainActivity::class.java))
          return true
        }

        R.id.action_print -> {
          // Handle action_print
            Printer.print(activity, fragments[mTabLayout.selectedTabPosition].content.toString())
          return true
        }

        R.id.batchrep -> {
          // Handle batchrep
          activity.startActivity(Intent(activity, BatchReplacement::class.java))
          return true
        }

        R.id.search -> {
          // Handle search
          handleSearch(activity)
          return true
        }

        R.id.search_next -> {
          // Handle search_next
          handleSearchNext()
          return true
        }

        R.id.search_previous -> {
          // Handle search_previous
          handleSearchPrevious()
          return true
        }

        R.id.search_close -> {
          // Handle search_close
          handleSearchClose()
          return true
        }

        R.id.replace -> {
          // Handle replace
          handleReplace(activity)
          return true
        }

        R.id.insertdate -> {
          // Handle insertdate
          activity.currentEditor.pasteText(
            " " + SimpleDateFormat.getDateTimeInstance()
              .format(Date(System.currentTimeMillis())) + " "
          )
          return true
        }


        R.id.action_open_source_licenses -> {
          activity.startActivity(Intent(activity, OpenSourceLicenseActivity::class.java))
          return true
        }
        R.id.action_exit -> {
          activity.finishAndRemoveTask()
          return true
        }

        else -> return false
      }

    }





    private fun updateUndoRedoMenuItems() {
      val undo = StaticData.menu.findItem(R.id.undo)
      val redo = StaticData.menu.findItem(R.id.redo)
      val editor = fragments[mTabLayout.selectedTabPosition].editor
      redo.isEnabled = editor.canRedo()
      undo.isEnabled = editor.canUndo()
    }

    private fun handleReplace(activity: MainActivity): Boolean {
      val popupView = LayoutInflater.from(activity).inflate(R.layout.popup_replace, null)
      MaterialAlertDialogBuilder(activity).setTitle(activity.getString(R.string.replace))
        .setView(popupView).setNegativeButton(activity.getString(R.string.cancel), null)
        .setPositiveButton(activity.getString(R.string.replace_all)) { _, _ ->
          replaceAll(popupView)
        }.show()
      return true
    }

    private fun replaceAll(popupView: View) {
      val replacementText =
        popupView.findViewById<TextView>(R.id.replace_replacement).text.toString()
      fragments[mTabLayout.selectedTabPosition].editor.searcher.replaceAll(replacementText)
    }

    private fun handleSearchNext(): Boolean {
      fragments[mTabLayout.selectedTabPosition].editor.searcher.gotoNext()
      return true
    }

    private fun handleSearchPrevious(): Boolean {
      fragments[mTabLayout.selectedTabPosition].editor.searcher.gotoPrevious()
      return true
    }

    private fun handleSearchClose(): Boolean {
      val editor = fragments[mTabLayout.selectedTabPosition].editor
      editor.searcher.stopSearch()
      hideSearchMenuItems()
      searchText = ""
      showUndoRedoMenuItems()
      return true
    }

    private fun hideSearchMenuItems() {
      StaticData.menu.findItem(R.id.search_next).isVisible = false
      StaticData.menu.findItem(R.id.search_previous).isVisible = false
      StaticData.menu.findItem(R.id.search_close).isVisible = false
      StaticData.menu.findItem(R.id.replace).isVisible = false
    }

    private fun showUndoRedoMenuItems() {
      StaticData.menu.findItem(R.id.undo).isVisible = true
      StaticData.menu.findItem(R.id.redo).isVisible = true
    }

    private fun handleSearch(activity: MainActivity): Boolean {
      val popupView = LayoutInflater.from(activity).inflate(R.layout.popup_search, null)
      val searchBox = popupView.findViewById<EditText>(R.id.searchbox)

      if (!searchText.isNullOrEmpty()) {
        searchBox.setText(searchText)
      }

      MaterialAlertDialogBuilder(activity).setTitle(activity.getString(R.string.search))
        .setView(popupView).setNegativeButton(activity.getString(R.string.cancel), null)
        .setPositiveButton(activity.getString(R.string.search)) { _, _ ->
          //search
          initiateSearch(searchBox, popupView)
        }.show()
      return true
    }

    private fun initiateSearch(searchBox: EditText, popupView: View) {
      val undo = StaticData.menu.findItem(R.id.undo)
      val redo = StaticData.menu.findItem(R.id.redo)
      undo.isVisible = false
      redo.isVisible = false
      val editor = fragments[mTabLayout.selectedTabPosition].editor
      val checkBox = popupView.findViewById<CheckBox>(R.id.case_senstive)
      searchText = searchBox.text.toString()
      editor.searcher.search(
        searchText!!,
        EditorSearcher.SearchOptions(EditorSearcher.SearchOptions.TYPE_NORMAL, !checkBox.isChecked)
      )
      showSearchMenuItems()
    }


    private fun showSearchMenuItems() {
      StaticData.menu.findItem(R.id.search_next).isVisible = true
      StaticData.menu.findItem(R.id.search_previous).isVisible = true
      StaticData.menu.findItem(R.id.search_close).isVisible = true
      StaticData.menu.findItem(R.id.replace).isVisible = true
    }



    private fun saveFileAs(activity: MainActivity, index: Int) {
      val fragment = fragments[index]
      val content = fragment.content
      val newFileName = fragment.fileName

      if (content != null) {
        try {
          val newFile = File(fragment.file?.parent, newFileName)
          val outputStream = FileOutputStream(newFile)
          //val outputStream: FileOutputStream = activity.openFileOutput(newFileName, Context.MODE_PRIVATE)
          ContentIO.writeTo(content, outputStream, true)
          outputStream.close()

          rkUtils.toast(activity, activity.getString(R.string.save_as))
        } catch (e: Exception) {
          e.printStackTrace()
          Log.e("e", "Error saving file ${e.message}")
        }
      } else {
        Log.e("e", "Content is null, cannot save file.")

      }
    }

    private fun saveFile(activity: MainActivity, index: Int) {
      val fragment = fragments[index]
      val file = fragment.file
      val content = fragment.content

      val tab = mTabLayout.getTabAt(index) ?: throw RuntimeException("Tab not found")
      if (tab.text?.endsWith("*") == true) {
        fragment.isModified = false
        tab.text = tab.text?.dropLast(1)
      }

       Thread {

        try {
          val outputStream = FileOutputStream(file, false)
          if (content != null) {
            ContentIO.writeTo(content, outputStream, true)
          }
          //rkUtils.toast(activity, activity.getString(R.string.save))
        } catch (e: IOException) {
          e.printStackTrace()
         // rkUtils.toast(activity, "Failed to save file: ${e.message}")
        }
      }.start()

      rkUtils.toast(activity, activity.getString(R.string.save))
    }

    private fun handleSaveAll(activity: MainActivity) {
      //loop over all tabs and save the files
      for (i in 0 until mTabLayout.tabCount) {
        saveFile(activity, i)
      }

      //show toast after some time
      After(100) {
        rkUtils.runOnUiThread {
          rkUtils.toast(activity, activity.getString(R.string.saveAll))
        }
      }

    }
  }


}
