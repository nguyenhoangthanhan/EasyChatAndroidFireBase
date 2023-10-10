package com.nguyenhoangthanhan.easychat.model

import com.google.firebase.Timestamp

class UserModel {
    var createdTimestamp: Timestamp? = null
    var phone: String? = null
    var username: String? = null
    var userId: String? = null
    var fcmToken: String? = null

    constructor()
    constructor(phone: String?, username: String?
                , createdTimestamp: Timestamp?, userId: String?, fcmToken: String?) {
        this.phone = phone
        this.username = username
        this.createdTimestamp = createdTimestamp
        this.userId = userId
        this.fcmToken = fcmToken
    }
}