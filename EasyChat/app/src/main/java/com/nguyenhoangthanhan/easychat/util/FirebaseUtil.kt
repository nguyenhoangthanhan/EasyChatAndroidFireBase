package com.nguyenhoangthanhan.easychat.util

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Locale

object FirebaseUtil {

    private val TAG = "FirebaseUtil_TAG"

    fun currentUserId(): String? {
        return FirebaseAuth.getInstance().uid
    }

    fun isLoggedIn(): Boolean{
        if(currentUserId() != null){
            return true
        }
        return false
    }

    fun currentUserDetails(): DocumentReference?{
        Log.d(TAG, "#currentUserDetails.currentUserId = =" + currentUserId())
        return currentUserId()?.let {
            FirebaseFirestore.getInstance().collection("users").document(
                it
            )
        }
    }

    fun allUserCollectionReference(): CollectionReference{
        return FirebaseFirestore.getInstance().collection("users")
    }

    fun getChatRoomReference(chatRoomId: String): DocumentReference{
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatRoomId)
    }

    fun getChatRoomMessageReference(chatRoomId: String): CollectionReference{
        return getChatRoomReference(chatRoomId).collection("chats")
    }

    fun getChatRoomId(userId1:String?, userId2:String?): String {
        return if (userId1.hashCode() < userId2.hashCode()){
            userId1 + "_" + userId2
        } else{
            userId2 + "_" + userId1
        }
    }

    fun allChatRoomCollectionReference(): CollectionReference{
        return FirebaseFirestore.getInstance().collection("chatrooms")
    }

    fun getOtherUserFromChatRoom(userIds: List<String?>?): DocumentReference{
        return if (userIds?.get(0)?.equals(currentUserId()) == true){
            allUserCollectionReference().document(userIds[1]!!)
        } else{
            allUserCollectionReference().document(userIds?.get(0)!!)
        }
    }

    fun timestampToString(timestamp: Timestamp?): String {
        return timestamp?.toDate()?.let { SimpleDateFormat("HH:MM", Locale.US).format(it) } ?: "null"
    }

    fun logout(){
        FirebaseAuth.getInstance().signOut()
    }

    fun getCurrentProfilePicStorageRef(): StorageReference? {
        return currentUserId()?.let {
            FirebaseStorage.getInstance().reference.child("profile_pic")
                .child(it)
        }
    }

    fun getOtherProfilePicStorageRef(otherUserId: String?): StorageReference? {
        return otherUserId?.let {
            FirebaseStorage.getInstance().reference.child("profile_pic")
                .child(it)
        }
    }
}