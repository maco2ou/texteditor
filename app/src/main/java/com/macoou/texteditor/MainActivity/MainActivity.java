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


import static com.macoou.texteditor.MainActivity.PathUtils.convertUriToPath;
import static com.macoou.texteditor.MainActivity.StaticData.REQUEST_DIRECTORY_SELECTION;
import static com.macoou.texteditor.MainActivity.StaticData.fragments;
import static com.macoou.texteditor.MainActivity.StaticData.mTabLayout;
import static com.macoou.texteditor.MainActivity.StaticData.menu;
import static com.macoou.texteditor.rkUtils.dpToPx;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import com.macoou.texteditor.After;
import com.macoou.texteditor.BaseActivity;
import com.macoou.texteditor.MainActivity.fragment.DynamicFragment;
import com.macoou.texteditor.MainActivity.fragment.TabAdapter;
import com.macoou.texteditor.R;
import com.macoou.texteditor.Settings.SettingsData;
import com.macoou.texteditor.databinding.ActivityMainBinding;
import com.macoou.texteditor.rkUtils;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import io.github.rosemoe.sora.widget.CodeEditor;

public class MainActivity extends BaseActivity {

    int REQUEST_CODE_OPEN_DOCUMENT_RENAME = 5587;
    private static final int REQUEST_CODE_STORAGE_PERMISSIONS = 38386;
    final int REQUEST_FILE_SELECTION = 123;
    final int REQUEST_FILE_NEW = 321;
    private final int REQUEST_CODE_MANAGE_EXTERNAL_STORAGE = 36169;
    public ActivityMainBinding binding;
    public TabAdapter adapter;
    public ViewPager viewPager;
    //public DrawerLayout drawerLayout;
    NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private final boolean isReselecting = false;

    private FloatingActionButton fabMain;
    private FloatingActionButton fabAdd;
    private FloatingActionButton fabCreate;

    public static void updateMenuItems() {
        final boolean visible = !(fragments == null || fragments.isEmpty());
        if (menu == null) {
            new After(200, () -> rkUtils.runOnUiThread(MainActivity::updateMenuItems));
            return;
        }
        menu.findItem(R.id.action_close_all).setVisible(visible);
        menu.findItem(R.id.action_new).setVisible(visible);
        menu.findItem(R.id.action_open).setVisible(visible);
        menu.findItem(R.id.batchrep).setVisible(visible);
        menu.findItem(R.id.search).setVisible(visible);
        menu.findItem(R.id.action_save).setVisible(visible);
        menu.findItem(R.id.action_print).setVisible(visible);
        menu.findItem(R.id.action_save_all).setVisible(visible);
        menu.findItem(R.id.batchrep).setVisible(visible);
        menu.findItem(R.id.search).setVisible(visible);
        menu.findItem(R.id.undo).setVisible(visible);
        menu.findItem(R.id.redo).setVisible(visible);
        menu.findItem(R.id.insertdate).setVisible(visible);

        //上とは逆に設定する
        final boolean visible2 = (fragments == null || fragments.isEmpty());
        menu.findItem(R.id.action_open_source_licenses).setVisible(visible2);
        menu.findItem(R.id.action_exit).setVisible(visible2);

        if (visible && SettingsData.getBoolean(BaseActivity.Companion.getActivity(MainActivity.class), "show_arrows", false)) {
            BaseActivity.Companion.getActivity(MainActivity.class).binding.divider.setVisibility(View.VISIBLE);
            BaseActivity.Companion.getActivity(MainActivity.class).binding.mainBottomBar.setVisibility(View.VISIBLE);
            var vp = BaseActivity.Companion.getActivity(MainActivity.class).binding.viewpager;
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) vp.getLayoutParams();
            layoutParams.bottomMargin = dpToPx(50, BaseActivity.Companion.getActivity(MainActivity.class));  // Convert dp to pixels as needed
            vp.setLayoutParams(layoutParams);

        } else {
            BaseActivity.Companion.getActivity(MainActivity.class).binding.divider.setVisibility(View.GONE);
            BaseActivity.Companion.getActivity(MainActivity.class).binding.mainBottomBar.setVisibility(View.GONE);
            var vp = BaseActivity.Companion.getActivity(MainActivity.class).binding.viewpager;
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) vp.getLayoutParams();
            layoutParams.bottomMargin = dpToPx(0, BaseActivity.Companion.getActivity(MainActivity.class));  // Convert dp to pixels as needed
            vp.setLayoutParams(layoutParams);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StaticData.clear();
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //new After(1000, () -> new SFTPClient());

        setSupportActionBar(binding.toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle(R.string.text_editor);


        //navigationView.getLayoutParams().width = (int) (getSystem().getDisplayMetrics().widthPixels * 0.87);

        fabMain = findViewById(R.id.fab_ex);
        fabAdd = findViewById(R.id.fab_add);
        fabCreate = findViewById(R.id.fab_create);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, getString(R.string.file_open), Toast.LENGTH_SHORT).show();
                openFile2();
            }
        });
        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewFile();
                Toast.makeText(MainActivity.this, R.string.create, Toast.LENGTH_SHORT).show();
            }
        });


        //new PrepareRecyclerView(this);

        verifyStoragePermission();

        //run async init
        new Init(this);

        viewPager = binding.viewpager;
        mTabLayout = binding.tabs;
        viewPager.setOffscreenPageLimit(15);
        mTabLayout.setupWithViewPager(viewPager);



        //onCreate
    }


    public void verifyStoragePermission() {
        var shouldAsk = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                shouldAsk = true;
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                shouldAsk = true;
            }
        }

        if (shouldAsk) {
            new MaterialAlertDialogBuilder(this).setTitle("Manage Storage").setMessage("App needs access to edit files in your storage. Please allow the access in the upcoming system setting.").setNegativeButton("Exit App", (dialog, which) -> {
                finishAffinity();
            }).setPositiveButton(getString(R.string.ok), (dialog, which) -> {

                    String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    ActivityCompat.requestPermissions(this, perms, REQUEST_CODE_STORAGE_PERMISSIONS);

            }).setCancelable(false).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //check permission for old devices
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSIONS) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permssion denied
                verifyStoragePermission();
            }
        }
    }

    public CodeEditor getCurrentEditor() {
        return fragments.get(mTabLayout.getSelectedTabPosition()).getEditor();
    }

    public boolean hasUriPermission(Uri uri) {
        if (uri == null) return false;
        List<UriPermission> persistedPermissions = getContentResolver().getPersistedUriPermissions();
        boolean hasPersistedPermission = persistedPermissions.stream().anyMatch(p -> p.getUri().equals(uri));
        return hasPersistedPermission;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_MANAGE_EXTERNAL_STORAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    // Permission still not granted
                    verifyStoragePermission();
                }
            }
        } else
            if (requestCode == REQUEST_FILE_SELECTION && resultCode == RESULT_OK && data != null) {
            binding.tabs.setVisibility(View.VISIBLE);
            binding.mainView.setVisibility(View.VISIBLE);
            binding.fabEx.setVisibility(View.GONE);
            binding.toolbarTitle.setVisibility(View.GONE);
            getSupportActionBar().setTitle("");
            Uri uri = data.getData();
            newEditor(new File(convertUriToPath(this, data.getData())), false,uri);


        } else if (requestCode == REQUEST_DIRECTORY_SELECTION && resultCode == RESULT_OK && data != null) {

        }

            else if (requestCode == REQUEST_FILE_NEW && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    writeFileToUri(uri, getString(R.string.write_the_file_contents_here));

                }
            }
        }else if (requestCode == MenuClickHandler.REQUEST_CODE_SAVE_FILE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    String str = fragments.get(mTabLayout.getSelectedTabPosition()).getEditor().getText().toString();
                    if (str != null) {
                        try {
                            OutputStream outputStream = getContentResolver().openOutputStream(uri);
                            if (outputStream != null) {
                                outputStream.write(str.getBytes());
                                outputStream.close();
                                Toast.makeText(this, R.string.saved_successfully, Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, R.string.saving_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }

    }

    private void writeFileToUri(Uri uri, String content) {
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                outputStream.write(content.getBytes());
                outputStream.close();

                binding.tabs.setVisibility(View.VISIBLE);
                binding.mainView.setVisibility(View.VISIBLE);
                binding.fabEx.setVisibility(View.GONE);
                binding.toolbarTitle.setVisibility(View.GONE);

                newEditor(new File(convertUriToPath(this, uri)), false, uri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onNewEditor() {
        binding.fabEx.setVisibility(View.GONE);
        binding.tabs.setVisibility(View.VISIBLE);
        binding.mainView.setVisibility(View.VISIBLE);
        binding.toolbarTitle.setVisibility(View.GONE);
        updateMenuItems();
    }

    public void newEditor(File file, boolean isNewFile,Uri uri) {
        newEditor2(file, isNewFile, null,uri);
    }

    public void newEditor2(File file, boolean isNewFile, String text, Uri uri) {
        if (adapter == null) {
            fragments = new ArrayList<>();
            adapter = new TabAdapter(getSupportFragmentManager());
            viewPager.setAdapter(adapter);
        }

        for (DynamicFragment f : fragments) {
            if (Objects.equals(f.getFile(), file)) {
                rkUtils.toast(this, getString(R.string.file_already_opened));
                return;
            }
        }

        var dynamicfragment = new DynamicFragment(file, this,uri);
        if (text != null) {
            dynamicfragment.editor.setText(text);
        }
        adapter.addFragment(dynamicfragment, file);

        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                String name = fragments.get(tab.getPosition()).getFileName();
                if (name != null) {
                    tab.setText(name);
                }
            }
        }

        updateMenuItems();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onDestroy() {
        StaticData.clear();
        super.onDestroy();
    }


    public void openFile2() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);//OPEN
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*"); // Allow all file types
        startActivityForResult(intent, REQUEST_FILE_SELECTION);
    }

    public void NewFile() {
        String baseName = "newfile.txt";


        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);//CREATE
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*"); //
        intent.putExtra(Intent.EXTRA_TITLE, baseName);
        startActivityForResult(intent,  REQUEST_FILE_NEW);//
    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        StaticData.menu = menu;

        if (menu instanceof MenuBuilder m) {
            //noinspection RestrictedApi
            m.setOptionalIconsVisible(true);
        }

        menu.findItem(R.id.search).setVisible(!(fragments == null || fragments.isEmpty()));
        menu.findItem(R.id.batchrep).setVisible(!(fragments == null || fragments.isEmpty()));


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        if (id == android.R.id.home) {

            }
            return MenuClickHandler.Companion.handle(this, item);


        }


}
