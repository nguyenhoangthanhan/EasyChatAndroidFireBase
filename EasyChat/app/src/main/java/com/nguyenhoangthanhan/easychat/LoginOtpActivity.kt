package com.nguyenhoangthanhan.easychat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.nguyenhoangthanhan.easychat.databinding.ActivityLoginOtpBinding

class LoginOtpActivity : AppCompatActivity() {

    private lateinit var binding:ActivityLoginOtpBinding

    private var phoneNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginOtpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        phoneNumber = intent.extras?.getString("phone").toString()
        Toast.makeText(applicationContext, "Phone number: $phoneNumber", Toast.LENGTH_SHORT).show()

    }
}