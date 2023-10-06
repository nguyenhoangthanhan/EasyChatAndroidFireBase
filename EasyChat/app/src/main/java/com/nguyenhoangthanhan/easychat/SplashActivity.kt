package com.nguyenhoangthanhan.easychat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            run {
                startActivity(Intent(this, LoginPhoneNumberActivity::class.java))
                finish()
            }
        }, 3000)
    }
}