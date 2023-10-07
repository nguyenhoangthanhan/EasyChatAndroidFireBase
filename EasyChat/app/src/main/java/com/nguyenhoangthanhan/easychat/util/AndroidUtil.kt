package com.nguyenhoangthanhan.easychat.util

import android.content.Context
import android.widget.Toast

object AndroidUtil {
    public fun showToastShort(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    public fun showToastLong(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}