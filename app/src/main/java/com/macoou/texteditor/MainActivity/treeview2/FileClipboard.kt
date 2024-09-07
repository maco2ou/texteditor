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

import java.io.File

class FileClipboard private constructor() {
  companion object {
    @Volatile
    private var fileClipboard: File? = null

    @Volatile
    private var isPasted: Boolean = true

    @JvmStatic
    fun setFile(file: File?) {
      synchronized(this) {
        fileClipboard = file
        isPasted = false
      }
    }

    @JvmStatic
    fun clear() {
      synchronized(this) {
        fileClipboard = null
        isPasted = true
      }
    }

    @JvmStatic
    fun getFile(): File? {
      synchronized(this) {
        val file = fileClipboard
        if (isPasted) {
          fileClipboard = null
        }
        isPasted = true
        return file
      }
    }

    @JvmStatic
    fun isEmpty(): Boolean {
      synchronized(this) {
        return fileClipboard == null
      }
    }

    @JvmStatic
    fun markAsPasted() {
      synchronized(this) {
        isPasted = true
      }
    }
  }
}
