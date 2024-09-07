package com.macoou.texteditor.MainActivity.fragment

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
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.macoou.texteditor.After
import com.macoou.texteditor.BaseActivity
import com.macoou.texteditor.MainActivity.MainActivity
import com.macoou.texteditor.MainActivity.StaticData
import com.macoou.texteditor.R
import com.macoou.texteditor.Settings.SettingsData
import com.macoou.texteditor.rkUtils
import com.macoou.texteditor.rkUtils.runOnUiThread
import com.macoou.texteditor.setupEditor
import io.github.rosemoe.sora.event.ContentChangeEvent
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.text.ContentIO
import io.github.rosemoe.sora.widget.CodeEditor
import org.mozilla.universalchardet.UniversalDetector
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset


class DynamicFragment : Fragment {

  lateinit var fileName: String
  var file: File? = null
  private var ctx: Context? = null
  lateinit var editor: CodeEditor
  private var editorx: CodeEditor? = null
  var content: Content? = null
  var isModified: Boolean = false
  var undo: MenuItem? = null
  var redo: MenuItem? = null
  var encoding: String = "UTF-8"

  constructor() {
    After(100) {
      runOnUiThread {
        val fragmentManager = BaseActivity.getActivity(MainActivity::class.java)?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.remove(this)
        fragmentTransaction?.commitNowAllowingStateLoss()
      }
    }
  }

  constructor(file: File, ctx: Context, uri: Uri) {


    this.fileName = file.name
    this.ctx = ctx
    this.file = file
    editor = CodeEditor(ctx)
    editorx = editor

    this.encoding = detectFileEncoding(this.file)

    setupEditor(editor, ctx).setupLanguage(fileName)

    editor.setPinLineNumber(SettingsData.getBoolean(ctx,"pinline",false))

    if (SettingsData.isDarkMode(ctx)) {
      //setupEditor(editor, ctx).ensureTextmateTheme()
      runOnUiThread { // Update your UI here
        setupEditor(editor, ctx).ensureTextmateTheme()
      }
    } else {
      //Thread { setupEditor(editor, ctx).ensureTextmateTheme() }.start()
      runOnUiThread { // Update your UI here
        setupEditor(editor, ctx).ensureTextmateTheme()
      }
    }

    val wordwrap = SettingsData.getBoolean(ctx, "wordwrap", false)


    Thread {
      try {
        val inputStream: InputStream = FileInputStream(file)
        //val reader = InputStreamReader(inputStream, Charset.forName(this.encoding))//UTF-8
        //content = reader.readText().toString().let { Content(it) }
        //reader.close()
        content = ContentIO.createFrom(inputStream)
        inputStream.close()
        runOnUiThread { editor.setText(content) }



        if (wordwrap) {
          val length = content.toString().length
          if (length > 700 && content.toString().split("\\R".toRegex())
              .dropLastWhile { it.isEmpty() }.toTypedArray().size < 100
          ) {
            runOnUiThread {
              rkUtils.toast(
                ctx, resources.getString(R.string.ww_wait)
              )
            }
          }
          if (length > 1500) {
            runOnUiThread {
              Toast.makeText(
                ctx, resources.getString(R.string.ww_wait), Toast.LENGTH_LONG
              ).show()
            }
          }
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }
      setListener()

    }.start()


    editor.typefaceText = Typeface.createFromAsset(ctx.assets, "JetBrainsMono-Regular.ttf")
    editor.setTextSize(SettingsData.getSetting(ctx, "textsize", "14").toFloat())
    editor.isWordwrap = wordwrap

    After(200){
      runOnUiThread{
        if (undo == null || redo == null){
          return@runOnUiThread
        }
        undo = StaticData.menu.findItem(R.id.undo)
        redo = StaticData.menu.findItem(R.id.redo)
      }
    }




  }

  private fun setListener() {
    editor.subscribeAlways(
      ContentChangeEvent::class.java
    ) {
      updateUndoRedo()
      val tab = StaticData.mTabLayout.getTabAt(StaticData.mTabLayout.selectedTabPosition)
      if (isModified) {
        tab!!.setText("$fileName*")
      }
      isModified = true
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    return editorx
  }

  fun updateUndoRedo() {
    if (undo != null && redo != null) {
      redo!!.setEnabled(editor.canRedo())
      undo!!.setEnabled(editor.canUndo())
    }
  }

  @JvmOverloads
  fun releaseEditor(removeCoontent: Boolean = false) {
    editor.release()
    content = null
  }

  fun Undo() {
    if (editor.canUndo()) {
      editor.undo()
    }
  }

  fun Redo() {
    if (editor.canRedo()) {
      editor.redo()
    }
  }



  fun detectFileEncoding(file: File?): String {
    if (file == null) {
      return "UTF-8"
    }
    val detector = UniversalDetector(null)
    val fis = FileInputStream(file)
    val buffer = ByteArray(4096)
    var bytesRead: Int
    while (fis.read(buffer).also { bytesRead = it } > 0 && !detector.isDone) {
      detector.handleData(buffer, 0, bytesRead)
    }
    detector.dataEnd()
    val encoding = detector.detectedCharset
    detector.reset()

    return encoding ?: Charset.defaultCharset().name()
  }





}