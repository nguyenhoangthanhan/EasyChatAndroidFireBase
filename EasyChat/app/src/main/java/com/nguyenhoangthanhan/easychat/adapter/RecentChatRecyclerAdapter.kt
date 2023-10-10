package com.nguyenhoangthanhan.easychat.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.nguyenhoangthanhan.easychat.ChatActivity
import com.nguyenhoangthanhan.easychat.databinding.RecentChatRecyclerRowBinding
import com.nguyenhoangthanhan.easychat.model.ChatRoomModel
import com.nguyenhoangthanhan.easychat.model.UserModel
import com.nguyenhoangthanhan.easychat.util.AndroidUtil
import com.nguyenhoangthanhan.easychat.util.FirebaseUtil

class RecentChatRecyclerAdapter(
    options: FirestoreRecyclerOptions<ChatRoomModel>
) : FirestoreRecyclerAdapter<ChatRoomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder>(
    options
) {

    private val TAG = "RecentChatRecyclerAdapter_TAG"

    private var binding: RecentChatRecyclerRowBinding? = null

    inner class ChatroomModelViewHolder(private val binding: RecentChatRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: ChatRoomModel) {
            FirebaseUtil.getOtherUserFromChatRoom(model.userIds).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val lastMessageSendByMe =
                        model.lastMessageSenderId.equals(FirebaseUtil.currentUserId())

                    Log.d(TAG, "#ChatroomModelViewHolder.bind.isSuccessful = true")
                    val otherUserModel = it.result.toObject(UserModel::class.java)
                    Log.d(
                        TAG,
                        "#ChatroomModelViewHolder.bind." +
                                "isSuccessful.it.result.toObject(UserModel::class.java) = $otherUserModel"
                    )

                    FirebaseUtil.getOtherProfilePicStorageRef(otherUserModel?.userId)?.downloadUrl?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uri = task.result
                            AndroidUtil.setProfilePic(
                                binding.root.context,
                                uri,
                                binding.imgAvatarUser
                            )
                        }
                    }

                    binding.tvUsername.text = otherUserModel?.username
                    if (lastMessageSendByMe) {
                        binding.tvLastMessage.text = "You: " + model.lastMessage
                    } else {
                        binding.tvLastMessage.text = model.lastMessage
                    }
                    binding.tvLastMessageTime.text =
                        FirebaseUtil.timestampToString(model.lastMessageTimeStamp)

                    binding.root.setOnClickListener {
                        Log.d(TAG, "#ChatroomModelViewHolder.bind.binding.root.setOnClickListener")

                        binding.root.context.startActivity(
                            Intent(binding.root.context, ChatActivity::class.java).also { intent ->
                                Log.d(
                                    TAG,
                                    "#ChatroomModelViewHolder.bind.binding.root.setOnClickListener.intent"
                                )
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                AndroidUtil.passUserModelAsIntent(intent, otherUserModel)
                            })
                    }
                } else {
                    Log.d(TAG, "#ChatroomModelViewHolder.bind.isSuccessful = false")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatroomModelViewHolder {
        binding = RecentChatRecyclerRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ChatroomModelViewHolder(binding!!)
    }

    override fun onBindViewHolder(
        holder: ChatroomModelViewHolder,
        position: Int,
        model: ChatRoomModel
    ) {
        holder.bind(model)
    }
}