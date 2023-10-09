package com.nguyenhoangthanhan.easychat

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.nguyenhoangthanhan.easychat.adapter.ChatRecyclerAdapter
import com.nguyenhoangthanhan.easychat.databinding.ActivityChatBinding
import com.nguyenhoangthanhan.easychat.model.ChatMessageModel
import com.nguyenhoangthanhan.easychat.model.ChatRoomModel
import com.nguyenhoangthanhan.easychat.model.UserModel
import com.nguyenhoangthanhan.easychat.util.AndroidUtil
import com.nguyenhoangthanhan.easychat.util.FirebaseUtil

class ChatActivity : AppCompatActivity() {

    private val TAG = "ChatActivity_TAG"

    private lateinit var binding: ActivityChatBinding

    private lateinit var otherUser: UserModel

    private var chatRoomModel: ChatRoomModel? = null

    private lateinit var chatRoomId: String

    private var adapter: ChatRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initData()
        initView()
        initEvents()
    }

    private fun initData() {
        //get UserModel from Intent\
        otherUser = AndroidUtil.getUserModelFromIntent(intent)
        chatRoomId = FirebaseUtil.getChatRoomId(FirebaseUtil.currentUserId(), otherUser.userId)

        getOrCreateChatRoomModel()
    }

    private fun getOrCreateChatRoomModel() {
        FirebaseUtil.getChatRoomReference(chatRoomId).get().addOnCompleteListener {
            chatRoomModel = it.result.toObject(ChatRoomModel::class.java)
            Log.d(TAG, "#initData.it.result.toObject(ChatRoomModel::class.java) = "
                    + chatRoomModel)
            if (chatRoomModel == null){
                chatRoomModel = ChatRoomModel(
                    chatRoomId,
                    listOf(FirebaseUtil.currentUserId(), otherUser.userId),
                    Timestamp.now(),
                    ""
                )
                FirebaseUtil.getChatRoomReference(chatRoomId).set(chatRoomModel!!)
            }
        }
    }

    private fun initView() {
        binding.tvOtherUsername.text = otherUser.username

        setUpChatRecyclerView()
    }

    private fun setUpChatRecyclerView() {
        val query = FirebaseUtil.getChatRoomMessageReference(chatRoomId)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val options: FirestoreRecyclerOptions<ChatMessageModel> = FirestoreRecyclerOptions.Builder<ChatMessageModel>()
            .setQuery(query, ChatMessageModel::class.java).build()

        adapter = ChatRecyclerAdapter(options)
        binding.rvChat.layoutManager = LinearLayoutManager(this).also { it.reverseLayout = true }
        binding.rvChat.adapter = adapter
        adapter?.startListening()
        adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver(){
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.rvChat.smoothScrollToPosition(0)
            }
        })
    }

    private fun initEvents() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnSendMessage.setOnClickListener {
            val message = binding.edtMessageInput.text.toString()
            if (message.isEmpty()){
                return@setOnClickListener
            }
            sendMessageToUser(message)
        }
    }

    private fun sendMessageToUser(message: String) {
        chatRoomModel?.lastMessageTimeStamp = Timestamp.now()
        chatRoomModel?.lastMessageSenderId = FirebaseUtil.currentUserId().toString()
        FirebaseUtil.getChatRoomReference(chatRoomId).set(chatRoomModel!!)

        val chatMessageModel = ChatMessageModel(FirebaseUtil.currentUserId().toString(), message, Timestamp.now())
        FirebaseUtil.getChatRoomMessageReference(chatRoomId).add(chatMessageModel).addOnCompleteListener {
            if (it.isSuccessful){
                binding.edtMessageInput.setText("")
            }
        }
    }
}