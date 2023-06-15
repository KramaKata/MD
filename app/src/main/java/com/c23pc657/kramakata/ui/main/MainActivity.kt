package com.c23pc657.kramakata.ui.main

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.c23pc657.kramakata.databinding.ActivityMainBinding
import com.c23pc657.kramakata.ui.translator.Translator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var items = arrayOf("Indonesia", "English", "Hindi", "Bengali", "Gujarati", "Tamil", "Telugu")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val itemsAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line, items
        )

        binding.languageFrom.setAdapter(itemsAdapter)
        binding.languageTo.setAdapter(itemsAdapter)

        val translator = Translator(this, binding)

        binding.translate.setOnClickListener {
            translator.translateText()
        }

        val outputTextView = binding.output
        val btnSalin = binding.btnSalin
        val btnShare = binding.btnShare

        btnSalin.setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("label", outputTextView.text)
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(this, "Teks telah disalin", Toast.LENGTH_SHORT).show()
        }

        btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, outputTextView.text.toString())
            startActivity(Intent.createChooser(shareIntent, "Bagikan teks melalui"))
        }
    }
}
