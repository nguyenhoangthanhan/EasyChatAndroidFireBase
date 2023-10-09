package com.nguyenhoangthanhan.easychat.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.nguyenhoangthanhan.easychat.ChatActivity
import com.nguyenhoangthanhan.easychat.databinding.SearchUserRecyclerRowBinding
import com.nguyenhoangthanhan.easychat.model.UserModel
import com.nguyenhoangthanhan.easychat.util.AndroidUtil
import com.nguyenhoangthanhan.easychat.util.FirebaseUtil


class SearchUserRecyclerAdapter(
    options: FirestoreRecyclerOptions<UserModel>
) : FirestoreRecyclerAdapter<UserModel
        , SearchUserRecyclerAdapter.UserViewModelViewHolder>(options) {

    private var binding: SearchUserRecyclerRowBinding? = null

    inner class UserViewModelViewHolder(private val binding: SearchUserRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: UserModel) {
            binding.tvUsername.text = model.username;
            binding.tvPhoneNumber.text = model.phone;
            if (model.userId?.equals(FirebaseUtil.currentUserId()) == true){
                binding.tvUsername.text = (model.username + " (Me)")
            }
            binding.root.setOnClickListener {
                binding.root.context.startActivity(
                    Intent(binding.root.context, ChatActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        AndroidUtil.passUserModelAsIntent(it, model)
                    })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewModelViewHolder {
        binding = SearchUserRecyclerRowBinding.inflate(LayoutInflater.from(parent.context)
            , parent, false)
        return UserViewModelViewHolder(binding!!)
    }

    override fun onBindViewHolder(
        holder: UserViewModelViewHolder,
        position: Int,
        model: UserModel
    ) {
        holder.bind(model)
    }
}