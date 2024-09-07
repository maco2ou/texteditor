package com.macoou.texteditor.BatchReplacement;

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

import static com.macoou.texteditor.MainActivity.StaticData.fragments;
import static com.macoou.texteditor.rkUtils.dpToPx;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.macoou.texteditor.BaseActivity;
import com.macoou.texteditor.MainActivity.fragment.TabAdapter;
import com.macoou.texteditor.R;
import com.macoou.texteditor.Settings.SettingsData;

import com.macoou.texteditor.SimpleEditor.SimpleEditor;
import com.macoou.texteditor.databinding.ActivityBatchReplacementBinding;
import com.macoou.texteditor.rkUtils;

import java.util.Objects;
import java.util.Random;

public class BatchReplacement extends BaseActivity {

    private ActivityBatchReplacementBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBatchReplacementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_batch_replacement));

        if (SettingsData.isDarkMode(this) && SettingsData.isOled(this)) {
            findViewById(R.id.drawer_layout).setBackgroundColor(Color.BLACK);
            findViewById(R.id.appbar).setBackgroundColor(Color.BLACK);
            findViewById(R.id.mainBody).setBackgroundColor(Color.BLACK);
            findViewById(R.id.toolbar).setBackgroundColor(Color.BLACK);
            getWindow().setNavigationBarColor(Color.BLACK);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
            window.setNavigationBarColor(Color.BLACK);
        }


    }

    private View newEditBox(String hint) {
        LinearLayout rootLinearLayout = new LinearLayout(this);
        rootLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        rootLinearLayout.setTag("keyRep");
        rootLinearLayout.setOrientation(LinearLayout.VERTICAL);

        // Create the inner LinearLayout
        LinearLayout innerLinearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams innerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(50, this));  // height is 50dp
        innerParams.setMargins(dpToPx(22, this), dpToPx(10, this), dpToPx(22, this), 0);
        innerLinearLayout.setLayoutParams(innerParams);
        innerLinearLayout.setTag("keyword");

        // Set the background for innerLinearLayout
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.edittext);
        innerLinearLayout.setBackground(drawable);

        // Create the EditText
        EditText editText = new EditText(this);
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(editTextParams);
        editText.setPadding(dpToPx(8, this), 0, dpToPx(5, this), 0);  // paddingStart 8dp, paddingEnd 5dp
        editText.setId(View.generateViewId());
        editText.setSingleLine(true);
        editText.setHint(hint);
        editText.setBackgroundColor(android.graphics.Color.TRANSPARENT);

        // Add EditText to inner LinearLayout
        innerLinearLayout.addView(editText);

        rootLinearLayout.addView(innerLinearLayout);
        return rootLinearLayout;
    }

    public void addBatch(View v) {
        int random_int = new Random().nextInt();
        ((LinearLayout) findViewById(R.id.mainBody)).addView(newEditBox(getString(R.string.keyword_regex)));
        ((LinearLayout) findViewById(R.id.mainBody)).addView(newEditBox(getString(R.string.replacement)));
        View view = new View(this);

        // Set the width and height of the View
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(0, this), // Width in dp
                dpToPx(20, this)   // Height in dp
        );
        view.setLayoutParams(params);
        ((LinearLayout) findViewById(R.id.mainBody)).addView(view);

        findViewById(R.id.removeBatch).setVisibility(View.VISIBLE);
    }

    public void removeBatch(View v) {
        LinearLayout linearLayout = findViewById(R.id.mainBody);
        int childCount = linearLayout.getChildCount();
        if (childCount > 3) {
            // Remove the last child first
            linearLayout.removeViewAt(childCount - 1);
            // Remove the second-to-last child
            linearLayout.removeViewAt(childCount - 2);
            // Remove the third-to-last child
            linearLayout.removeViewAt(childCount - 3);
            if (linearLayout.getChildCount() <= 3) {
                findViewById(R.id.removeBatch).setVisibility(View.GONE);
            }
        } else {
            findViewById(R.id.removeBatch).setVisibility(View.GONE);
        }

    }

    public void replace_all(View v) {
        ProgressDialog dialog = ProgressDialog.show(this, "",
                getString(R.string.please_wait), true);
        LinearLayout linearLayout = findViewById(R.id.mainBody);
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View view = linearLayout.getChildAt(i);
            if (view instanceof LinearLayout) {
                LinearLayout l = (LinearLayout) ((LinearLayout) view).getChildAt(0);
                EditText editText = (EditText) l.getChildAt(0);
                if (editText.getHint().equals(getString(R.string.keyword_regex))) {
                    View viewx = linearLayout.getChildAt(i + 1);
                    LinearLayout lx = (LinearLayout) ((LinearLayout) viewx).getChildAt(0);
                    EditText editTextx = (EditText) lx.getChildAt(0);
                    String keyword = editText.getText().toString();
                    String replacement = editTextx.getText().toString();

                    if (fragments != null) {
                        var editor = TabAdapter.getCurrentEditor();
                        editor.setText(editor.getText().toString().replaceAll(keyword, replacement));

                    } else if (getIntent().getExtras().getBoolean("isExt", false)) {
                        SimpleEditor.editor.setText(SimpleEditor.editor.getText().toString().replaceAll(keyword, replacement));
                    }


                }
            }
        }
        dialog.hide();
        rkUtils.toast(this, getString(R.string.action_completed));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // Handle the back arrow click here
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}