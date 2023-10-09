package com.nguyenhoangthanhan.easychat.model

import com.google.firebase.Timestamp

class ChatMessageModel {
    var senderId: String? = null
    var message: String? = null
    var timestamp: Timestamp? = null

    constructor()
    constructor(senderId: String?, message: String?
                , timestamp: Timestamp?) {
        this.senderId = senderId
        this.message = message
        this.timestamp = timestamp
    }
}