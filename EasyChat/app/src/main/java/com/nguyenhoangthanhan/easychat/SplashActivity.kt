package com.nguyenhoangthanhan.easychat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.nguyenhoangthanhan.easychat.util.FirebaseUtil

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            run {
                if(FirebaseUtil.isLoggedIn()){
                    startActivity(Intent(this, MainActivity::class.java))
                }
                else{
                    startActivity(Intent(this, LoginPhoneNumberActivity::class.java))
                }
                finish()
            }
        }, 1000)
    }
}