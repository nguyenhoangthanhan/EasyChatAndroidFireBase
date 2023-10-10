package com.nguyenhoangthanhan.easychat

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.nguyenhoangthanhan.easychat.adapter.ChatRecyclerAdapter
import com.nguyenhoangthanhan.easychat.databinding.ActivityChatBinding
import com.nguyenhoangthanhan.easychat.model.ChatMessageModel
import com.nguyenhoangthanhan.easychat.model.ChatRoomModel
import com.nguyenhoangthanhan.easychat.model.UserModel
import com.nguyenhoangthanhan.easychat.util.AndroidUtil
import com.nguyenhoangthanhan.easychat.util.FirebaseUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


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
                    "",
                    ""
                )
                FirebaseUtil.getChatRoomReference(chatRoomId).set(chatRoomModel!!)
            }
        }
    }

    private fun initView() {
        binding.tvOtherUsername.text = otherUser.username

        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.userId)?.downloadUrl?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uri = task.result
                AndroidUtil.setProfilePic(
                    binding.root.context,
                    uri,
                    binding.layoutPickProfile.profilePicImageView
                )
            }
        }

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
        chatRoomModel?.lastMessage = message
        FirebaseUtil.getChatRoomReference(chatRoomId).set(chatRoomModel!!)

        val chatMessageModel = ChatMessageModel(FirebaseUtil.currentUserId().toString(), message, Timestamp.now())
        FirebaseUtil.getChatRoomMessageReference(chatRoomId).add(chatMessageModel).addOnCompleteListener {
            if (it.isSuccessful){
                binding.edtMessageInput.setText("")
                sendMessageNotification(message)
            }
        }
    }

    private fun sendMessageNotification(message: String) {
        FirebaseUtil.currentUserDetails()?.get()?.addOnCompleteListener{ task ->
            if (task.isSuccessful){
                val currentUser = task.result.toObject(UserModel::class.java)
                try {
                    val jsonObject = JSONObject()

                    val notificationObject = JSONObject()
                    notificationObject.put("title", currentUser?.username)
                    notificationObject.put("body", message)

                    val dataObject = JSONObject()
                    Log.d(TAG, "#sendMessageNotification.sendUserId = ${currentUser?.userId}")
                    dataObject.put("userId", currentUser?.userId)

                    jsonObject.put("notification", notificationObject)
                    jsonObject.put("data", dataObject)
                    jsonObject.put("to", otherUser.fcmToken)

                    callApi(jsonObject)
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }

    }

    private fun callApi(jsonObject: JSONObject){
        Log.d(TAG, "#callApi")
        val json: MediaType = "application/json; charset=utf-8".toMediaType()
        val client = OkHttpClient()
        val url = "https://fcm.googleapis.com/fcm/send"
        val requestBody = RequestBody.create(json, jsonObject.toString())
        Log.d(TAG, "#jsonObject = ${jsonObject.toString()}")
        val request = Request.Builder()
            .url(url)
            .post(body = requestBody)
            .header("Authorization", "Bearer AAAAty7_dv4:APA91bG_inttiFtaejjjBJpBAFO0qsh7ZSWfszcc7ofBF1tW31YYZuDmTy6fvesM7eQEsYUR443kcPHY83UnW3qqmra4940f1wLx5gN8kQUepU4w6AyVUbA2skCdQbw02Kxc1DfHAVKb")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {

            }
        })
    }

}