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
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoadingPopup(val ctx: Activity, hide_after_millis: Long?) {
  private var dialog: AlertDialog? = null

  init {
    // Create the dialog on the UI thread
    ctx.runOnUiThread {
      val inflater1: LayoutInflater = ctx.layoutInflater
      val dialogView: View = inflater1.inflate(R.layout.progress_dialog, null)
      dialog = MaterialAlertDialogBuilder(ctx).setView(dialogView)
        .setCancelable(false).create()

      if (hide_after_millis != null) {
        show()
        After(hide_after_millis) {
          ctx.runOnUiThread {
            hide()
          }
        }
      }
    }
  }

  fun show(): LoadingPopup {
    ctx.runOnUiThread {
      dialog?.show()
    }
    return this
  }

  fun hide() {
    ctx.runOnUiThread {
      if (dialog != null && dialog?.isShowing == true) {
        dialog?.dismiss()
      }
    }
  }

  fun getDialog(): AlertDialog? {
    return dialog
  }
}
