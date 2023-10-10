package com.nguyenhoangthanhan.easychat.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nguyenhoangthanhan.easychat.model.UserModel

object AndroidUtil {
    fun showToastShort(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showToastLong(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun passUserModelAsIntent(intent: Intent, userModel: UserModel?) {
        intent.putExtra("username", userModel?.username)
        intent.putExtra("phone", userModel?.phone)
        intent.putExtra("userId", userModel?.userId)
    }

    fun getUserModelFromIntent(intent: Intent): UserModel{
        val userModel = UserModel()
        userModel.username = intent.getStringExtra("username")
        userModel.phone = intent.getStringExtra("phone")
        userModel.userId = intent.getStringExtra("userId")
        return userModel
    }

    fun setProfilePic(context: Context, uri: Uri?, imageView: ImageView){
        Glide.with(context).load(uri).apply(RequestOptions.circleCropTransform()).into(imageView)
    }
}