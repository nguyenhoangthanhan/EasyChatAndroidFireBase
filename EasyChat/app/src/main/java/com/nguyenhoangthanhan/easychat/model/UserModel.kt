package com.nguyenhoangthanhan.easychat.model

import com.google.firebase.Timestamp

class UserModel {
    private var createdTimestamp: Timestamp? = null
    private var phone: String? = null
    var username: String? = null

    constructor()
    constructor(phone: String?, username: String?, createdTimestamp: Timestamp?) {
        this.phone = phone
        this.username = username
        this.createdTimestamp = createdTimestamp
    }
}