package com.zoomore.reelapp.repo.network.user

import com.zoomore.reelapp.models.user.User
import com.zoomore.reelapp.models.video.RemoteVideo
import com.zoomore.reelapp.models.video.VideoType
import com.zoomore.reelapp.repo.network.utils.FirePath
import com.zoomore.reelapp.repo.network.utils.safeAccess
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import timber.log.Timber

@Suppress("EXPERIMENTAL_API_USAGE")
class DefaultUserRepo(
    private val fireAuth: FirebaseAuth = Firebase.auth,
    private val firePath: FirePath = FirePath(),
    private val realFire: FirebaseDatabase = Firebase.database
) : UserRepo {

    override fun doesDeviceHaveAnAccount() = fireAuth.currentUser != null

    override suspend fun getUserProfile(uid: String?) = safeAccess {
        Timber.d("uid is $uid")
        val userProfile = realFire
            .getReference(firePath.getUserInfo(uid ?: ""))
            .get()
            .addOnCompleteListener {
                val user = it.result.getValue<User>()
                Timber.d("user is $user")
            }
            .await()
            .getValue<User>()
        Timber.d("userProfile is $userProfile")
        userProfile
    }

    override suspend fun addUserToDatabase(
        username: String,
        authResult: AuthResult,
        googleProfilePicture: String?
    ) =
        safeAccess {
            val user = User(
                username = username,
                followers = 0,
                following = 0,
                totalLikes = 0,
                profilePictureUrl = googleProfilePicture,
                uid = authResult.user?.uid.toString()
            )

            realFire
                .getReference(firePath.getUserInfo(Firebase.auth.uid ?: ""))
                .setValue(user)
                .await()

            true
        }

    override suspend fun getUserVideos(uid: String?, videoType: VideoType) = safeAccess {
        val listOfUserVideoId = realFire
            .getReference(firePath.getUserVideos(uid ?: "", videoType))
            .get()
            .await()
            .getValue<Map<String, String>>()
            ?.values
            ?.toList() ?: listOf()

        val allVideos = realFire.getReference(firePath.getAllVideosPath())
        listOfUserVideoId.map { videoId ->
            allVideos.child(videoId).get().await().getValue<RemoteVideo>()
        }
    }

    override suspend fun isFollowingAuthor(authorUid: String?): Boolean {
        return authorUid?.let {
            val myFollowingRef = realFire.getReference(firePath.getMyFollowingPath())
            myFollowingRef.child(authorUid).get().await().exists()
        } ?: false
    }

    override suspend fun followAuthor(authorUid: String?) {
        changeAuthorInMyFollowing(authorUid = authorUid, shouldAddAuthor = true)
        changeMeInAuthorFollowers(authorUid = authorUid, shouldAddMe = true)
    }

    override suspend fun unFollowAuthor(authorUid: String?) {
        changeAuthorInMyFollowing(authorUid = authorUid, shouldAddAuthor = false)
        changeMeInAuthorFollowers(authorUid = authorUid, shouldAddMe = false)
    }

    private suspend fun changeAuthorInMyFollowing(authorUid: String?, shouldAddAuthor: Boolean) {
        val myUid = Firebase.auth.uid ?: return

        authorUid?.let {
            changeFollowCount(uid = myUid, field = FOLLOWING, shouldIncrease = shouldAddAuthor)
            val myFollowingRef = realFire.getReference(firePath.getMyFollowingPath())
            myFollowingRef.child(authorUid).setValue(if (shouldAddAuthor) authorUid else null)
        }
    }

    private suspend fun changeMeInAuthorFollowers(authorUid: String?, shouldAddMe: Boolean) {
        val myUid = Firebase.auth.uid ?: return

        authorUid?.let {
            changeFollowCount(uid = authorUid, field = FOLLOWERS, shouldIncrease = shouldAddMe)
            val authorFollowerRef = realFire.getReference(firePath.getUserFollowerPath(authorUid))
            authorFollowerRef.child(myUid).setValue(if (shouldAddMe) myUid else null)
        }
    }

    /**
     * Increases or decreases either the my following count or the author's follower count.
     * This would have been two different functions but to reduce function count, let's use one.
     *
     * @param uid a uid referencing the account data to change
     * @param field indicates whether to change the followers module or the following module
     */
    private suspend fun changeFollowCount(uid: String, field: String, shouldIncrease: Boolean) {
        val databaseReference = realFire
            .getReference("users/$uid")
            .child(field)

        var count = databaseReference.get().await().getValue<Int>() ?: 0
        if (shouldIncrease) count++ else count--

        databaseReference.setValue(count)
    }

    companion object {
        const val FOLLOWERS = "followers"
        const val FOLLOWING = "following"
    }
}