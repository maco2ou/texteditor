package com.macoou.texteditor.MainActivity;

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


import android.view.Menu;
import com.google.android.material.tabs.TabLayout;
import com.macoou.texteditor.MainActivity.fragment.DynamicFragment;
import com.macoou.texteditor.MainActivity.treeview2.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class StaticData {

    public static final int REQUEST_DIRECTORY_SELECTION = 2937;
    public static ArrayList<DynamicFragment> fragments;
    public static List<Node<File>> nodes;
    public static TabLayout mTabLayout;
    public static Menu menu;
    public static File rootFolder;


    public static void clear() {
        nodes = null;
        menu = null;
        fragments = null;
        mTabLayout = null;
        rootFolder = null;

        //run the garbage collector
        System.gc();
    }

}
