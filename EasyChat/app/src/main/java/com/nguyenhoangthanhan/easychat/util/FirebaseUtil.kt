package com.nguyenhoangthanhan.easychat.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseUtil {

    fun currentUserId(): String?{
        return FirebaseAuth.getInstance().uid
    }

    fun isLoggedIn(): Boolean{
        if(currentUserId() != null){
            return true
        }
        return false
    }

    fun currentUserDetails(): DocumentReference{
        return FirebaseFirestore.getInstance().collection("users").document()
    }

    fun allUserCollectionReference(): CollectionReference{
        return FirebaseFirestore.getInstance().collection("users")
    }
}