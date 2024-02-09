package com.zoomore.reelapp.repo.network.tag

import com.zoomore.reelapp.models.tag.Tag
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.zoomore.reelapp.repo.network.utils.FirePath
import com.zoomore.reelapp.repo.network.utils.safeAccess
import com.zoomore.reelapp.utils.runAsync
import kotlinx.coroutines.tasks.await

class DefaultTagRepo(private val realFire: FirebaseDatabase = Firebase.database) : TagRepo {
    private val firePath = FirePath()

    override suspend fun fetchPopularTags() = safeAccess {
        val tagList = realFire
            .getReference(firePath.getTagsPath())
            .orderByChild("count")
            .limitToFirst(12)
            .get().await().getValue<List<Tag>>()

        return@safeAccess tagList ?: listOf()
    }

    override suspend fun fetchTagVideos(tagName: String) = safeAccess {
        realFire
            .getReference(firePath.getTagVideos(tagName))
            .limitToFirst(12)
            .get()
            .await()
            .getValue<List<String>>() ?: listOf()
    }

    override fun saveTagsInVideo(tags: Collection<String>, videoId: String) {
        tags.forEach {
            val tagName = it.replace("#", "", true)

            // Increment tag count
            realFire
                .getReference(firePath.getTagInfo(tagName))
                .runAsync {
                    // Get an existing tag or create a new one.
                    val tag = get().await().getValue<Tag>() ?: Tag(tagName, 0)
                    tag.count++

                    setValue(tag)
                }

            realFire
                .getReference(firePath.getTagVideos(tagName))
                .child(videoId)
                .setValue(videoId)
        }
    }
}