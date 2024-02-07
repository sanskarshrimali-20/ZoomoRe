package com.zoomore.reelapp.presentation.ui.components.comment

import android.view.View
import com.zoomore.reelapp.R
import com.zoomore.reelapp.databinding.EachCommentItemBinding
import com.zoomore.reelapp.models.comment.Comment
import com.zoomore.reelapp.models.user.User
import com.zoomore.reelapp.utils.ImageUtils
import com.xwray.groupie.viewbinding.BindableItem

class EachCommentGroup(
    private val comment: Comment,
    private var isLiked: Boolean = false,
    private val commentAuthor: User?,
    private val likeOrUnlikeComment: () -> Unit
) : BindableItem<EachCommentItemBinding>() {
    var likesCount = comment.commentLikes

    override fun bind(binding: EachCommentItemBinding, position: Int) {
        binding.personComment.text = comment.commentText
        binding.likesCount.text = likesCount.toString()

        binding.personUsername.text = commentAuthor?.username
        ImageUtils.loadGlideImage(binding.personImage, commentAuthor?.profilePictureUrl)

        binding.likeIconImage.setOnClickListener {
            likeOrUnlikeComment()
            isLiked = !isLiked
        }
    }


    override fun initializeViewBinding(view: View) =
        EachCommentItemBinding.bind(view)

    override fun getLayout(): Int = R.layout.each_comment_item

}