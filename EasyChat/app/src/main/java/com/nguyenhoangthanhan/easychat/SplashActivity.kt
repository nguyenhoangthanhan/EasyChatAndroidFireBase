package com.nguyenhoangthanhan.easychat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.nguyenhoangthanhan.easychat.model.UserModel
import com.nguyenhoangthanhan.easychat.util.AndroidUtil
import com.nguyenhoangthanhan.easychat.util.FirebaseUtil

class SplashActivity : AppCompatActivity() {

    private val TAG = "SplashActivity_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (intent.extras != null){
            Log.d(TAG, "#onCreate.FirebaseUtil.isLoggedIn() && intent.extras == true")
            //from notification
            val userId = intent.extras?.getString("userId")
            Log.d(TAG, "#onCreate.userId = $userId")
            userId?.let {
                FirebaseUtil.allUserCollectionReference().document(it).get().addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Log.d(TAG, "#onCreate.task.isSuccessful == true")
                        val userModel = task.result.toObject(UserModel::class.java)
                        Log.d(TAG, "#onCreate.userModel = ${userModel.toString()}")

                        val mainIntent = Intent(this, MainActivity::class.java).also { intent ->
                            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                        }
                        startActivity(mainIntent)

                        startActivity(
                            Intent(this, ChatActivity::class.java).also { intent ->
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                AndroidUtil.passUserModelAsIntent(intent, userModel)
                            }
                        )
                        finish()
                    }
                    else{
                        Log.d(TAG, "#onCreate.task.isSuccessful == false")
                    }
                }
            }
        }
        else{
            Log.d(TAG, "#onCreate.FirebaseUtil.isLoggedIn() && intent.extras == false")
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
}