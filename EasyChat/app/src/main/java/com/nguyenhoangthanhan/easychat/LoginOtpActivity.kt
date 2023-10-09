package com.nguyenhoangthanhan.easychat

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.nguyenhoangthanhan.easychat.databinding.ActivityLoginOtpBinding
import com.nguyenhoangthanhan.easychat.util.AndroidUtil
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit

class LoginOtpActivity : AppCompatActivity() {

    private lateinit var binding:ActivityLoginOtpBinding

    private var timeOutSeconds = 60L
    private var phoneNumber = ""
    private var verificationCode = ""
    private lateinit var forceResendingToken: PhoneAuthProvider.ForceResendingToken

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginOtpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initData()
        initView()
        initEvents()

    }

    private fun initData() {
        phoneNumber = intent.extras?.getString("phone").toString()
    }

    private fun initView() {
        sendOTtp(phoneNumber, false)
    }

    private fun initEvents() {
        binding.btnLoginNext.setOnClickListener{
            val enteredOtp = binding.edtInputOtp.text.toString()
            val credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp)
            singIn(credential)
        }

        binding.tvResendOtp.setOnClickListener {
            sendOTtp(phoneNumber, true)
        }
    }

    private fun sendOTtp(phoneNumber:String, isResend: Boolean) {
        setResendTimer()
        setInProgress(true)
        val builder:PhoneAuthOptions.Builder = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(timeOutSeconds, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    singIn(p0)
                    setInProgress(false)
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    AndroidUtil.showToastShort(applicationContext, "OTP verification failed")
                    setInProgress(false)
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    verificationCode = p0
                    forceResendingToken = p1
                    AndroidUtil.showToastShort(applicationContext, "OTP sent successfully")
                    setInProgress(false)
                }
            })

        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(
                builder.setForceResendingToken(forceResendingToken).build())
        }
        else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build())
        }
    }

    private fun singIn(phoneAuthCredential: PhoneAuthCredential) {
        //login and go to next activity
        setInProgress(true)
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener {
            setInProgress(false)
            if (it.isSuccessful){
                val intent = Intent(this@LoginOtpActivity, LoginUserNameActivity::class.java)
                intent.putExtra("phone", phoneNumber)
                startActivity(intent)
            }
            else{
                AndroidUtil.showToastShort(applicationContext, "OTP verification failed")
            }
        }
    }

    private fun setInProgress(inProgress: Boolean) {
        if(inProgress){
            binding.progressBarLogin.visibility = View.VISIBLE
            binding.btnLoginNext.visibility = View.GONE
        }else{
            binding.progressBarLogin.visibility = View.GONE
            binding.btnLoginNext.visibility = View.VISIBLE
        }
    }

    private fun setResendTimer() {
        binding.tvResendOtp.isEnabled = false
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask(){
            override fun run() {
                timeOutSeconds--
                binding.tvResendOtp.text = "Resend OTP in $timeOutSeconds seconds"
                if(timeOutSeconds <= 0){
                    timeOutSeconds = 60L
                    timer.cancel()
                    runOnUiThread {
                        binding.tvResendOtp.isEnabled = true
                    }
                }
            }

        },0, 1000)
    }
}