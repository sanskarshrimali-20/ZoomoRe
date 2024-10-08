package com.zoomore.reelapp.repo.network.user

import com.zoomore.reelapp.models.video.RemoteVideo
import com.zoomore.reelapp.models.TheResult
import com.zoomore.reelapp.models.user.User
import com.zoomore.reelapp.models.video.VideoType
import com.google.firebase.auth.AuthResult

interface UserRepo {
    /**
     * Whether there is an account on the user's device
     *
     * @return true if the device has a current user
     */
    fun doesDeviceHaveAnAccount(): Boolean

    /**
     * Retrieves the user's profile based on the user's uid
     *
     * @return the user profile safely wrapped in a try catch statement
     */
    suspend fun getUserProfile(uid: String?): TheResult<User?>

    /**
     * Checks if the current user is following the video author
     *
     * @return true if the current user is following him/her
     */
    suspend fun isFollowingAuthor(authorUid: String?): Boolean

    /**
     * Saves the user to the database after signing up.
     *
     * @param username the user's username
     * @param authResult the authResult obtained after signing up that is useful for getting the uid
     * @param googleProfilePicture the url path to the user's profile picture. This has a value if Google Sign-In was used
     */
    suspend fun addUserToDatabase(
        username: String,
        authResult: AuthResult,
        googleProfilePicture: String?
    ): TheResult<Boolean>

    /**
     * Follow another user based on their uid
     *
     * @param authorUid reference to the user
     */
    suspend fun followAuthor(authorUid: String?)

    /**
     * Unfollow another user based on their uid
     *
     * @param authorUid reference to the user
     */
    suspend fun unFollowAuthor(authorUid: String?)

    /**
     * Get's the videos of a specific user
     *
     * @param uid reference to the user
     * @param videoType type of videos to fetch whether public, private or liked
     */
    suspend fun getUserVideos(uid: String?, videoType: VideoType): TheResult<List<RemoteVideo?>>
}