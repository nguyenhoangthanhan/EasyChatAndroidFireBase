package com.nguyenhoangthanhan.easychat.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.nguyenhoangthanhan.easychat.ChatActivity
import com.nguyenhoangthanhan.easychat.databinding.ChatMessageRecyclerRowBinding
import com.nguyenhoangthanhan.easychat.databinding.SearchUserRecyclerRowBinding
import com.nguyenhoangthanhan.easychat.model.ChatMessageModel
import com.nguyenhoangthanhan.easychat.util.AndroidUtil
import com.nguyenhoangthanhan.easychat.util.FirebaseUtil

class ChatRecyclerAdapter(
    options: FirestoreRecyclerOptions<ChatMessageModel>
) : FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatMessageViewHolder>(options) {

    private var binding: ChatMessageRecyclerRowBinding? = null

    inner class ChatMessageViewHolder(private val binding: ChatMessageRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: ChatMessageModel) {
            if (model.senderId.equals(FirebaseUtil.currentUserId())){
                binding.leftChatLayout.visibility = View.GONE
                binding.rightChatLayout.visibility = View.VISIBLE
                binding.rightChatTextview.text = model.message
            }
            else{
                binding.leftChatLayout.visibility = View.VISIBLE
                binding.rightChatLayout.visibility = View.GONE
                binding.leftChatTextview.text = model.message
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        binding = ChatMessageRecyclerRowBinding.inflate(
            LayoutInflater.from(parent.context)
            , parent, false)
        return ChatMessageViewHolder(binding!!)
    }

    override fun onBindViewHolder(
        holder: ChatMessageViewHolder,
        position: Int,
        model: ChatMessageModel
    ) {
        holder.bind(model)
    }
}