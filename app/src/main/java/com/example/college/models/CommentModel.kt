package com.example.college.models

class CommentModel() {

    var comment : String? = null
    var author : String? = null
    var uid : String? = null
    var dpUrl : String? = null
    var timestamp : Long? = null


    constructor(mComment: String?, mAuthor : String?, muid : String?, mDpUrl : String?, mTimestamp: Long?) : this() {
        this.comment = mComment
        this.author = mAuthor
        this.uid = muid
        this.dpUrl = mDpUrl
        this.timestamp = mTimestamp
    }

}