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

import android.content.Context
import android.graphics.Color
import android.util.Log
import com.macoou.texteditor.Settings.SettingsData
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import org.eclipse.tm4e.core.registry.IThemeSource
import java.io.File

class setupEditor(val editor: CodeEditor, private val ctx: Context) {

  fun setupLanguage(fileName: String) {
    when (fileName.substringAfterLast('.', "")) {
      "java","bsh" -> {
        setLanguage("source.java")
      }

      "html" -> {
        setLanguage("text.html.basic")
      }

      "kt","kts" -> {
        setLanguage("source.kotlin")
      }

      "py" -> {
        setLanguage("source.python")
      }

      "xml" -> {
        setLanguage("text.xml")
      }

      "js" -> {
        setLanguage("source.js")
      }

      "md" -> {
        setLanguage("text.html.markdown")
      }

      "c" -> {
        setLanguage("source.c")
      }

      "cpp", "h" -> {
        setLanguage("source.cpp")
      }

      "json" -> {
        setLanguage("source.json")
      }

      "css" -> {
        setLanguage("source.css")
      }

      "cs" -> {
        setLanguage("source.cs")
      }
    }
  }


  private fun setLanguage(languageScopeName: String) {
    FileProviderRegistry.getInstance().addFileProvider(
      AssetsFileResolver(
        ctx.applicationContext?.assets
      )
    )

    GrammarRegistry.getInstance().loadGrammars("textmate/languages.json")

    val language = TextMateLanguage.create(
      languageScopeName, true /* true for enabling auto-completion */
    )
    editor.setEditorLanguage(language as Language)
  }


  fun ensureTextmateTheme() {
    var editorColorScheme = editor.colorScheme
    val themeRegistry = ThemeRegistry.getInstance()

    val darkMode = SettingsData.isDarkMode(ctx)
    try {
      if (darkMode) {
        val path = if (SettingsData.isOled(ctx)) {
          ctx.getExternalFilesDir(null)!!.absolutePath + "/unzip/textmate/black/darcula.json"///unzip/textmate/black/darcula.json
        } else {
          ctx.getExternalFilesDir(null)!!.absolutePath + "/unzip/textmate/darcula.json"///unzip/textmate/darcula.json
        }
        if (!File(path).exists()) {
          rkUtils.runOnUiThread {
            rkUtils.toast(
              ctx, ctx.resources.getString(R.string.theme_not_found_err)
            )
          }
        }

        themeRegistry.loadTheme(
          ThemeModel(
            IThemeSource.fromInputStream(
              FileProviderRegistry.getInstance().tryGetInputStream(path), path, null
            ), "darcula"
          )
        )
        editorColorScheme = TextMateColorScheme.create(themeRegistry)
        if (SettingsData.isOled(ctx)) {
          editorColorScheme.setColor(EditorColorScheme.WHOLE_BACKGROUND, Color.BLACK)
        }
      } else {
        val path = ctx.getExternalFilesDir(null)!!.absolutePath + "/unzip/textmate/quietlight.json"
        //Log.e("sssspath", "path: $path")
        if (!File(path).exists()) {
          rkUtils.runOnUiThread {
            rkUtils.toast(ctx, ctx.resources.getString(R.string.theme_not_found_err))
          }
        } else {
          val inputStream = FileProviderRegistry.getInstance().tryGetInputStream(path)
          if (inputStream == null) {
            //Log.e("sssThemeLoader", "InputStream is null for path: $path")
          } else {
            themeRegistry.loadTheme(
              ThemeModel(
                IThemeSource.fromInputStream(inputStream, path, null), "quitelight"
              )
            )
            editorColorScheme = TextMateColorScheme.create(themeRegistry)
          }
        }

      }
    } catch (e: Exception) {
      e.printStackTrace()
    }

    if (darkMode) {
      val pref = ctx.applicationContext.getSharedPreferences("MyPref", 0)
      themeRegistry.setTheme("darcula")
    } else {
      themeRegistry.setTheme("quietlight")
    }
    synchronized(editor) {
      editor.colorScheme = editorColorScheme
    }
  }
}