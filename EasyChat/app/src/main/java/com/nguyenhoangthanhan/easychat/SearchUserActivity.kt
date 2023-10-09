package com.nguyenhoangthanhan.easychat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.nguyenhoangthanhan.easychat.adapter.SearchUserRecyclerAdapter
import com.nguyenhoangthanhan.easychat.databinding.ActivitySearchUserBinding
import com.nguyenhoangthanhan.easychat.model.UserModel
import com.nguyenhoangthanhan.easychat.util.FirebaseUtil

class SearchUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchUserBinding

    private var adapter: SearchUserRecyclerAdapter? = null

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
        val query = FirebaseUtil.allUserCollectionReference()
            .whereGreaterThanOrEqualTo("username", searchTerm)

        val options: FirestoreRecyclerOptions<UserModel> = FirestoreRecyclerOptions.Builder<UserModel>()
            .setQuery(query, UserModel::class.java).build()

        adapter = SearchUserRecyclerAdapter(options)
        binding.rvSearchUsers.layoutManager = LinearLayoutManager(this)
        binding.rvSearchUsers.adapter = adapter
        adapter?.startListening()
    }

    override fun onStart() {
        super.onStart()
        if (adapter != null){
            adapter?.startListening()
        }
    }

    override fun onResume() {
        super.onResume()
        if (adapter != null){
            adapter?.startListening()
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        if (adapter != null){
            adapter?.stopListening()
        }
    }
}