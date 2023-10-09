package com.nguyenhoangthanhan.easychat.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.nguyenhoangthanhan.easychat.model.UserModel

object AndroidUtil {
    public fun showToastShort(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    public fun showToastLong(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    public fun passUserModelAsIntent(intent: Intent, userModel: UserModel) {
        intent.putExtra("username", userModel.username)
        intent.putExtra("phone", userModel.phone)
        intent.putExtra("userId", userModel.userId)
    }

    public fun getUserModelFromIntent(intent: Intent): UserModel{
        val userModel = UserModel()
        userModel.username = intent.getStringExtra("username")
        userModel.phone = intent.getStringExtra("phone")
        userModel.userId = intent.getStringExtra("userId")
        return userModel
    }
}