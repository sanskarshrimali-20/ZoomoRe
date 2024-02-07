package com.zoomore.reelapp.repo.network.tag

import com.zoomore.reelapp.models.TheResult
import com.zoomore.reelapp.models.tag.Tag


interface TagRepo {
    fun saveTagsInVideo(tags: Collection<String>, videoId: String)

    suspend fun fetchPopularTags(): TheResult<List<Tag>>

    suspend fun fetchTagVideos(tagName: String): TheResult<List<String>>
}