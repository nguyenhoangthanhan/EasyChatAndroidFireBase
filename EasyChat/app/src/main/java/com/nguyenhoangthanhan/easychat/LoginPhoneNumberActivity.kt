package com.nguyenhoangthanhan.easychat

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.nguyenhoangthanhan.easychat.databinding.ActivityLoginPhoneNumberBinding
import com.nguyenhoangthanhan.easychat.databinding.ActivityMainBinding

class LoginPhoneNumberActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginPhoneNumberBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginPhoneNumberBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initView()
        initEvents()
    }

    private fun initView() {
        binding.progressBarLogin.visibility = View.GONE
        binding.loginCountryCode.registerCarrierNumberEditText(binding.edtLoginMobileNumber)
    }

    private fun initEvents() {
        binding.btnSendOtp.setOnClickListener {
            if(!binding.loginCountryCode.isValidFullNumber){
                binding.edtLoginMobileNumber.error = "Phone number is not valid"
                return@setOnClickListener
            }
            val intent = Intent(this, LoginOtpActivity::class.java)
            intent.putExtra("phone", binding.loginCountryCode.fullNumberWithPlus)
            startActivity(intent)
        }
    }
}