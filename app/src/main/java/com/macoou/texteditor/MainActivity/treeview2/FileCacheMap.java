package com.macoou.texteditor.MainActivity.treeview2;

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

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.security.MessageDigest;

public class FileCacheMap<K extends File, V> {
    private static final int INITIAL_CAPACITY = 1 << 4;
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    private final Entry[] hashtable;

    private final MessageDigest digest;

    public FileCacheMap() {
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        hashtable = new Entry[INITIAL_CAPACITY];
    }

    public FileCacheMap(Integer capacity) {
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        int cap = tableSizeFor(capacity);
        hashtable = new Entry[cap];
    }

    private int tableSizeFor(Integer cap) {
        int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
        return n < 0 ? 1 : (n >= MAXIMUM_CAPACITY ? MAXIMUM_CAPACITY : n + 1);
    }

    final int hash(K key) {
        byte[] hash = digest.digest(key.toString().getBytes());
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result |= (hash[i] & 0xFF) << (8 * i);
        }
        return result;
    }

    public void put(K key, V value) {
        int hashCode = hash(key) & (hashtable.length - 1);
        Entry node = hashtable[hashCode];
        if (node == null) {
            Entry newNode = new Entry(key, value);
            hashtable[hashCode] = newNode;
        } else {
            Entry prevNode = node;
            while (node != null) {
                if (node.key.equals(key)) {
                    node.value = value;
                    return;
                }
                prevNode = node;
                node = node.next;
            }
            Entry newNode = new Entry(key, value);
            prevNode.next = newNode;
        }
    }

    public V get(K key) {
        int hashCode = hash(key) & (hashtable.length - 1);
        Entry node = hashtable[hashCode];
        while (node != null) {
            if (node.key.equals(key)) {
                return (V) node.value;
            }
            node = node.next;
        }
        return null;
    }

    class Entry<K, V> {
        public K key;
        public V value;
        public Entry next;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}