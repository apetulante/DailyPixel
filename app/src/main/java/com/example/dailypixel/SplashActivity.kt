package com.example.dailypixel

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide system UI (status bar and navigation bar)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContentView(R.layout.splash_screen)

        // Show splash screen for 2 seconds, then navigate to MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Close splash activity
        }, 2000) // 2 second delay
    }
}