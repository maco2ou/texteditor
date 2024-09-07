package com.macoou.texteditor

import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.macoou.texteditor.databinding.ActivityOpensourcelicenseBinding
import java.io.*
import kotlin.collections.ArrayList

class OpenSourceLicenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOpensourcelicenseBinding


    //リストの宣言
    private var wordList: java.util.ArrayList<String?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpensourcelicenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        //フレームワーク作成
        wordList = ArrayList()


        val arrayAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this, R.layout.custom_list, wordList!! as List<Any?>
        )


        //assetから読み取り
        val assetManager = resources.assets
        val inputStream= assetManager.open("license.csv")

        if (inputStream != null) {
            try {
                readCSV(inputStream)

                //binding.listView.visibility
                binding.listView.adapter = arrayAdapter

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }


        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(callback)

    }

    fun readCSV(inputStream: InputStream) {
        try {

            val inputStreamReader = InputStreamReader(inputStream)

            //pdfに元がある。　これは出力に[]なし。番号を振ってくれるが設定が必要。
            inputStreamReader.useLines {
                var lineNo = 1
                it.forEach {
                    wordList?.add(it)
                    println("$lineNo: $it")
                    lineNo++
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



}