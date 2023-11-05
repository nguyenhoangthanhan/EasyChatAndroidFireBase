package com.nguyenhoangthanhan.easychat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.nguyenhoangthanhan.easychat.databinding.ActivityMainBinding
import com.nguyenhoangthanhan.easychat.fragment.ChatFragment
import com.nguyenhoangthanhan.easychat.fragment.ProfileFragment
import com.nguyenhoangthanhan.easychat.util.FirebaseUtil

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity_TAG"

    private var mSelectedNavItem = 1;
    private var CURRENT_SELECTED_FRAGMENT = "current fragment"

    private lateinit var binding: ActivityMainBinding

    private var chatFragment:ChatFragment = ChatFragment()
    private var profileFragment: ProfileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "#onCreate")
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (savedInstanceState != null) {
            //Retrieve the value from the bundle then assign it to mSelectedItem global variable
            val retrieveSelectedNavItem = savedInstanceState.getInt(CURRENT_SELECTED_FRAGMENT);
            mSelectedNavItem = retrieveSelectedNavItem;
            Log.d(TAG, "#onCreate.retrieveSelectedNavItem = $retrieveSelectedNavItem")
        }

        initData()
        initView()
        initEvents()

    }

    private fun initData() {
    }

    private fun initView() {
    }

    private fun initEvents() {
        binding.btnMainSearch.setOnClickListener {
            startActivity(Intent(this@MainActivity, SearchUserActivity::class.java))
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            if (it.itemId == R.id.menu_chat){
                mSelectedNavItem = 1
                supportFragmentManager.beginTransaction().replace(R.id.container, chatFragment).commit()
            }
            else if (it.itemId == R.id.menu_profile){
                mSelectedNavItem = 2
                supportFragmentManager.beginTransaction().replace(R.id.container, profileFragment).commit()
            }
            return@setOnItemSelectedListener true
        }
        if (mSelectedNavItem == 1){
            binding.bottomNavigation.selectedItemId = R.id.menu_chat
        }
        else{
            binding.bottomNavigation.selectedItemId = R.id.menu_profile
        }

        getFCMToken()

    }

    private fun getFCMToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful){
                val token = task.result
                Log.d(TAG, token)
                FirebaseUtil.currentUserDetails()?.update("fcmToken", token)
            }
        }
    }

    override fun onStart() {
        Log.d(TAG, "#onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG, "#onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "#onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "#onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "#onDestroy")
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_SELECTED_FRAGMENT, mSelectedNavItem);
    }
}