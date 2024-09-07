/*
 * Copyright © 2022 Github Lzhiyong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

data class Node<T>(
  var value: T,
  var parent: Node<T>? = null,
  var child: List<Node<T>>? = null,
  var isExpand: Boolean = false,
  var level: Int = 0,
) {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Node<*>

    return value == other.value && parent == other.parent && child == other.child && isExpand == other.isExpand && level == other.level
  }

  override fun hashCode(): Int {
    return value.hashCode()
  }
}

object TreeViewModel {

  // add child node
  fun <T> add(
    parent: Node<T>, child: List<Node<T>>? = null
  ) {
    // check
    child?.let {
      if (it.isNotEmpty()) {
        parent.isExpand = true
      }
    }

    parent.parent?.let {
      val nodes = it.child
      if (nodes != null && nodes.size == 1 && ((child != null && child.isEmpty()) || child == null)) {
        parent.isExpand = true
      }
    }

    // parent associate with child
    parent.child = child

    child?.forEach {
      it.parent = parent
      it.level = parent.level + 1
    }
  }

  // remove child node
  fun <T> remove(
    parent: Node<T>, child: List<Node<T>>? = null
  ) {
    parent.child?.let {
      if (it.isNotEmpty()) {
        parent.isExpand = false
      }
    }
    parent.child = null

    child?.forEach { childNode ->
      childNode.parent = null
      childNode.level = 0
      if (childNode.isExpand) {
        childNode.isExpand = false
        childNode.child?.let { listNodes ->
          remove(childNode, listNodes)
        }
      }
    }
  }

  // Get all child nodes of the parent node
  private fun <T> getChildren(
    parent: Node<T>, result: MutableList<Node<T>>
  ): List<Node<T>> {
    parent.child?.let { result.addAll(it) }

    parent.child?.forEach {
      if (it.isExpand) {
        getChildren(it, result)
      }
    }
    return result
  }

  fun <T> getChildren(parent: Node<T>) = getChildren(parent, mutableListOf())
}