package com.macoou.texteditor.MainActivity.treeview2

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

import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.macoou.texteditor.MainActivity.MainActivity
import com.macoou.texteditor.Settings.SettingsData

class PrepareRecyclerView(val activity: MainActivity) {

  companion object {
    val recyclerViewId = 428699
  }

  init {


    with(activity) {
      val holder = holder(this)

      val linearLayout = LinearLayout(this).apply {
        layoutParams = ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
          if (!SettingsData.getBoolean(activity, "diagonalScroll", false)) {
            setPadding(0, 0, dpToPx(54), dpToPx(5))
          } else {
            setPadding(0, 0, dpToPx(54), dpToPx(60))
          }

        }
      }

      val recyclerView = RecyclerView(this).apply {
        id = recyclerViewId
        layoutParams = ViewGroup.MarginLayoutParams(
          ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
          if (!SettingsData.getBoolean(activity, "diagonalScroll", false)) {
            setMargins(0, dpToPx(10), 0, 0)
          } else {
            setMargins(0, dpToPx(10), 0, dpToPx(60))
          }

        }
        visibility = View.GONE
      }

      linearLayout.addView(recyclerView)
      holder.addView(linearLayout)
      //activity.binding.maindrawer.addView(holder)
    }
  }


  fun holder(activity: MainActivity): ViewGroup {

    if (!SettingsData.getBoolean(activity, "diagonalScroll", false)) {
      val hsv = HorizontalScrollView(activity).apply {
        layoutParams = ViewGroup.MarginLayoutParams(
          ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        isHorizontalScrollBarEnabled = false
      }
      return hsv
    }


    val dsv = DiagonalScrollView(activity).apply {
      layoutParams = ViewGroup.MarginLayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
      ).apply {
        setMargins(0, dpToPx(10), 0, 0)
      }
    }
    return dsv
  }

  fun dpToPx(dp: Int): Int {
    val density = activity.resources.displayMetrics.density
    return (dp * density).toInt()
  }
}
