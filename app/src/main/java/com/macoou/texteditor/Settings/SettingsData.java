package com.macoou.texteditor.Settings;

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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class SettingsData {


    public static boolean isDarkMode(Context ctx) {
        return (ctx.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
    }

    public static boolean isOled(Context ctx) {
        return Boolean.parseBoolean(getSetting(ctx, "isOled", "false"));
    }

    public static boolean getBoolean(Context ctx, String key, Boolean Default) {
        return Boolean.parseBoolean(getSetting(ctx, key, Boolean.toString(Default)));
    }

    public static void setBoolean(Context ctx, String key, boolean value) {
        setSetting(ctx, key, Boolean.toString(value));
    }

    public static String getSetting(Context ctx, String key, String Default) {
        SharedPreferences sharedPreferences = ctx.getApplicationContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, Default);
    }

    public static void setSetting(Context ctx, String key, String value) {
        SharedPreferences sharedPreferences = ctx.getApplicationContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
        //editor.commit();
    }


}
