package com.zoomore.reelapp.repo.network.videos

import com.zoomore.reelapp.models.TheResult
import com.zoomore.reelapp.models.video.RemoteVideo

interface VideosRepo {
    /**
     * Fetches videos from Firebase database but limits it to 12 to avoid use of too much network resources
     * @return A custom result containing a list of {@link [com.andre_max.tiktokclone.models.video.RemoteVideo]}
     */
    suspend fun fetchRandomVideos(): TheResult<List<RemoteVideo>>

    /**
     * Fetches a specific video based on the video id.
     *
     * @param videoId a reference to the video
     */
    suspend fun fetchVideo(videoId: String): TheResult<RemoteVideo?>

    /**
     * This function checks if the user likes the video. One might suggest that we use a
     * ValueEventListener and return a liveData but that would be really intensive
     * in a scenario where we have 15 videos. However, that's my opinion, what do you think(Drop a PR).
     *
     * @param videoId a reference to the video
     */
    suspend fun isVideoLiked(videoId: String): TheResult<Boolean>

    /**
     * This function does 3 things (Uncle Bob is about to get mad since I'm breaking the Clean Code Conduct).
     * First, it adds or removes the video to my liked videos.
     * Second, it changes the video's like count
     * Third, it changes the author's total likes count
     *
     * @param videoId A reference to the video
     * @param authorId A reference to the video's author
     * @return Whether the video has been liked by the user
     */
    suspend fun likeOrUnlikeVideo(videoId: String, authorId: String, shouldLike: Boolean)

    /**
     * This function saves the video to the database. It uploads the actual video to the
     * main videos/ path then it uploads the video uid to the users/$userUid/videos path for
     * two way storage
     *
     * @param isPrivate whether to make the video private or not
     * @param videoUrl the url pointing to the video location in Firebase Storage
     * @param descriptionText the video description
     */
    suspend fun saveVideoToFireDB(
        isPrivate: Boolean,
        videoUrl: String,
        descriptionText: String,
        tags: Map<String, String>,
        duration: Long?,
        onComplete: (Boolean) -> Unit
    )
}