package com.c23pc657.kramakata.ui.splash_screen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.c23pc657.kramakata.ui.main.MainActivity
import com.c23pc657.kramakata.R
import com.c23pc657.kramakata.constants.Constants.SPLASH_SCREEN_TIMER


class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Delay navigasi ke activity berikutnya menggunakan Handler
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }, SPLASH_SCREEN_TIMER)
    }
}