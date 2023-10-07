package com.nguyenhoangthanhan.easychat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nguyenhoangthanhan.easychat.databinding.ActivitySearchUserBinding

class SearchUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initData()
        initView()
        initEvents()

    }

    private fun initData() {

    }

    private fun initView() {

    }

    private fun initEvents() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnSearchUser.setOnClickListener {
            val searchTerm = binding.edtSearchUsername.text.toString()
            if (searchTerm.isEmpty() || searchTerm.length < 3){
                binding.edtSearchUsername.error = "Invalid Username"
                return@setOnClickListener
            }
            setUpSearchRecycleView(searchTerm)
        }
    }

    private fun setUpSearchRecycleView(searchTerm: String) {

    }
}