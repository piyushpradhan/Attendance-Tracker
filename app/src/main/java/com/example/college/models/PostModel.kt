package com.example.college.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class PostModel() : Parcelable{

    var label : String? = null
    var imageUrl : String? = null
    var author : String? = null
    var name : String? = null
    var timestamp : Long? = null
    var date : String? = null
    var likes : Int? = null

    constructor(text : String?, downloadUrl : String?, postAuthor : String?, displayName : String?, postTimestamp : Long?, postDate : String?, mLikes : Int?) : this() {
        this.label = text
        this.imageUrl = downloadUrl
        this.author = postAuthor
        this.name = displayName
        this.timestamp = postTimestamp
        this.date = postDate
        this.likes = mLikes
    }

}