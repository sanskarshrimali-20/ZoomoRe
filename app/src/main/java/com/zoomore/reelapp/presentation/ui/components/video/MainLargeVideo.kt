package com.zoomore.reelapp.presentation.ui.components.video

import android.content.Intent
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.zoomore.reelapp.databinding.LargeVideoLayoutBinding
import com.zoomore.reelapp.models.succeeded
import com.zoomore.reelapp.models.user.User
import com.zoomore.reelapp.models.video.RemoteVideo
import com.zoomore.reelapp.presentation.exoplayer.Player
import com.zoomore.reelapp.presentation.ui.components.comment.MainComment
import com.zoomore.reelapp.repo.network.comment.DefaultCommentRepo
import com.zoomore.reelapp.repo.network.user.UserRepo
import com.zoomore.reelapp.repo.network.videos.VideosRepo
import com.zoomore.reelapp.utils.NumbersUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class MainLargeVideo(
    private val scope: CoroutineScope,
    private val lifecycle: Lifecycle,
    private val binding: LargeVideoLayoutBinding,
    private val userRepo: UserRepo,
    private val videosRepo: VideosRepo,
    private val onPersonIconClicked: (String) -> Unit,
    private val onVideoEnded: (Player) -> Unit,
    private val onCommentVisibilityChanged: (Boolean) -> Unit
) {
    var author: User? = null
    var player: Player? = null

    private var likeCount = 0

    private lateinit var mainComment: MainComment

    /**
     * This is livedata instance holds what the user is currently typing and is passed on to MainComment
     */
    val liveUserComment = MutableLiveData("")

    private val _isVideoLiked = MutableLiveData(false)
    val isVideoLiked: LiveData<Boolean> = _isVideoLiked

    private val _isFollowingAuthor = MutableLiveData(true)
    val isFollowingAuthor: LiveData<Boolean> = _isFollowingAuthor

    fun init(remoteVideo: RemoteVideo) {
        scope.launch {
            createProfile(remoteVideo)
            createPlayer(remoteVideo)
            createMainComment(remoteVideo)
            createVideoInfo(remoteVideo)
            setOnClickListeners(remoteVideo)
            enableDoubleTap(remoteVideo)

            isVideoLiked(remoteVideo)
            isFollowingAuthor(remoteVideo.authorUid)
        }
    }

    private fun createVideoInfo(remoteVideo: RemoteVideo) {
        likeCount = remoteVideo.likes.toInt()
        binding.totalVideoLikes.text = NumbersUtils.formatCount(likeCount)
    }

    private fun createMainComment(remoteVideo: RemoteVideo) {
        val commentRepo = DefaultCommentRepo()
        mainComment =
            MainComment(binding, commentRepo, remoteVideo, userRepo, scope, liveUserComment, onCommentVisibilityChanged)
        mainComment.init()
    }

    private suspend fun createProfile(remoteVideo: RemoteVideo) {
        author = userRepo.getUserProfile(remoteVideo.authorUid).tryData()
        Timber.d("author is $author")

        binding.authorUsername.text = author?.username?.let { "@${it}" } ?: "@..."
        Glide.with(binding.root).load(author?.profilePictureUrl).into(binding.authorIcon)
        binding.videoDescription.text = remoteVideo.description ?: "#NoDescription"
    }

    private fun createPlayer(remoteVideo: RemoteVideo) {
        player = Player(
            simpleExoplayerView = binding.simpleExoPlayerView,
            playBtn = binding.playBtn,
            context = binding.root.context,
            url = remoteVideo.url,
            onVideoEnded = { player -> onVideoEnded(player) }
        )
        lifecycle.addObserver(player!!)
        player?.init()
    }

    private fun setOnClickListeners(remoteVideo: RemoteVideo) {
        // TODO: Once the button is clicked, let's show a small pop-up layout that tells him/her to sign up or login
        if (userRepo.doesDeviceHaveAnAccount()) {
            binding.followAuthor.setOnClickListener { followOrUnFollowAuthor() }
            binding.likeVideoIcon.setOnClickListener { likeOrUnlikeVideo(remoteVideo) }
        }
        binding.authorIcon.setOnClickListener { onPersonIconClicked(remoteVideo.authorUid) }
        binding.shareVideoBtn.setOnClickListener {
            // TODO: Create a website that takes in a remote video id and displays them. The website will also check if the
            // device has the app and make an intent to the app. For now, just create a link to the firebase video
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, remoteVideo.url)
            binding.root.context.startActivity(intent)
        }

        binding.bottomAddCommentBtn.setOnClickListener { mainComment.showCommentSection() }
        binding.openCommentSectionBtn.setOnClickListener { mainComment.showCommentSection() }
        binding.exitCommentSectionBtn.setOnClickListener { mainComment.hideCommentSection() }
    }

    private fun enableDoubleTap(remoteVideo: RemoteVideo) {
        val gd = GestureDetector(binding.root.context, object: GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                player?.changePlayerState()
                return true
            }

            override fun onDoubleTap(e: MotionEvent?): Boolean {
                likeOrUnlikeVideo(remoteVideo)
                return true
            }

            override fun onDoubleTapEvent(e: MotionEvent?) = true
        })
        binding.simpleExoPlayerView.setOnTouchListener { view, event ->
            view.performClick()
            return@setOnTouchListener gd.onTouchEvent(event)
        }
    }

    private fun likeOrUnlikeVideo(remoteVideo: RemoteVideo) {
        scope.launch {
            // Change the heart icon
            val shouldLike =
                isVideoLiked.value != true // Simply put if the user already likes the video, dislike it otherwise like it
            _isVideoLiked.value = shouldLike

            // Change like count
            changeLikeCount(shouldLike)

            videosRepo.likeOrUnlikeVideo(
                videoId = remoteVideo.videoId,
                authorId = remoteVideo.authorUid,
                shouldLike = shouldLike
            )
        }
    }

    private fun changeLikeCount(shouldLike: Boolean) {
        if (shouldLike) likeCount++ else likeCount--
        binding.totalVideoLikes.text = NumbersUtils.formatCount(likeCount)
    }

    /**
     * Sets isVideoLiked to true if the user likes the video
     *
     * @param remoteVideo the video currently being displayed to the user
     */
    private suspend fun isVideoLiked(remoteVideo: RemoteVideo) {
        val isLiked = videosRepo.isVideoLiked(remoteVideo.videoId)
        _isVideoLiked.value = isLiked.succeeded && isLiked.forceData()
    }


    /**
     * Sets isFollowingAuthor to true hence removing the icon if we are the video author or we are following the author already
     *
     * @param authorUid the uid of the video author
     */
    private suspend fun isFollowingAuthor(authorUid: String?) {
        _isFollowingAuthor.value =
            (authorUid == Firebase.auth.uid) || (userRepo.isFollowingAuthor(authorUid))
    }

    /**
     * This function adds the author to our following module and adds me to the author's followers module.
     */
    private fun followOrUnFollowAuthor() {
        if (author?.uid != Firebase.auth.uid) { // Author cannot follow or unfollow himself
            scope.launch {
                if (isFollowingAuthor.value == false) {
                    _isFollowingAuthor.value = true
                    userRepo.followAuthor(author?.uid)
                } else {
                    _isFollowingAuthor.value = false
                    userRepo.unFollowAuthor(author?.uid)
                }
            }
        }
    }

    fun destroy() {
        mainComment.destroy()

        player?.let {
            it.stopPlayer()
            lifecycle.removeObserver(it)
            player = null
        }
    }
}