package com.nguyenhoangthanhan.easychat.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.nguyenhoangthanhan.easychat.adapter.RecentChatRecyclerAdapter
import com.nguyenhoangthanhan.easychat.adapter.SearchUserRecyclerAdapter
import com.nguyenhoangthanhan.easychat.databinding.FragmentChatBinding
import com.nguyenhoangthanhan.easychat.model.ChatRoomModel
import com.nguyenhoangthanhan.easychat.model.UserModel
import com.nguyenhoangthanhan.easychat.util.FirebaseUtil

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding

    private var adapter: RecentChatRecyclerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        initData()
        initView()
        initEvents()

        return binding.root
    }

    private fun initData() {

    }

    private fun initView() {
        setUpSearchRecycleView()
    }

    private fun initEvents() {

    }

    private fun setUpSearchRecycleView() {
        val query = FirebaseUtil.currentUserId()?.let {
            FirebaseUtil.allChatRoomCollectionReference()
                .whereArrayContains("userIds", it)
                .orderBy("lastMessageTimeStamp", Query.Direction.DESCENDING)
        }

        val options: FirestoreRecyclerOptions<ChatRoomModel> =
            query?.let {
                FirestoreRecyclerOptions.Builder<ChatRoomModel>()
                    .setQuery(it, ChatRoomModel::class.java).build()
            } as FirestoreRecyclerOptions<ChatRoomModel>

        adapter = RecentChatRecyclerAdapter(options)
        binding.rvChatInfo.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChatInfo.adapter = adapter
        adapter?.startListening()
    }

    override fun onStart() {
        super.onStart()
        if (adapter != null) {
            adapter?.startListening()
        }
    }

    override fun onResume() {
        super.onResume()
        if (adapter != null) {
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        if (adapter != null) {
            adapter?.stopListening()
        }
    }
}