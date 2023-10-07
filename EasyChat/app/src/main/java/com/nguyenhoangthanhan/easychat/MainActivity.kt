package com.nguyenhoangthanhan.easychat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nguyenhoangthanhan.easychat.databinding.ActivityMainBinding
import com.nguyenhoangthanhan.easychat.fragment.ChatFragment
import com.nguyenhoangthanhan.easychat.fragment.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var chatFragment:ChatFragment
    private lateinit var profileFragment: ProfileFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initData()
        initView()
        initEvents()

    }

    private fun initData() {
    }

    private fun initView() {
        chatFragment = ChatFragment()
        profileFragment = ProfileFragment()
    }

    private fun initEvents() {
        binding.btnMainSearch.setOnClickListener {
            startActivity(Intent(this@MainActivity, SearchUserActivity::class.java))
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            if (it.itemId == R.id.menu_chat){
                supportFragmentManager.beginTransaction().replace(R.id.container, chatFragment).commit()
            }
            else if (it.itemId == R.id.menu_profile){
                supportFragmentManager.beginTransaction().replace(R.id.container, profileFragment).commit()
            }
            return@setOnItemSelectedListener true
        }
        binding.bottomNavigation.selectedItemId = R.id.menu_chat

    }
}