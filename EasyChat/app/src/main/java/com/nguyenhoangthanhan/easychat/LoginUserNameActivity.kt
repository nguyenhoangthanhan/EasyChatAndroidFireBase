package com.nguyenhoangthanhan.easychat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.nguyenhoangthanhan.easychat.databinding.ActivityLoginUserNameBinding
import com.nguyenhoangthanhan.easychat.model.UserModel
import com.nguyenhoangthanhan.easychat.util.FirebaseUtil

class LoginUserNameActivity : AppCompatActivity() {

    private val TAG = "LoginUserNameActivity_TAG"

    private lateinit var binding: ActivityLoginUserNameBinding

    private var phoneNumber = ""

    private var userModel:UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginUserNameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initData()
        initView()
        initEvents()
    }

    private fun initData() {
        Log.d(TAG, "#initData")
        phoneNumber = intent.extras?.getString("phone").toString()

        setInProgress(true)
        FirebaseUtil.currentUserDetails()?.get()?.addOnCompleteListener{
            setInProgress(false)
            if (it.isSuccessful){
                Log.d(TAG, "#initData.isSuccessful = true")
                userModel = it.result.toObject(UserModel::class.java)
                Log.d(TAG, "#initData.it.result.exists() = " + it.result.exists())
                Log.d(TAG, "#initData.it.result.toString() = " + it.result.toString())
                Log.d(TAG, "#initData.it.result.toObject(UserModel::class.java) = "
                        + it.result.toObject(UserModel::class.java))
                Log.d(TAG, "#initData.userModel = $userModel")
                if (userModel != null){
                    Log.d(TAG, "#initData.userModel.toString() = " + userModel.toString())
                    binding.edtLoginUsername.setText(userModel?.username)
                }
            }
            else{
                Log.d(TAG, "#initData.isSuccessful = false")
            }
        }
    }

    private fun initView() {

    }

    private fun initEvents() {
        binding.btnLetMeIn.setOnClickListener {
            setUsername()
        }
    }

    private fun setUsername() {
        val username = binding.edtLoginUsername.text.toString()
        if(username.isEmpty() || username.length <  3){
            binding.edtLoginUsername.error = "Username length should be at least 3 chars"
            return
        }
        setInProgress(true)

        if (userModel != null){
            userModel?.username = username
        }
        else{
            userModel = UserModel(phoneNumber, username, Timestamp.now(), FirebaseUtil.currentUserId())
        }

        FirebaseUtil.currentUserDetails()?.set(userModel!!)?.addOnCompleteListener {
            setInProgress(false)
            if (it.isSuccessful){
                val intent = Intent(this@LoginUserNameActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

    }

    private fun setInProgress(inProgress: Boolean) {
        if(inProgress){
            binding.progressBarLogin.visibility = View.VISIBLE
            binding.btnLetMeIn.visibility = View.GONE
        }else{
            binding.progressBarLogin.visibility = View.GONE
            binding.btnLetMeIn.visibility = View.VISIBLE
        }
    }
}