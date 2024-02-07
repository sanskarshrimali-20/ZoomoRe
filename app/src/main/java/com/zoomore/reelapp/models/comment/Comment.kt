package com.zoomore.reelapp.models.comment

import com.zoomore.reelapp.models.reply.Reply


data class Comment(
    val authorUid: String,
    var commentText: String,
    var commentLikes: Long,
    val replies: ArrayList<Reply>,
    val dateCreated: Long,
    val commentId: String
) {
    constructor() : this("", "", -1, arrayListOf(), -1, "")
}