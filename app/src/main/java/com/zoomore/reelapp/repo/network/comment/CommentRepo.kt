package com.zoomore.reelapp.repo.network.comment

import androidx.lifecycle.LiveData
import com.zoomore.reelapp.models.comment.Comment
import com.zoomore.reelapp.utils.map.SmartMap


interface CommentRepo {
    /**
     * Adds a comment to the video
     *
     * @param commentText message in the comment
     * @param videoId reference to the video
     */
    fun sendComment(commentText: String, videoId: String)
    fun deleteComment(videoId: String)

    /**
     * Fetches comments for the specified video. The @link [ChildEventListener] will ensure that our map of Comments is always up to date.
     *
     * @param videoId a reference to the video
     * @return an observable array map containing the video's comments
     */
    fun fetchComments(videoId: String): SmartMap<String, Comment>

    /**
     * Emits the total comment size of the video. Given the fact that we are using @link [ValueEventListener], any changes to the comment size
     * is reflected to the onDataChange function.
     *
     * @param videoId a reference to the video
     * @return a liveData instance containing the total number of comments in that video
     */
    fun getTotalCommentsSize(videoId: String): LiveData<Int>

    /**
     * Checks if the comment is in the user's liked comments
     *
     * @param videoId A reference to the video
     * @param commentId A reference to the comment
     * @return Whether the comment has been liked by the user
     */
    suspend fun isCommentLiked(videoId: String, commentId: String): Boolean

    /**
     * Either likes or unlikes the comment
     *
     * @param videoId A reference to the video
     * @param commentId A reference to the comment
     */
    suspend fun likeOrUnlikeComment(videoId: String, commentId: String)
}