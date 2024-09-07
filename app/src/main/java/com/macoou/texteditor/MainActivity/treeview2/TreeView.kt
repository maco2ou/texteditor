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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.macoou.texteditor.After
import com.macoou.texteditor.LoadingPopup
import com.macoou.texteditor.MainActivity.MainActivity
import com.macoou.texteditor.MainActivity.StaticData.nodes
import com.macoou.texteditor.Settings.SettingsData
import com.macoou.texteditor.rkUtils.runOnUiThread
import java.io.File


class TreeView(val ctx: MainActivity, rootFolder: File) {

  companion object {
    var opened_file_path = ""
  }

  init {
    val recyclerView = ctx.findViewById<RecyclerView>(PrepareRecyclerView.recyclerViewId).apply {
      setItemViewCacheSize(100)
      visibility = View.GONE

    }

    //ctx.binding.progressBar.visibility = View.VISIBLE


    Thread {
      opened_file_path = rootFolder.absolutePath
      SettingsData.setSetting(ctx, "lastOpenedPath", rootFolder.absolutePath)

      nodes = TreeViewAdapter.merge(rootFolder)

      val adapter = TreeViewAdapter(recyclerView, ctx, rootFolder)

      adapter.apply {
        setOnItemClickListener(object : OnItemClickListener {
          override fun onItemClick(v: View, node: Node<File>) {
            val loading = LoadingPopup(ctx, null).show()

            After(150) {
              runOnUiThread {
                ctx.newEditor(node.value, false, null)
                ctx.onNewEditor()
                if (!SettingsData.getBoolean(ctx, "keepDrawerLocked", false)) {
                  After(500) {
                    ctx.binding.drawerLayout.close()
                  }
                }
                loading.hide()
              }
            }


          }


          override fun onItemLongClick(v: View, node: Node<File>) {
            //FileAction(ctx, rootFolder, node.value, adapter)
          }
        })
        submitList(nodes)
      }


      ctx.runOnUiThread {
        recyclerView.layoutManager = LinearLayoutManager(ctx)
        //ctx.binding.progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        recyclerView.adapter = adapter
      }

    }.start()

  }
}
