package com.nguyenhoangthanhan.easychat.model

import com.google.firebase.Timestamp

class ChatRoomModel{
    var chatRoomId: String? = null
    var userIds: List<String?>? = null
    var lastMessageTimeStamp: Timestamp? = null
    var lastMessageSenderId: String? = null
    var lastMessage: String? = null

    constructor()
    constructor(chatRoomId: String?, userIds: List<String?>?, lastMessageTimeStamp: Timestamp?
                , lastMessageSenderId: String?, lastMessage: String?) {
        this.chatRoomId = chatRoomId
        this.userIds = userIds
        this.lastMessageTimeStamp = lastMessageTimeStamp
        this.lastMessageSenderId = lastMessageSenderId
        this.lastMessage = lastMessage
    }

}