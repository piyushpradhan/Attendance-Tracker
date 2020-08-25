package com.example.college.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class NoticeModel() : Parcelable{
    var title: String? = null
    var content: String? = null
    var author: String? = null
    var profileUrl : String? = ""
    var timestamp : Long? = null
    var displayName : String? = null


    constructor(heading: String?, desc: String?, uid: String?, dpUrl : String?, time : Long, name : String) : this() {
        this.title = heading
        this.content = desc
        this.author = uid
        this.profileUrl = dpUrl
        this.timestamp = time
        this.displayName = name
    }

}

